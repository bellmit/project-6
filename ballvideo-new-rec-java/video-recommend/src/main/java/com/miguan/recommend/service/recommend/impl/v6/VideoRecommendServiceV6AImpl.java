package com.miguan.recommend.service.recommend.impl.v6;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.miguan.recommend.bo.*;
import com.miguan.recommend.common.constants.MongoConstants;
import com.miguan.recommend.common.constants.RedisRecommendConstants;
import com.miguan.recommend.common.constants.RedisUserConstant;
import com.miguan.recommend.entity.mongo.IncentiveVideoHotspot;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.service.BloomFilterService;
import com.miguan.recommend.service.RedisService;
import com.miguan.recommend.service.mongo.UserRawTagsService;
import com.miguan.recommend.service.recommend.*;
import com.miguan.recommend.service.xy.VideosCatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service(value = "videoRecommendServiceV6A")
public class VideoRecommendServiceV6AImpl extends AbstractRecommendService implements VideoRecommendService<VideoRecommendDto> {

    @Resource(name = "redisDB0Service")
    private RedisService redisDB0Service;
    @Resource(name = "redisDB11Service")
    private RedisService redisDB11Service;
    @Resource(name = "recDB9Pool")
    private JedisPool recDB9Pool;
    @Resource(name = "logMongoTemplate")
    private MongoTemplate mongoTemplate;
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
    private UserRawTagsService userRawTagsService;

    ExecutorService executor = new ThreadPoolExecutor(200, 1000, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5000));

    @Override
    public void recommend(BaseDto baseDto, VideoRecommendDto recommendDto) {
        PublicInfo publicInfo = baseDto.getPublicInfo();
        this.initRecommendParam(baseDto, recommendDto, recDB9Pool, mongoTemplate, videosCatService);
        // 从缓存中获取预估数据
        String key = String.format(RedisRecommendConstants.key_user_rec_video_list, publicInfo.getUuid());
        String cacheList = redisDB0Service.get(key);

        boolean needFilter = false;
        List<Object> videoInfoForRecList = null;
        if (cacheList == null || cacheList.isEmpty()) {
            videoInfoForRecList = getAsync(baseDto, recommendDto);
            String cacheValue = JSON.toJSONString(videoInfoForRecList);
            redisDB0Service.set(key, cacheValue, publicInfo.isNew() ? 30 : 60);
        } else {
            videoInfoForRecList = (List<Object>) JSON.parse(cacheList);
            needFilter = true;
        }

        Map<String, BigDecimal> videoPlayRateMap = (Map<String, BigDecimal>) videoInfoForRecList.get(0);
        Map<String, Integer> videoCatMap = (Map<String, Integer>) videoInfoForRecList.get(1);
        if (needFilter) {
            List<IncentiveVideoHotspot> jlVideo = new ArrayList<>();
            videoPlayRateMap = filterSortedMap(videoPlayRateMap, publicInfo.getUuid(), bloomFilterService);
            CompletableFuture<Integer> incetiveHotVideo = getIncetiveHotVideo(baseDto, recommendDto, jlVideo, incentiveVideoHotServiceV3New, executor);
            incetiveHotVideo.join();
            recommendDto.getJlvideo().addAll(jlVideo.stream().map(IncentiveVideoHotspot::getVideo_id).collect(Collectors.toList()));
            recommendDto.setJlvideoCat(jlVideo.stream().collect(Collectors.toMap(IncentiveVideoHotspot::getVideo_id, IncentiveVideoHotspot::getCatid)));
        }

        if (videoInfoForRecList == null || videoInfoForRecList.isEmpty()) {
            return;
        }

        Map<Integer, List<String>> catVideoListMap = predictService.videoTopK(videoPlayRateMap, videoCatMap);
        List<String> specificCatVideos = new ArrayList<String>();
        if (baseDto.getPublicInfo().isNewApp()) {
            // 新用户头两刷，默认第一分类取1个，实时的第一个分类取1个, 默认分类头两个分别取1个
            // 新用户超过两刷，实时的第一个分类取1个, 默认分类头两个分别取1个
//            if (baseDto.getTodayFlushNum() < 3) {
//                this.addTheFirstVideoOfTopCat(baseDto, recommendDto.getDefaultCatList(), catVideoListMap, specificCatVideos, 1);
//            }
//            this.addTheFirstVideoOfTopCat(baseDto, recommendDto.getUserCats(), catVideoListMap, specificCatVideos, 1);
//            this.addTheFirstVideoOfTopCat(baseDto, recommendDto.getDefaultCatList(), catVideoListMap, specificCatVideos, 2);
        } else {
            // 老用户根据离线兴趣分类，头两个分类各取1个视频
            // 老用户根据实时兴趣分类，头一个分类各取1个视频
            this.addTheFirstVideoOfTopCat(baseDto, recommendDto.getUserOfflineCats(), catVideoListMap, specificCatVideos, 2);
            this.addTheFirstVideoOfTopCat(baseDto, recommendDto.getUserCats(), catVideoListMap, specificCatVideos, 1);
        }
        log.debug("{} 推荐0.6A 确定分类的视频ID>>{}", baseDto.getUuid(), JSONObject.toJSONString(specificCatVideos));
        // 热度视频返回的数量
        int hotVideoReturnCount = 7;
        if (isEmpty(recommendDto.getJlvideo())) {
            hotVideoReturnCount = 8;
        }

        int oneCatLimit = 2;
        int limitMulti = 10;
        if (baseDto.getPublicInfo().isNew()) {
            limitMulti = 4;
        }

        // 按照排序取出剩余需要返回的视频
        List<String> videoSortList = predictService.videoTopK(videoPlayRateMap, videoCatMap, hotVideoReturnCount, oneCatLimit, limitMulti);
        videoSortList.removeAll(specificCatVideos);
        log.debug("{} 推荐0.6A 根据排序获取的视频ID>>{}", baseDto.getUuid(), JSONObject.toJSONString(videoSortList));

        // 去重，重新排序
        List<String> recVideoList = new ArrayList<String>(hotVideoReturnCount);
        recVideoList.addAll(videoSortList.subList(0, Math.min(videoSortList.size(), hotVideoReturnCount - specificCatVideos.size())));
        recVideoList.addAll(specificCatVideos);
        // 按预估值排序
        recVideoList = reSortByRate(videoPlayRateMap, recVideoList);

        recommendDto.setRecommendVideo(recVideoList);
        recommendDto.setRecommendVideoCat(videoCatMap);
        log.warn("推荐0.6A 此次给uuid({})推荐了 {} 个视频，推荐视频ID集：{}, 激励视频 {} 个, 激励视频ID：{}",
                publicInfo.getUuid(), recVideoList.size(), JSON.toJSONString(recVideoList), recommendDto.getJlvideo().size(), JSON.toJSONString(recommendDto.getJlvideo()));

        // 将查询的视频放入Bloom过滤器
        bloomVideo(publicInfo.getUuid(), recVideoList, recommendDto.getJlvideo(), bloomFilterService, executor);
        featureService.saveFeatureToRedis(baseDto, recommendDto);
    }

    public List<Object> getAsync(BaseDto baseDto, VideoRecommendDto recommendDto) {
        long pt1 = System.currentTimeMillis();
        // 所有并行的函数集合
        List<CompletableFuture> futureList = new ArrayList<CompletableFuture>();

        // 激励视频
        List<IncentiveVideoHotspot> jlVideo = Lists.newArrayList();
        if (recommendDto.getIncentiveVideoNum() > 0) {
            CompletableFuture<Integer> jlVideoFuture = super.getIncetiveHotVideo(baseDto, recommendDto, jlVideo, incentiveVideoHotServiceV3New, executor);
            futureList.add(jlVideoFuture);
        }

        // 最终查询到的推荐视频，按分类，放入map
        Map<Integer, List<VideoHotspotVo>> userCatVideoMap = new LinkedHashMap<>();
        // 根据用户分类兴趣池查询200个
        CompletableFuture<Void> userCatVideoFuture = this.findHotVideo(baseDto, recommendDto, 350, userCatVideoMap);
        futureList.add(userCatVideoFuture);

        int futureSize = futureList.size();
        CompletableFuture[] futures = new CompletableFuture[futureSize];
        for (int i = 0; i < futureSize; i++) {
            futures[i] = futureList.get(i);
        }

        CompletableFuture allFuture = CompletableFuture.allOf(futures);
        allFuture.join();

        // 所有用户兴趣分类视频和离线视频
        List<VideoHotspotVo> allVideos = new ArrayList<VideoHotspotVo>();
        Map<String, Integer> allVideosCatMap = new LinkedHashMap<>();
        int catSum = 0;
        for (Integer cat : userCatVideoMap.keySet()) {
            List<VideoHotspotVo> catVideos = userCatVideoMap.get(cat);
            if (isEmpty(catVideos)) {
                continue;
            }
            catVideos = catVideos.stream().filter(e -> !recommendDto.getExcludeCatList().contains(e.getCatid())).collect(Collectors.toList());
            catSum += catVideos.size();
            allVideos.removeAll(catVideos);
            allVideos.addAll(catVideos);
            catVideos.stream().forEach(e -> {
                allVideosCatMap.put(e.getVideo_id(), e.getCatid());
            });
        }

        long pt2 = System.currentTimeMillis();
        log.warn("{} 推荐0.6A 召回时长：{}，召回数量：{}，其中热门召回：{}", baseDto.getUuid(), (pt2 - pt1), allVideos.size(), catSum);

        // 获取排序值并排序
        Map<String, BigDecimal> sortedVideoMap = predictService.getVideoListPlayRate(baseDto, allVideos);
        long pt3 = System.currentTimeMillis();

        recommendDto.getJlvideo().addAll(jlVideo.stream().map(IncentiveVideoHotspot::getVideo_id).collect(Collectors.toList()));
        recommendDto.setJlvideoCat(jlVideo.stream().collect(Collectors.toMap(IncentiveVideoHotspot::getVideo_id, IncentiveVideoHotspot::getCatid)));
        log.info("{} 推荐0.6A 排序总时长：{}", baseDto.getUuid(), (pt3 - pt2));
        return Arrays.asList(sortedVideoMap, allVideosCatMap);
    }

    public List<String> reSortByRate(Map<String, BigDecimal> videoPlayRateMap,List<String> videoList){
        List<String> sortedVideoList = Lists.newArrayList();
        for (String vid : videoPlayRateMap.keySet()) {
            if(videoList.contains(vid)){
                sortedVideoList.add(vid);
            }
            if(sortedVideoList.size() >= videoList.size()){
                break;
            }
        }
        return sortedVideoList;
    }

    public CompletableFuture<Void> findHotVideo(BaseDto baseDto, VideoRecommendDto recommendDto, int getNum, Map<Integer, List<VideoHotspotVo>> userCatVideo) {
        List<Integer> catIds = recommendDto.getRecommendCat();
        double catNumRate = catIds.size() > 5 ? 0.3D : 0.5D;
        Map<Integer, Long> catNum = new HashMap<Integer, Long>();
        for (Integer catId : catIds) {
            Double cunrrentCatNum = getNum * catNumRate;
            catNum.put(catId, cunrrentCatNum.longValue());
            getNum -= cunrrentCatNum.longValue();
        }

        Function<Integer[], Number> f = e -> {
            long pt = System.currentTimeMillis();
            VideoQueryDto<VideoHotspotVo> queryDto = new VideoQueryDto<VideoHotspotVo>(baseDto, e[0], recommendDto.getSensitiveState(), e[1]);
            if(baseDto.isVideo98Group()){
                queryDto.setExcludedSource(excludeSource);
            }
            List<VideoHotspotVo> videoId1 = videoHotServiceV3.findAndFilter(queryDto, null);
            if (isEmpty(videoId1)) {
                log.debug("{} 推荐0.6A 获取用户兴趣分类[{}]视频：{} 个", baseDto.getUuid(), e[0], 0);
            } else {
                log.debug("{} 推荐0.6A 获取用户兴趣分类[{}]视频：{} 个", baseDto.getUuid(), e[0], videoId1.size());
                userCatVideo.put(e[0], videoId1);
            }
            log.debug("{} 推荐0.6A 获取用户兴趣分类视频时长：{}", baseDto.getUuid(), (System.currentTimeMillis() - pt));
            return System.currentTimeMillis() - pt;
        };
        CompletableFuture[] listFeture = catNum.entrySet().stream().map(e -> {
            Integer[] params = new Integer[]{e.getKey(), e.getValue().intValue()};
            return CompletableFuture.completedFuture(params).thenApplyAsync(f, executor);
        }).toArray(size -> new CompletableFuture[size]);
        return CompletableFuture.allOf(listFeture);

    }

    @Override
    public void initRecommendParam(BaseDto baseDto, VideoRecommendDto recommendDto, JedisPool recDB9Pool, MongoTemplate logMongTemplate, VideosCatService videosCatService) {
        List<Integer> defaultCat = recommendDto.getDefaultCatList();
        log.info("{} 推荐0.6A 默认分类>>{}", baseDto.getUuid(), JSONObject.toJSONString(defaultCat));
        // 召回分类
        List<Integer> recommendCat = new ArrayList<Integer>();
        if (baseDto.getPublicInfo().isNewApp()) {
            // 新用户 3个实时分类 + 第一实时分类的相似分类3个
            List<Integer> userCats = super.getUserCats(recDB9Pool, baseDto.getUuid(), recommendDto.getDefaultCatList());
            recommendDto.setUserCats(userCats);
            recommendCat.addAll(userCats);

            List<Integer> similarCat = super.getSimilarCatByCollectionName(userCats.get(0), recommendDto.getExcludeCatList(), baseDto.isABTest(), logMongTemplate, MongoConstants.cat_hotspot_old, videosCatService);
            if(!isEmpty(similarCat)){
                similarCat.removeAll(userCats);
                recommendDto.setSimilarCats(similarCat);
                recommendCat.addAll(similarCat.subList(0, Math.min(6 - userCats.size(), similarCat.size())));
            }
        } else {
            List<Integer> userCat = this.getUserCat(baseDto, defaultCat);
            if(!isEmpty(userCat) && !isEmpty(recommendDto.getExcludeCatList())){
                userCat.removeAll(recommendDto.getExcludeCatList());
            }
            recommendDto.setUserCats(userCat);
            // 老用户2个离线分类 + 2个实时分类 + 2个第一离线的相似分类
            List<Integer> offlineCat = userRawTagsService.findOfflineCatsByUUid(baseDto.getUuid());

            int subLimit = 2;
            if (isEmpty(offlineCat)) {
                recommendCat.addAll(defaultCat);
            } else {
                if (!isEmpty(recommendDto.getExcludeCatList())) {
                    offlineCat.removeAll(recommendDto.getExcludeCatList());
                }
                recommendDto.setUserOfflineCats(offlineCat);
                recommendCat.addAll(offlineCat.subList(0, Math.min(subLimit, offlineCat.size())));
            }
            recommendCat.addAll(recommendDto.getUserCats().subList(0, Math.min(subLimit, recommendDto.getUserCats().size())));

            Integer parentCatid = isEmpty(recommendCat) ? defaultCat.get(0) : recommendCat.get(0);
            List<Integer> similarCat = super.getSimilarCat(parentCatid, recommendDto.getExcludeCatList(), baseDto.isABTest(), logMongTemplate, videosCatService);
            if (!isEmpty(similarCat)) {
                if (!isEmpty(offlineCat)) {
                    similarCat.removeAll(offlineCat);
                }
                similarCat.removeAll(recommendCat);
                recommendDto.setSimilarCats(similarCat);
                recommendCat.addAll(similarCat);
            }
            log.info("{} 推荐0.6A 作为老用户的相似分类>>{}", baseDto.getUuid(), JSONObject.toJSONString(similarCat));
        }

        List<Integer> finalCatList = recommendCat.stream().distinct().limit(6).collect(Collectors.toList());
        recommendCat.clear();
        log.info("{} 推荐0.6A 最终召回分类>>{}", baseDto.getUuid(), JSONObject.toJSONString(finalCatList));
        recommendDto.setRecommendCat(finalCatList);
    }

    private List<Integer> getUserCat(BaseDto baseDto, List<Integer> defaultCat) {
//        if (baseDto.getPublicInfo().isNewApp() && baseDto.getTodayFlushNum() >= 3) {
//            return this.getUserCatBySorce(baseDto, defaultCat);
//        }
        return super.getUserCats(recDB9Pool, baseDto.getUuid(), defaultCat);
    }

    private List<Integer> getUserCatBySorce(BaseDto baseDto, List<Integer> defaultCat) {
        int dayOfMonth = LocalDate.now().getDayOfMonth();
        Map<String, String> map = redisDB11Service.hgetAll(String.format(RedisUserConstant.user_cat_score, dayOfMonth, baseDto.getUuid()));
        if (isEmpty(map)) {
            log.debug("{} 推荐0.6A 用户兴趣分类评分为空，直接返回默认分类", baseDto.getUuid());
            return Lists.newArrayList(defaultCat);
        }

        log.debug("{} 推荐0.6A 用户兴趣分类评分:{}", baseDto.getUuid(), map);
        Set<String> keys = map.keySet();
        List<UserCatScoreDto> dtoList = new ArrayList<UserCatScoreDto>(keys.size());
        for (String key : keys) {
            String value = map.getOrDefault(key, "0");
            dtoList.add(new UserCatScoreDto(Integer.parseInt(key), Integer.parseInt(value)));
        }

        List<Integer> catScoreList = dtoList.stream().sorted((d1, d2) -> {
            return d2.getScore().compareTo(d1.getScore());
        }).map(UserCatScoreDto::getCatId).collect(Collectors.toList());
        log.info("{} 推荐0.6A 排序后的用户兴趣分类评分:{}", baseDto.getUuid(), map);
        return catScoreList;
    }

    private void addTheFirstVideoOfTopCat(BaseDto baseDto, List<Integer> catList, Map<Integer, List<String>> catVideoListMap, List<String> recVideoList, int topCatNum) {
        if (isEmpty(catList)) {
            return;
        }

        int cycleSize = Math.min(topCatNum, catList.size());
        for (int i = 0; i < cycleSize; i++) {
            Integer catid = catList.get(i);
            List<String> catVideos = catVideoListMap.get(catid);
            if (isEmpty(catVideos)) {
                continue;
            }
            int videoIndex = 0;
            int endIndex = catVideos.size();
            String videoId = null;
            do {
                if (videoIndex >= endIndex) {
                    return;
                }
                videoId = catVideos.get(videoIndex);
                videoIndex++;
            } while (recVideoList.contains(videoId));

            log.info("{} 推荐0.6A 根据最感兴趣分类>>{}，获取到视频>>{}", baseDto.getUuid(), catid, videoId);
            recVideoList.add(videoId);
        }
    }
}
