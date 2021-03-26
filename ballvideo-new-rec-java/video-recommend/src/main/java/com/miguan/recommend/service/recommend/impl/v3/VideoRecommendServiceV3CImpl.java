package com.miguan.recommend.service.recommend.impl.v3;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.miguan.recommend.bo.*;
import com.miguan.recommend.common.constants.ExistConstants;
import com.miguan.recommend.common.constants.RedisRecommendConstants;
import com.miguan.recommend.common.constants.RedisUserConstant;
import com.miguan.recommend.common.constants.XyConstants;
import com.miguan.recommend.entity.mongo.IncentiveVideoHotspot;
import com.miguan.recommend.entity.mongo.UserRawTags;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.service.BloomFilterService;
import com.miguan.recommend.service.RedisService;
import com.miguan.recommend.service.mongo.UserRawTagsService;
import com.miguan.recommend.service.recommend.*;
import com.miguan.recommend.service.xy.VideosCatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service(value = "videoRecommendServiceV3C")
public class VideoRecommendServiceV3CImpl extends AbstractRecommendService implements VideoRecommendService<VideoRecommendDto> {

    @Resource(name = "recDB9Pool")
    private JedisPool recDB9Pool;
    @Resource
    private VideosCatService videosCatService;
    @Resource(name = "videoHotServiceV3")
    private VideoHotService videoHotServiceV3;
    @Resource(name = "incentiveVideoHotServiceV3New")
    private IncentiveVideoHotService incentiveVideoHotServiceV3New;
    @Resource
    private BloomFilterService bloomFilterService;
    @Autowired
    private PredictService predictService;
    @Resource
    private FeatureService featureService;
    @Resource
    private EmbeddingService embeddingService;
    @Resource(name = "redisDB11Service")
    private RedisService redis11Service;
    @Resource
    private UserRawTagsService userRawTagsService;

    //private static ExecutorService executor = new ThreadPoolExecutor(200, 2000, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5000));

    @Override
    public void recommend(BaseDto baseDto, VideoRecommendDto recommendDto) {
        PublicInfo publicInfo = baseDto.getPublicInfo();
        this.initRecommendParam(baseDto, recommendDto);

        boolean needFilter = false;
        // 判断是否是新用户
        int cacheExpireTime = publicInfo.isNew() ? 30 : 60;
        List<Object> videoInfoForRecList = null;
        try (Jedis con = recDB9Pool.getResource()) {
            String key = String.format(RedisRecommendConstants.key_user_rec_video_list, publicInfo.getUuid());
            String cacheList = con.get(key);
            if (cacheList == null || cacheList.isEmpty()) {
                videoInfoForRecList = getAsync(baseDto, recommendDto);
                String cacheValue = JSON.toJSONString(videoInfoForRecList);
                con.setex(key, cacheExpireTime, cacheValue);
            } else {
                videoInfoForRecList = (List<Object>) JSON.parse(cacheList);
                needFilter = true;
            }
        }

        Map<String, BigDecimal> videoPlayRateMap = (Map<String, BigDecimal>) videoInfoForRecList.get(0);
        Map<String, Integer> videoCatMap = (Map<String, Integer>) videoInfoForRecList.get(1);
        if (needFilter) {
            List<IncentiveVideoHotspot> jlVideo = new ArrayList<>();
            videoPlayRateMap = filterSortedMap(videoPlayRateMap, baseDto.getUuid());
            CompletableFuture<Integer> incetiveHotVideo = getIncetiveHotVideo(baseDto, recommendDto, jlVideo);
            incetiveHotVideo.join();
            recommendDto.getJlvideo().addAll(jlVideo.stream().map(IncentiveVideoHotspot::getVideo_id).collect(Collectors.toList()));
            recommendDto.setJlvideoCat(jlVideo.stream().collect(Collectors.toMap(IncentiveVideoHotspot::getVideo_id, IncentiveVideoHotspot::getCatid)));
        }

        if (videoInfoForRecList == null || videoInfoForRecList.isEmpty()) {
            return;
        }

        int hotVideoReturnCount = recommendDto.getVideoNum();
        if(isEmpty(recommendDto.getJlvideo())){
            ++hotVideoReturnCount;
        }

        // 分类对应的视频列表Map
        Map<Integer, List<String>> catVideoListMap = predictService.videoTopK(videoPlayRateMap, videoCatMap);
        // 最终推荐的视频集合
        List<String> recVideoList = new ArrayList<>();
        recommendDto.getRecommendCatNumMap().forEach((key, value) -> {
            List<String> catVideoList = catVideoListMap.get(key);
            if(!isEmpty(catVideoList)){
                recVideoList.addAll(catVideoList.subList(0, Math.min(value, catVideoList.size())));
            }
        });

        // 如果最终推荐的视频的个数，不满足需要返回的个数，补足
        if(recVideoList.size() < hotVideoReturnCount){
            // 按照排序取出剩余需要返回的视频
            List<String> videoSortList = predictService.videoTopK(videoPlayRateMap, videoCatMap, hotVideoReturnCount, 3, 2);
            videoSortList.removeAll(recVideoList);
            recVideoList.addAll(videoSortList.subList(0, hotVideoReturnCount - recVideoList.size()));
        }


        recommendDto.setRecommendVideo(recVideoList);
        recommendDto.setRecommendVideoCat(videoCatMap);
        log.warn("推荐0.3C 此次给uuid({})推荐了 {} 个视频，推荐视频ID集：{}, 激励视频 {} 个, 激励视频ID：{}",
                publicInfo.getUuid(), recVideoList.size(), JSON.toJSONString(recVideoList), recommendDto.getJlvideo().size(), JSON.toJSONString(recommendDto.getJlvideo()));

        // 将查询的视频放入Bloom过滤器
        bloomVideo(baseDto.getUuid(), recVideoList, recommendDto.getJlvideo());
        featureService.saveFeatureToRedis(baseDto, recommendDto);
    }

    public List<Object> getAsync(BaseDto baseDto, VideoRecommendDto recommendDto) {
        Map<Integer, List<VideoHotspotVo>> userCatVideoMap = new LinkedHashMap<>();


        // 获取用户的兴趣分类视频
        CompletableFuture<Void> userCatVideoFuture = getHotVideo(baseDto, recommendDto, userCatVideoMap);
        // 获取激励视频
        List<IncentiveVideoHotspot> jlVideo = Lists.newArrayList();
        CompletableFuture<Integer> jlVideoFuture = null;
        if (recommendDto.getIncentiveVideoNum() > 0) {
            jlVideoFuture = getIncetiveHotVideo(baseDto, recommendDto, jlVideo);
        }

        CompletableFuture<Void> allFuture = null;
        if (recommendDto.getIncentiveVideoNum() == 0) {
            allFuture = CompletableFuture.allOf(userCatVideoFuture);
        } else {
            allFuture = CompletableFuture.allOf(userCatVideoFuture, jlVideoFuture);
        }
        long pt = System.currentTimeMillis();
        allFuture.join();

        // 所有用户兴趣分类视频和离线视频
        List<VideoHotspotVo> allVideos = new ArrayList<VideoHotspotVo>();
        Map<String, Integer> userCatVideoAndOfflineVideoMap = new LinkedHashMap<>();
        int catSum = 0;
        if ("2".equals(baseDto.getEmbeddingGroup())) {
            //双塔向量视频召回
            List<VideoHotspotVo> videoEmbedding = embeddingService.findFromEsVideoEmbedding(baseDto.publicInfo, allVideos);
            if (!isEmpty(videoEmbedding)) {
                Map<Integer, List<VideoHotspotVo>> embeddingCatVideoMap = videoEmbedding.stream().collect(Collectors.groupingBy(VideoHotspotVo::getCatid));
                userCatVideoMap = this.mergeEmbeddingMap(userCatVideoMap, embeddingCatVideoMap);
            }
        }
        for (Integer cat : userCatVideoMap.keySet()) {
            List<VideoHotspotVo> catVideos = userCatVideoMap.get(cat);
            if (isEmpty(catVideos)) {
                continue;
            }
            //catVideos = catVideos.stream().filter(e -> !recommendDto.getExcludeCatList().contains(e.getCatid())).collect(Collectors.toList());
            catSum += catVideos.size();
            allVideos.removeAll(catVideos);
            allVideos.addAll(catVideos);
            catVideos.stream().forEach(e -> {
                userCatVideoAndOfflineVideoMap.put(e.getVideo_id(), e.getCatid());
            });
        }


        // 去重后得所有视频ID
        long pt2 = System.currentTimeMillis();
        log.warn("{} 推荐0.3C 时长，召回：{}，召回数量：{}，其中热门召回：{}，协同过滤召回：{}",
                baseDto.getUuid(), (pt2 - pt), allVideos.size(), catSum, 0);

        // 获取排序值并排序
        Map<String, BigDecimal> sortedVideoMap = predictService.getVideoListPlayRate(baseDto, allVideos);
        long pt3 = System.currentTimeMillis();

        recommendDto.getJlvideo().addAll(jlVideo.stream().map(IncentiveVideoHotspot::getVideo_id).collect(Collectors.toList()));
        recommendDto.setJlvideoCat(jlVideo.stream().collect(Collectors.toMap(IncentiveVideoHotspot::getVideo_id, IncentiveVideoHotspot::getCatid)));
        log.info("推荐0.3C 时长，排序总时长：" + (pt3 - pt2));
        return Arrays.asList(sortedVideoMap, userCatVideoAndOfflineVideoMap);
    }

    /**
     * 第二个map合并到第一个map
     *
     * @param userCatVideoMap      热门召回
     * @param embeddingCatVideoMap 向量召回
     * @return
     */
    private Map<Integer, List<VideoHotspotVo>> mergeEmbeddingMap(Map<Integer, List<VideoHotspotVo>> userCatVideoMap, Map<Integer, List<VideoHotspotVo>> embeddingCatVideoMap) {
        if (isEmpty(embeddingCatVideoMap)) {
            return userCatVideoMap;
        }
        for (Map.Entry<Integer, List<VideoHotspotVo>> entry : embeddingCatVideoMap.entrySet()) {
            List<VideoHotspotVo> embeddingVideos = entry.getValue();
            Integer catId = entry.getKey();
            if (isEmpty(embeddingVideos)) {
                continue;
            }
            List<VideoHotspotVo> catVideos = userCatVideoMap.get(catId);
            if (isEmpty(catVideos)) {
                catVideos = new ArrayList<>();
            }
            catVideos.addAll(embeddingVideos);
            userCatVideoMap.put(catId, catVideos);
        }
        return userCatVideoMap;
    }

    /**
     * 通过用户的兴趣分类获取视频ID
     *
     * @param recommendDto
     * @return
     */
    public CompletableFuture<Void> getHotVideo(BaseDto baseDto, VideoRecommendDto recommendDto, Map<Integer, List<VideoHotspotVo>> userCatVideo) {
        String uuid = baseDto.getUuid();
        List<Integer> catIds = recommendDto.getRecommendCat();
        // 计算用户的兴趣分类，每个分类的个数
        Map<Integer, Long> catNum = new HashMap<Integer, Long>();

        long catFindCount = 100;
        if ("2".equals(baseDto.getEmbeddingGroup())) {
            catFindCount = 20;
        }
        for (Integer catId : catIds) {
            if (catFindCount <= 20) {
                catFindCount = 20;
            }
            catNum.put(catId, catFindCount);
            catFindCount -= 20;
        }
        Function<Integer[], Number> f = e -> {
            long pt = System.currentTimeMillis();
            VideoQueryDto<VideoHotspotVo> queryDto = new VideoQueryDto<VideoHotspotVo>(baseDto, e[0], recommendDto.getSensitiveState(), e[1]);
            if (baseDto.isVideo98Group()) {
                queryDto.setExcludedSource(excludeSource);
            }
            List<VideoHotspotVo> videoId1 = videoHotServiceV3.findAndFilter(queryDto, null);
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.3C 获取用户兴趣分类[{}]视频：{} 个", uuid, e[0], isEmpty(videoId1) ? 0 : videoId1.size());
            }
            if (!isEmpty(videoId1)) {
                userCatVideo.put(e[0], videoId1);
            }
            if (log.isInfoEnabled()) {
                log.info("{} 推荐0.3C 获取用户兴趣分类视频时长：{}", uuid, (System.currentTimeMillis() - pt));
            }
            return System.currentTimeMillis() - pt;
        };

        CompletableFuture[] listFeture = catNum.entrySet().stream().map(e -> {
            Integer[] params = new Integer[]{e.getKey(), e.getValue().intValue()};
            return CompletableFuture.completedFuture(params).thenApplyAsync(f, executor);
        }).toArray(size -> new CompletableFuture[size]);
        return CompletableFuture.allOf(listFeture);
    }

    /**
     * 获取激励视频
     *
     * @param recommendDto
     * @return
     */
    public CompletableFuture<Integer> getIncetiveHotVideo(BaseDto baseDto, VideoRecommendDto recommendDto, List<IncentiveVideoHotspot> jlVideo) {
        String uuid = baseDto.getUuid();
        return CompletableFuture.supplyAsync(() -> {
            //lzhong 根据热度标签获取第4规则的1个视频(激励视频)
            long pt = System.currentTimeMillis();
            //int jlCatid = function.apply(recommendDto.getUserCats());
            List<IncentiveVideoHotspot> findList = incentiveVideoHotServiceV3New.findAndFilter(uuid, recommendDto.getExcludeCatList(), recommendDto.getSensitiveState(), recommendDto.getIncentiveVideoNum());
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.3C 获取的激励视频：{} 个", uuid, isEmpty(findList) ? 0 : findList.size());
            }
            if (isEmpty(findList)) {
                log.info("推荐0.3C 未找到 uuid={} 的激励视频", uuid);
                return 0;
            } else {
                jlVideo.addAll(findList);
            }
            if (log.isInfoEnabled()) {
                log.info("{} 推荐0.3C 获取的激励视频时长：{}", uuid, (System.currentTimeMillis() - pt));
            }
            return findList.size();
        }, executor);
    }

    /**
     * 将返回的热度视频和激励视频放入bloom过滤器
     *
     * @param uuid
     * @param userCatVideo
     * @param incentiveVideo
     */
    public void bloomVideo(String uuid, List<String> userCatVideo, List<String> incentiveVideo) {
        List<String> bloomVideos = new ArrayList<String>();
        if (!isEmpty(userCatVideo)) {
            bloomVideos.addAll(userCatVideo);
        }
        if (!isEmpty(incentiveVideo)) {
            bloomVideos.addAll(incentiveVideo);
        }
        if (isEmpty(bloomVideos)) {
            return;
        }
        executor.execute(() -> {
            bloomFilterService.putAll(uuid, bloomVideos);
        });
    }

    public Map<String, BigDecimal> filterSortedMap(Map<String, BigDecimal> sortedVideoMap, String uuid) {
        List<String> vidList = Lists.newArrayList(sortedVideoMap.keySet());
        List<String> filteredVidList = bloomFilterService.containMuil(vidList.size(), uuid, vidList);
        Map<String, BigDecimal> filteredMap = new LinkedHashMap<>();
        filteredVidList.stream().forEach(vid -> filteredMap.put(vid, sortedVideoMap.get(vid)));
        return filteredMap;
    }

    /**
     * 初始化推荐参数
     * @param baseDto
     * @param recommendDto
     */
    public void initRecommendParam(BaseDto baseDto, VideoRecommendDto recommendDto) {
        Map<Integer, Integer> recommendCatNum = null;
        if (baseDto.getPublicInfo().isNewApp()) {
            recommendCatNum = this.initNewUserRecommendCat(recommendDto.getUserChooseCatList());
        } else {
            List<CatWeightDto> userChooseCat = this.getUserChooseCat(baseDto);
            if(isEmpty(userChooseCat)){
                log.warn("{} 推荐0.3C 作为老用户，未查询到离线分类权重，使用初始数据", baseDto.getUuid());
                recommendCatNum = this.initNewUserRecommendCat(recommendDto.getUserChooseCatList());
            } else {
                List<Integer> allCat = videosCatService.getAllCatIds(XyConstants.FIRST_VIDEO_CODE).stream().map(Integer::valueOf).collect(Collectors.toList());
                allCat.removeAll(userChooseCat.stream().map(CatWeightDto::getCatId).collect(Collectors.toList()));
                recommendCatNum = this.initOldUserRecommendCat(userChooseCat, allCat);
            }
        }

        log.info("{} 推荐0.3C 最终进行推荐的分类>>{}", baseDto.getUuid(), recommendCatNum);
        recommendDto.setRecommendCat(recommendCatNum.keySet().stream().collect(Collectors.toList()));
        recommendDto.setRecommendCatNumMap(recommendCatNum);
    }

    private Map<Integer, Integer> initOldUserRecommendCat(List<CatWeightDto> userChooseCat, List<Integer> allCat) {
        Map<Integer, Integer> recommendCatNum = new HashMap<>();
        List<Integer> randomCatList = new ArrayList<>();
        userChooseCat.forEach(e -> {
            for (int i = 0; i < e.getWeight(); i++) {
                randomCatList.add(e.getCatId());
            }
        });

        Random random = new Random();
        int randomSize = randomCatList.size();
        if (!isEmpty(allCat)) {
            Integer randomCat = allCat.get(random.nextInt(allCat.size()));
            Double tenPercent = randomSize * 0.1;
            int randomCatNum = tenPercent > 0 ? tenPercent.intValue() : 1;
            while (randomCatNum > 0){
                randomCatList.add(randomCat);
                randomCatNum--;
            }
        }

        for (int i = 0; i < 7; i++) {
            Integer catid = recommendCatNum.get(random.nextInt(randomSize));
            Integer num = recommendCatNum.get(catid);
            if (num == null) {
                recommendCatNum.put(catid, 1);
            } else {
                recommendCatNum.put(catid, ++num);
            }
        }
        return recommendCatNum;
    }

    /**
     * 初始化新用户推荐视频的分类，以及每个分类视频的个数
     * @param userChooseCatList
     * @return
     */
    private Map<Integer, Integer> initNewUserRecommendCat(List<Integer> userChooseCatList) {
        List<CatWeightDto> userChooseCat = new ArrayList<CatWeightDto>();
        userChooseCatList.forEach(e -> {
            userChooseCat.add(new CatWeightDto(e, 1D));
        });

        Map<Integer, Integer> recommendCatNum = new HashMap<Integer, Integer>();
        Random random = new Random();
        int size = userChooseCat.size();
        switch (size) {
            case 2:
                // 新用户只选择了2个分类，随机1个分类返回4个视频，另一个返回3个视频
                recommendCatNum.put(userChooseCat.remove(random.nextInt(2)).getCatId(), 4);
                recommendCatNum.put(userChooseCat.get(0).getCatId(), 3);
                break;
            case 3:
                // 新用户只选择了3个分类，随机1个分类返回3个视频，其余返回2个视频
                recommendCatNum.put(userChooseCat.remove(random.nextInt(3)).getCatId(), 3);
                recommendCatNum.put(userChooseCat.get(0).getCatId(), 2);
                recommendCatNum.put(userChooseCat.get(1).getCatId(), 2);
                break;
            case 4:
                // 新用户只选择了4个分类，随机1个分类返回1个视频，其余返回2个视频
                recommendCatNum.put(userChooseCat.remove(random.nextInt(4)).getCatId(), 1);
                recommendCatNum.put(userChooseCat.get(0).getCatId(), 2);
                recommendCatNum.put(userChooseCat.get(1).getCatId(), 2);
                recommendCatNum.put(userChooseCat.get(2).getCatId(), 2);
                break;
            case 5:
                // 新用户只选择了5个分类，随机2个分类返回2个视频，其余返回1个视频
                recommendCatNum.put(userChooseCat.remove(random.nextInt(5)).getCatId(), 2);
                recommendCatNum.put(userChooseCat.remove(random.nextInt(4)).getCatId(), 2);
                recommendCatNum.put(userChooseCat.get(0).getCatId(), 1);
                recommendCatNum.put(userChooseCat.get(1).getCatId(), 1);
                recommendCatNum.put(userChooseCat.get(2).getCatId(), 1);
                break;
            case 6:
                // 新用户只选择了6个分类，随机1个分类返回2个视频，其余返回1个视频
                recommendCatNum.put(userChooseCat.remove(random.nextInt(6)).getCatId(), 2);
                recommendCatNum.put(userChooseCat.get(0).getCatId(), 1);
                recommendCatNum.put(userChooseCat.get(1).getCatId(), 1);
                recommendCatNum.put(userChooseCat.get(2).getCatId(), 1);
                recommendCatNum.put(userChooseCat.get(3).getCatId(), 1);
                recommendCatNum.put(userChooseCat.get(4).getCatId(), 1);
                break;
            default:
                // 新用户选择了含7个分类，随机7个分类，每个分类1个视频
                for (int i = 0; i < 7; i++) {
                    recommendCatNum.put(userChooseCat.remove(random.nextInt(userChooseCat.size())).getCatId(), 1);
                }
        }

        return recommendCatNum;
    }

    /**
     * 获取用户选择的分类
     *
     * @param baseDto
     * @return
     */
    private List<CatWeightDto> getUserChooseCat(BaseDto baseDto) {
        // 从xy库中获取用户选择的分类
        String date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String redisKey = String.format(RedisUserConstant.user_interest_weight_pool, date, baseDto.getUuid());
        Map<String, String> redisValue = redis11Service.hgetAll(redisKey);
        if (isEmpty(redisValue)) {
            redisValue = new HashMap<>();
            List<CatWeightDto> catList = new ArrayList<CatWeightDto>();
            // 从mongo库中获取离线计算出后，用户选择的分类及其权重
            List<UserRawTags> userRawTags = userRawTagsService.findChooseCatByUUid(baseDto.getUuid());
            if (isEmpty(userRawTags)) {
                return null;
            }

            Double weight = 1D;
            for (UserRawTags rawTag : userRawTags) {
                redisValue.put(rawTag.getTag_value().toString(), weight.toString());
                catList.add(new CatWeightDto(rawTag.getTag_value(), weight));
                weight++;
            }
            redis11Service.hmset(redisKey, redisValue, ExistConstants.one_day_seconds);
            return catList;
        } else {
            List<CatWeightDto> catList = new ArrayList<CatWeightDto>();
            for (String key : redisValue.keySet()) {
                Integer catId = Integer.parseInt(key);
                Double weight = Double.parseDouble(redisValue.get(key));
                catList.add(new CatWeightDto(catId, weight));
            }
            return catList;
        }
    }

}
