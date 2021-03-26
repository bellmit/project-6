package com.miguan.recommend.service.recommend.impl.v3;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.miguan.recommend.bo.BaseDto;
import com.miguan.recommend.bo.PublicInfo;
import com.miguan.recommend.bo.VideoQueryDto;
import com.miguan.recommend.bo.VideoRecommendDto;
import com.miguan.recommend.common.constants.MongoConstants;
import com.miguan.recommend.common.constants.RedisRecommendConstants;
import com.miguan.recommend.common.constants.SymbolConstants;
import com.miguan.recommend.common.util.Global;
import com.miguan.recommend.entity.mongo.IncentiveVideoHotspot;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.service.BloomFilterService;
import com.miguan.recommend.service.recommend.*;
import com.miguan.recommend.service.xy.VideosCatService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service(value = "videoRecommendServiceV3B")
public class VideoRecommendServiceV3BImpl extends AbstractRecommendService implements VideoRecommendService<VideoRecommendDto> {

//    private final static int isSensitive = 0;
//    private final static List<String> excludeSource = null;
    private final static int isSensitive = 1;

    @Resource(name = "recDB9Pool")
    private JedisPool recDB9Pool;
    @Resource(name = "logMongoTemplate")
    private MongoTemplate logMongoTemplate;
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
                videoInfoForRecList = this.getAsync(baseDto, recommendDto);
                if (!isEmpty(videoInfoForRecList)) {
                    String cacheValue = JSON.toJSONString(videoInfoForRecList);
                    con.setex(key, cacheExpireTime, cacheValue);
                }
            } else {
                videoInfoForRecList = (List<Object>) JSON.parse(cacheList);
                needFilter = true;
            }
        }

        Map<String, BigDecimal> videoPlayRateMap = (Map<String, BigDecimal>) videoInfoForRecList.get(0);
        Map<String, Integer> videoCatMap = (Map<String, Integer>) videoInfoForRecList.get(1);
        if (needFilter) {
            List<IncentiveVideoHotspot> jlVideo = new ArrayList<>();
            videoPlayRateMap = filterSortedMap(videoPlayRateMap, publicInfo.getUuid(), bloomFilterService);
            CompletableFuture<Integer> incetiveHotVideo = this.getIncetiveHotVideo(baseDto, recommendDto, jlVideo, executor);
            incetiveHotVideo.join();
            recommendDto.getJlvideo().addAll(jlVideo.stream().map(IncentiveVideoHotspot::getVideo_id).collect(Collectors.toList()));
            recommendDto.setJlvideoCat(jlVideo.stream().collect(Collectors.toMap(IncentiveVideoHotspot::getVideo_id, IncentiveVideoHotspot::getCatid)));
        }

        if (videoInfoForRecList == null || videoInfoForRecList.isEmpty()) {
            return;
        }

        // 分类对应的视频列表Map
        Map<Integer, List<String>> catVideoListMap = predictService.videoTopK(videoPlayRateMap, videoCatMap);

        // 最终热度视频返回的数量
        int hotVideoReturnCount = recommendDto.getVideoNum();
        if (isEmpty(recommendDto.getJlvideo())) {
            ++hotVideoReturnCount;
        }
        // 每个分类的视频限制个数
        int oneCatLimit = 5;
        // 所有分类视频限制个数
        int limitMulti = 100;

        //用户3刷前，8个视频：
        //5个是视频为主标签分类视频
        //2个视频为较相关性分类视频
        //1个视频为其他随机分类视频
        List<String> recVideos = null;
        List<String> specificCatVideos = new ArrayList<String>();
        if (baseDto.getFlushNum() <= 3) {
            this.addTheFirstVideoOfTopCat(baseDto, recommendDto.getDefaultCatList(), catVideoListMap, specificCatVideos, 1, 4);
            log.info("{} 推荐0.3B 前3刷渠道主标签分类:{}, 视频列表:{}", baseDto.getUuid(), recommendDto.getDefaultCat(), JSONObject.toJSONString(specificCatVideos));

            this.addTheFirstVideoOfTopCat(baseDto, recommendDto.getRelevantCats(), catVideoListMap, specificCatVideos, 2, 1);
            log.info("{} 推荐0.3B 前3刷相关性分类:{}, 视频列表:{}", baseDto.getUuid(), recommendDto.getRelevantCats(), JSONObject.toJSONString(specificCatVideos));

            this.addTheFirstVideoOfTopCat(baseDto, recommendDto.getRandomCats(), catVideoListMap, specificCatVideos, 1, 1);
            log.info("{} 推荐0.3B 前3刷随机分类:{}, 视频列表:{}", baseDto.getUuid(), recommendDto.getRandomCats(), JSONObject.toJSONString(specificCatVideos));

            if (specificCatVideos.size() < hotVideoReturnCount) {
                recVideos = new ArrayList<String>(hotVideoReturnCount);
                // 按照排序取出剩余需要返回的视频
                List<String> videoSortList = predictService.videoTopK(videoPlayRateMap, videoCatMap, hotVideoReturnCount, oneCatLimit, limitMulti);
                videoSortList.removeAll(specificCatVideos);

                recVideos.addAll(specificCatVideos);
                recVideos.addAll(videoSortList.subList(0, Math.min(videoSortList.size(), hotVideoReturnCount - specificCatVideos.size())));

                log.info("{} 推荐0.3B 前3刷视频不满足需要返回的个数:{}, 补足后的视频列表:{}", baseDto.getUuid(), recommendDto.getDefaultCat(), JSONObject.toJSONString(recVideos));
            } else {
                recVideos = specificCatVideos;
            }

        } else {
            // 用户3刷后：
            // 有点击：每个分类的视频限制个数：2→5
            // 无点击：每个分类的视频限制个数：2
            if (isEmpty(recommendDto.getUserCats())) {
                oneCatLimit = 2;
                log.info("{} 推荐0.3B 3刷后用户无点击行为，每个分类视频个数最多2个", baseDto.getUuid());
            } else {
                log.info("{} 推荐0.3B 3刷后用户有点击行为，每个分类视频个数最多5个", baseDto.getUuid());
            }
            recVideos = predictService.videoTopK(videoPlayRateMap, videoCatMap, hotVideoReturnCount, oneCatLimit, limitMulti);
            log.info("{} 推荐0.3B 3刷后视频列表:{}", baseDto.getUuid(), JSONObject.toJSONString(recVideos));
        }

        recommendDto.setRecommendVideo(recVideos);
        recommendDto.setRecommendVideoCat(videoCatMap);
        log.warn("推荐0.3B此次给uuid({})推荐了 {} 个视频，推荐视频ID集：{}, 激励视频 {} 个, 激励视频ID：{}",
                publicInfo.getUuid(), recVideos.size(), JSON.toJSONString(recVideos), recommendDto.getJlvideo().size(), JSON.toJSONString(recommendDto.getJlvideo()));

        // 将查询的视频放入Bloom过滤器
        bloomVideo(publicInfo.getUuid(), recVideos, recommendDto.getJlvideo(), bloomFilterService, executor);
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
            jlVideoFuture = this.getIncetiveHotVideo(baseDto, recommendDto, jlVideo, executor);
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
                userCatVideoAndOfflineVideoMap.put(e.getVideo_id(), e.getCatid());
            });
        }


        // 去重后得所有视频ID
        long pt2 = System.currentTimeMillis();
        log.warn("{} 推荐0.3B召回时长：{}，召回数量：{}，其中热门召回：{}，协同过滤召回：{}",
                baseDto.getUuid(), (pt2 - pt), allVideos.size(), catSum, 0);

        // 获取排序值并排序
        Map<String, BigDecimal> sortedVideoMap = predictService.getVideoListPlayRate(baseDto, allVideos);
        long pt3 = System.currentTimeMillis();

        recommendDto.getJlvideo().addAll(jlVideo.stream().map(IncentiveVideoHotspot::getVideo_id).collect(Collectors.toList()));
        recommendDto.setJlvideoCat(jlVideo.stream().collect(Collectors.toMap(IncentiveVideoHotspot::getVideo_id, IncentiveVideoHotspot::getCatid)));
        log.info("推荐0.3B时长，排序总时长：" + (pt3 - pt2));
        return Arrays.asList(sortedVideoMap, userCatVideoAndOfflineVideoMap);
    }

    /**
     * 通过用户的兴趣分类获取视频ID
     *
     * @param recommendDto
     * @return
     */
    public CompletableFuture<Void> getHotVideo(BaseDto baseDto, VideoRecommendDto recommendDto, Map<Integer, List<VideoHotspotVo>> userCatVideo) {
        String uuid = baseDto.getUuid();
        // 计算用户的兴趣分类，每个分类的个数
        Map<Integer, Long> catNum = new HashMap<Integer, Long>();
        List<Integer> recommendCatList = recommendDto.getRecommendCat();

        long catVideoNum = 100L;
        long lowestNum = 20L;
        if (!isEmpty(recommendCatList)) {
            for (Integer catid : recommendCatList) {
                catNum.put(catid, catVideoNum);
                catVideoNum -= lowestNum;
                if (catVideoNum < lowestNum) {
                    catVideoNum = lowestNum;
                }
            }
        }

        Function<Integer[], Number> f = e -> {
            long pt = System.currentTimeMillis();
            VideoQueryDto<VideoHotspotVo> queryDto = new VideoQueryDto<VideoHotspotVo>(baseDto, e[0], isSensitive, e[1]);
            queryDto.setExcludedSource(excludeSource);
            List<VideoHotspotVo> videoId1 = videoHotServiceV3.findAndFilter(queryDto, null);
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.3B 获取用户兴趣分类[{}]视频：{} 个", uuid, e[0], isEmpty(videoId1) ? 0 : videoId1.size());
            }
            if (!isEmpty(videoId1)) {
                userCatVideo.put(e[0], videoId1);
            }
            if (log.isInfoEnabled()) {
                log.info("{} 推荐0.3B 获取用户兴趣分类视频时长：{}", uuid, (System.currentTimeMillis() - pt));
            }
            return System.currentTimeMillis() - pt;
        };
        CompletableFuture[] listFeture = catNum.entrySet().stream().map(e -> {
            Integer[] params = new Integer[]{e.getKey(), e.getValue().intValue()};
            return CompletableFuture.completedFuture(params).thenApplyAsync(f, executor);
        }).toArray(size -> new CompletableFuture[size]);
        return CompletableFuture.allOf(listFeture);
    }

    public CompletableFuture<Integer> getIncetiveHotVideo(BaseDto baseDto, VideoRecommendDto recommendDto, List<IncentiveVideoHotspot> jlVideo, ExecutorService executor) {
        String uuid = baseDto.getUuid();
        return CompletableFuture.supplyAsync(() -> {
            //lzhong 根据热度标签获取第4规则的1个视频(激励视频)
            long pt = System.currentTimeMillis();
            List<IncentiveVideoHotspot> findList = incentiveVideoHotServiceV3New.findAndFilter(uuid, recommendDto.getDefaultCatList().get(0), null, isSensitive, excludeSource, recommendDto.getIncentiveVideoNum());
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.3B 获取的激励视频：{} 个", uuid, isEmpty(findList) ? 0 : findList.size());
            }
            if (isEmpty(findList)) {
                log.info("{} 推荐0.3B 未找到激励视频", uuid);
                return 0;
            } else {
                jlVideo.addAll(findList);
            }
            if (log.isInfoEnabled()) {
                log.info("{} 推荐0.3B 获取的激励视频时长：{}", uuid, (System.currentTimeMillis() - pt));
            }
            return findList.size();
        }, executor);
    }

    public void initRecommendParam(BaseDto baseDto, VideoRecommendDto recommendDto) {
        List<Integer> recommendCatList = new ArrayList<Integer>();
        List<Integer> defaultCatList = Stream.of(recommendDto.getDefaultCat().split(SymbolConstants.comma)).map(Integer::valueOf).limit(1L).collect(Collectors.toList());
        log.info("{} 推荐0.3B 渠道主标签分类:{}", baseDto.getUuid(), recommendDto.getDefaultCat());
        List<Integer> userCatList = null;
        List<Integer> similarCatList = null;

        String wilteUsers = Global.getValue("first_flush_of_unity_channel_recommend");
        boolean isWilteUser = StringUtils.isEmpty(wilteUsers) ? false : wilteUsers.contains(baseDto.getUuid());
        if (isWilteUser || baseDto.getFlushNum() <= 3) {
            log.info("{} 推荐0.3B 属于白名单或前3刷用户", baseDto.getUuid());
            baseDto.setFlushNum(3L);
            recommendCatList.add(defaultCatList.get(0));

            List<Integer> relevantCatList = null;
            // 获取主标签的相关性分类配置
            String relevantConfig = Global.getValue("uniformity_recommend_relevant_cat");
            if(!StringUtils.isEmpty(recommendCatList)){
                JSONArray jsonArray = JSONObject.parseObject(relevantConfig).getJSONArray(defaultCatList.get(0).toString());
                if(jsonArray == null || jsonArray.isEmpty()){
                    log.info("{} 推荐0.3B 通过渠道主标签未获取到的相关性分类", baseDto.getUuid());
                } else {
                    relevantCatList = jsonArray.toJavaList(Integer.class);
                    log.info("{} 推荐0.3B 通过渠道主标签获取到的相关性分类：{}", baseDto.getUuid(), JSONObject.toJSONString(relevantCatList));
                    recommendDto.setRelevantCats(relevantCatList);
                    recommendCatList.addAll(relevantCatList);
                }
            } else {
                log.info("{} 推荐0.3B 未开启渠道主标签相关性分类配置", baseDto.getUuid());
            }

            List<Integer> similarExcludeCat = new ArrayList<Integer>();
            similarExcludeCat.addAll(recommendCatList);
            similarExcludeCat.addAll(recommendDto.getExcludeCatList());
            similarCatList = super.getSimilarCatByCollectionName(recommendCatList.get(0), similarExcludeCat,
                    false, logMongoTemplate, MongoConstants.cat_hotspot, videosCatService);
            if(!isEmpty(similarCatList)){
                int i = new Random().nextInt(similarCatList.size());
                List<Integer> randomCatList = Lists.newArrayList(similarCatList.remove(i == 0 ? i : i - 1));
                recommendDto.setRandomCats(randomCatList);
                recommendCatList.addAll(randomCatList);
                log.info("{} 推荐0.3B 获取到随机分类:{}", baseDto.getUuid(), JSONObject.toJSONString(randomCatList));
            } else {
                log.info("{} 推荐0.3B 未获取到随机分类", baseDto.getUuid());
            }
        } else {

            log.info("{} 推荐0.3B 属于3刷后用户", baseDto.getUuid());
            try (Jedis con = recDB9Pool.getResource()) {
                String tmp = con.hget("bg_sUp", baseDto.getUuid());
                if (!StringUtils.isEmpty(tmp)) {
                    String[] str = tmp.split(",");
                    if (str.length > 3) {
                        str = ArrayUtils.subarray(str, 0, 3);
                    }
                    userCatList = Stream.of(str).map(Integer::valueOf).limit(3).collect(Collectors.toList());
                    if (!isEmpty(recommendDto.getExcludeCatList())) {
                        userCatList.removeAll(recommendDto.getExcludeCatList());
                    }
                }
            }

            if (isEmpty(userCatList) || userCatList.size() < 2) {
                recommendCatList.add(defaultCatList.get(0));
                similarCatList = super.getSimilarCatByCollectionName(recommendCatList.get(0), recommendDto.getExcludeCatList(),
                        false, logMongoTemplate, MongoConstants.cat_hotspot, videosCatService);
                if (!isEmpty(similarCatList)) {
                    similarCatList.removeAll(recommendCatList);
                    recommendCatList.addAll(similarCatList.subList(0, Math.min(4, similarCatList.size())));
                }
                log.info("{} 推荐0.3B 未获取到用户点击过的分类，根据渠道默认分类，以及默认第一分类的4个相似分类进行召回:{}", baseDto.getUuid(), JSONObject.toJSONString(recommendCatList));
            } else {
                log.info("{} 推荐0.3B 获取到用户点击过的分类:{}", baseDto.getUuid(), JSONObject.toJSONString(userCatList));
                recommendDto.setUserCats(userCatList);
                recommendCatList.addAll(userCatList);
            }
        }
        log.info("{} 推荐0.3B 的召回分类:{}", baseDto.getUuid(), JSONObject.toJSONString(recommendCatList));
        recommendDto.setRecommendCat(recommendCatList);
    }


    private void addTheFirstVideoOfTopCat(BaseDto baseDto, List<Integer> catList, Map<Integer, List<String>> catVideoListMap, List<String> recVideoList, int topCatNum, int catVideoNum) {
        if (isEmpty(catList)) {
            return;
        }
        // 循环分类次数
        int cycleSize = Math.min(topCatNum, catList.size());
        for (int i = 0; i < cycleSize; i++) {
            Integer catid = catList.get(i);
            List<String> catVideos = catVideoListMap.get(catid);
            if (isEmpty(catVideos)) {
                continue;
            }
            int addNum = 0;
            int videoIndex = 0;
            int endIndex = catVideos.size();
            String videoId = null;
            while (videoIndex < endIndex) {
                videoId = catVideos.get(videoIndex);
                if (!recVideoList.contains(videoId)) {
                    log.debug("{} 推荐0.3B 根据最感兴趣分类>>{}，获取到视频>>{}", baseDto.getUuid(), catid, videoId);
                    recVideoList.add(videoId);
                    addNum += 1;
                    if (addNum >= catVideoNum) {
                        break;
                    }
                }
                videoIndex++;
            }
        }
    }

    public List<String> reSortByRate(Map<String, BigDecimal> videoPlayRateMap, List<String> videoList) {
        List<String> sortedVideoList = Lists.newArrayList();
        for (String vid : videoPlayRateMap.keySet()) {
            if (videoList.contains(vid)) {
                sortedVideoList.add(vid);
            }
            if (sortedVideoList.size() >= videoList.size()) {
                break;
            }
        }
        return sortedVideoList;
    }


}
