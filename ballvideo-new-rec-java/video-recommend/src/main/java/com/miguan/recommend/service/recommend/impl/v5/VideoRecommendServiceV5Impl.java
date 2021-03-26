package com.miguan.recommend.service.recommend.impl.v5;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.miguan.recommend.bo.BaseDto;
import com.miguan.recommend.bo.PublicInfo;
import com.miguan.recommend.bo.VideoRecommendDto;
import com.miguan.recommend.common.constants.RedisRecommendConstants;
import com.miguan.recommend.entity.mongo.IncentiveVideoHotspot;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.service.BloomFilterService;
import com.miguan.recommend.service.RedisService;
import com.miguan.recommend.service.mongo.ScenairoVideoService;
import com.miguan.recommend.service.mongo.VideoHotspotService;
import com.miguan.recommend.service.mongo.VideoScenairoSimilarService;
import com.miguan.recommend.service.recommend.*;
import com.miguan.recommend.service.xy.VideosCatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service(value = "videoRecommendServiceV5")
public class VideoRecommendServiceV5Impl extends AbstractRecommendService implements VideoRecommendService<VideoRecommendDto> {

    @Resource(name = "redisDB0Service")
    private RedisService redisDB0Service;
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
    private VideoScenairoSimilarService videoScenairoSimilarService;
    @Resource
    private ScenairoVideoService scenairoVideoService;
    @Resource
    private VideoHotspotService videoHotspotService;

    ExecutorService executor = new ThreadPoolExecutor(200, 1000, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5000));

    @Override
    public void recommend(BaseDto baseDto, VideoRecommendDto recommendDto) {
        PublicInfo publicInfo = baseDto.getPublicInfo();
        initRecommendParam(baseDto, recommendDto, recDB9Pool, mongoTemplate, videosCatService);
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

        // 热度视频返回的数量
        int hotVideoReturnCount = 7;
        if (isEmpty(recommendDto.getJlvideo())) {
            hotVideoReturnCount = 8;
        }

        int oneCatLimit = 2;
        int limitMulti = 20;
        if (baseDto.getPublicInfo().isNew()) {
            oneCatLimit = 3;
            limitMulti = 2;
        }

        List<String> recVideoList = predictService.videoTopK(videoPlayRateMap, videoCatMap, hotVideoReturnCount, oneCatLimit, limitMulti);
        recommendDto.setRecommendVideo(recVideoList);
        recommendDto.setRecommendVideoCat(videoCatMap);
        log.warn("推荐0.5 此次给uuid({})推荐了 {} 个视频，推荐视频ID集：{}, 激励视频 {} 个, 激励视频ID：{}",
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
            CompletableFuture<Integer> jlVideoFuture = getIncetiveHotVideo(baseDto, recommendDto, jlVideo, incentiveVideoHotServiceV3New, executor);
            futureList.add(jlVideoFuture);
        }

        // 相似视频和场景视频
        List<String> similarAndSceneVideos = new ArrayList<String>(380);

        // 根据用户最近观看过的3个视频，获取每个视频的相似视频10个
        List<String> videoPoolList = baseDto.getUserFeature().getVideoPoolList();
        CompletableFuture<Void> similarVideoFuture = findSimilarVideoFromUserVideoPool(similarAndSceneVideos, baseDto.getUuid(), videoPoolList, 10, videoScenairoSimilarService, executor);
        if (similarVideoFuture != null) {
            futureList.add(similarVideoFuture);
        }

        // 根据用户场景兴趣池的前5个场景，获取每个场景70个视频
        CompletableFuture<Void> sceneVideoFuture = this.findSceneVideo(similarAndSceneVideos, baseDto.getUuid(), baseDto.getUserFeature().getScenePoolList(), 50);
        if (sceneVideoFuture != null) {
            futureList.add(sceneVideoFuture);
        }

        // 最终查询到的推荐视频，按分类，放入map
        Map<Integer, List<VideoHotspotVo>> userCatVideoMap = new LinkedHashMap<>();
        // 根据用户分类兴趣池查询200个
        CompletableFuture<Void> userCatVideoFuture = this.findHotVideo(baseDto, recommendDto, 200, userCatVideoMap);
        futureList.add(userCatVideoFuture);

        int futureSize = futureList.size();
        CompletableFuture[] futures = new CompletableFuture[futureSize];
        for(int i=0; i < futureSize; i++){
            futures[i] = futureList.get(i);
        }

        CompletableFuture allFuture = CompletableFuture.allOf(futures);
        allFuture.join();

        // 将相似视频和场景视频ID转化为实体, 并根据分类放入userCatVideoMap
        if (!isEmpty(similarAndSceneVideos)) {
            // 将相似视频和场景视频ID做去重
            List<String> distnictVideoId = similarAndSceneVideos.stream().distinct().collect(Collectors.toList());
            similarAndSceneVideos = null;
            log.debug("{} 推荐0.5 共获取相似视频和场景视频{}个", baseDto.getUuid(), distnictVideoId.size());
            List<VideoHotspotVo> similarAndSceneVideosVo = videoHotspotService.findFromMongoById(distnictVideoId, recommendDto.getSensitiveState());
            distnictVideoId = null;
            // 对相似视频和场景视频进行屏蔽分类过滤
            List<VideoHotspotVo> finalSimilarAndSceneVideosVo = similarAndSceneVideosVo.stream().filter(e -> !recommendDto.getExcludeCatList().contains(e.getCatid())).collect(Collectors.toList());
            similarAndSceneVideosVo = null;

            int finalSimilarAndSceneVideosNum = finalSimilarAndSceneVideosVo.size();
            Map<Integer, List<VideoHotspotVo>> similarAndSceneCatVideoMap = finalSimilarAndSceneVideosVo.stream().collect(Collectors.groupingBy(VideoHotspotVo::getCatid));
            finalSimilarAndSceneVideosVo = null;

            if (finalSimilarAndSceneVideosNum > 200) {
                userCatVideoMap = similarAndSceneCatVideoMap;
            } else {
                Set<Integer> similarAndSceneCatVideoMapKeySet = similarAndSceneCatVideoMap.keySet();
                for (Integer catid : similarAndSceneCatVideoMapKeySet) {
                    List<VideoHotspotVo> similarAndSceneCatVideo = similarAndSceneCatVideoMap.get(catid);
                    if (userCatVideoMap.containsKey(catid)) {
                        List<VideoHotspotVo> userCatVideoList = userCatVideoMap.get(catid);
                        userCatVideoList.removeAll(similarAndSceneCatVideo);
                        userCatVideoList.addAll(similarAndSceneCatVideo);
                    } else {
                        userCatVideoMap.put(catid, similarAndSceneCatVideo);
                    }
                }
            }
        }

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
        log.warn("{} 推荐0.5 召回时长：{}，召回数量：{}，其中热门召回：{}", baseDto.getUuid(), (pt2 - pt1), allVideos.size(), catSum);

        // 获取排序值并排序
        Map<String, BigDecimal> sortedVideoMap = predictService.getVideoListPlayRate(baseDto, allVideos);
        long pt3 = System.currentTimeMillis();

        recommendDto.getJlvideo().addAll(jlVideo.stream().map(IncentiveVideoHotspot::getVideo_id).collect(Collectors.toList()));
        recommendDto.setJlvideoCat(jlVideo.stream().collect(Collectors.toMap(IncentiveVideoHotspot::getVideo_id, IncentiveVideoHotspot::getCatid)));
        log.info("{} 推荐0.5 排序总时长：{}", baseDto.getUuid(), (pt3 - pt2));
        return Arrays.asList(sortedVideoMap, allVideosCatMap);
    }

    public CompletableFuture<Void> findSceneVideo(List<String> similarAndSceneVideos, String uuid, List<String> scenePoolList, int getNum) {
        if (isEmpty(scenePoolList)) {
            return null;
        }
        List<String> top5ScenePoolList = Lists.newArrayList(scenePoolList.subList(0, Math.min(5, scenePoolList.size())));
        Function<String, Long> f = e -> {
            long start = System.currentTimeMillis();
            List<String> sceneVideos = scenairoVideoService.findVideoFromMongoOrCache(Integer.parseInt(e));
            sceneVideos = bloomFilterService.containMuilSplit(getNum, uuid, sceneVideos);
            if (isEmpty(sceneVideos)) {
                log.debug("{} 推荐0.5 场景[{}]视频查询到{}个", uuid, e, 0);
            } else {
                log.debug("{} 推荐0.5 场景[{}]视频查询到{}个", uuid, e, sceneVideos .size());
                similarAndSceneVideos.addAll(sceneVideos);
            }
            long time = System.currentTimeMillis() - start;
            log.debug("{} 推荐0.5 场景[{}]视频查询耗时:{}", uuid, e, time);
            return time;
        };

        CompletableFuture[] listFeture = top5ScenePoolList.stream().map(e -> {
            return CompletableFuture.completedFuture(e).thenApplyAsync(f, executor);
        }).toArray(size -> new CompletableFuture[size]);

        return CompletableFuture.allOf(listFeture);
    }

    public CompletableFuture<Void> findHotVideo(BaseDto baseDto, VideoRecommendDto recommendDto, int getNum, Map<Integer, List<VideoHotspotVo>> userCatVideo) {
        List<Integer> catIds = recommendDto.getUserCats();
        List<Integer> similarCatIds = recommendDto.getSimilarCats();

        int needAddCatNum = 7 - catIds.size();
        catIds.addAll(similarCatIds.subList(0, Math.min(needAddCatNum, similarCatIds.size())));

        double catNumRate = catIds.size() > 5 ? 0.3D : 0.5D;
        Map<Integer, Long> catNum = new HashMap<Integer, Long>();
        for (Integer catId : catIds) {
            Double cunrrentCatNum = getNum * catNumRate;
            catNum.put(catId, cunrrentCatNum.longValue());
            getNum -= cunrrentCatNum.longValue();
        }

        Function<Integer[], Number> f = e -> {
            long pt = System.currentTimeMillis();
            List<VideoHotspotVo> videoId1 = videoHotServiceV3.findAndFilter(baseDto.getUuid(), null, e[0], recommendDto.getSensitiveState(), e[1], null);
            if (isEmpty(videoId1)) {
                log.debug("{} 推荐0.5 获取用户兴趣分类[{}]视频：{} 个", baseDto.getUuid(), e[0], 0);
            } else {
                log.debug("{} 推荐0.5 获取用户兴趣分类[{}]视频：{} 个", baseDto.getUuid(), e[0], videoId1.size());
                userCatVideo.put(e[0], videoId1);
            }
            log.debug("{} 推荐0.5 获取用户兴趣分类视频时长：{}", baseDto.getUuid(), (System.currentTimeMillis() - pt));
            return System.currentTimeMillis() - pt;
        };
        CompletableFuture[] listFeture = catNum.entrySet().stream().map(e -> {
            Integer[] params = new Integer[]{e.getKey(), e.getValue().intValue()};
            return CompletableFuture.completedFuture(params).thenApplyAsync(f, executor);
        }).toArray(size -> new CompletableFuture[size]);
        return CompletableFuture.allOf(listFeture);

    }
}
