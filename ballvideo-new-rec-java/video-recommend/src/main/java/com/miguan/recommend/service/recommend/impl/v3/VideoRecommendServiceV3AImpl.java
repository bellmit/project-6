package com.miguan.recommend.service.recommend.impl.v3;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.miguan.recommend.bo.BaseDto;
import com.miguan.recommend.bo.PublicInfo;
import com.miguan.recommend.bo.VideoQueryDto;
import com.miguan.recommend.bo.VideoRecommendDto;
import com.miguan.recommend.common.constants.RedisRecommendConstants;
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
@Service(value = "videoRecommendServiceV3A")
public class VideoRecommendServiceV3AImpl extends AbstractRecommendService implements VideoRecommendService<VideoRecommendDto> {

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
    @Resource(name = "offLineVideoServiceV3")
    private OffLineVideoService offLineVideoServiceV3;
    @Resource
    private BloomFilterService bloomFilterService;
    @Autowired
    private PredictService predictService;
    @Resource
    private FeatureService featureService;

    private static ExecutorService executor = new ThreadPoolExecutor(200, 2000, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5000));

    @Override
    public void recommend(BaseDto baseDto, VideoRecommendDto recommendDto) {
        PublicInfo publicInfo = baseDto.getPublicInfo();
        //this.initRecommendParam(baseDto, recommendDto);

        boolean needFilter = false;
        // 判断是否是新用户
        int cacheExpireTime = publicInfo.isNew() ? 30 : 60;
        List<Object> videoInfoForRecList = null;
        try (Jedis con = recDB9Pool.getResource()) {
            String key = String.format(RedisRecommendConstants.key_user_rec_video_list, publicInfo.getUuid());
            String cacheList = con.get(key);
            if (cacheList == null || cacheList.isEmpty()) {
                videoInfoForRecList = this.getAsync(baseDto, recommendDto);
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
            videoPlayRateMap = filterSortedMap(videoPlayRateMap, publicInfo.getUuid(), bloomFilterService);
            CompletableFuture<Integer> incetiveHotVideo = this.getIncetiveHotVideo(baseDto, recommendDto, jlVideo, executor);
            incetiveHotVideo.join();
            recommendDto.getJlvideo().addAll(jlVideo.stream().map(IncentiveVideoHotspot::getVideo_id).collect(Collectors.toList()));
            recommendDto.setJlvideoCat(jlVideo.stream().collect(Collectors.toMap(IncentiveVideoHotspot::getVideo_id, IncentiveVideoHotspot::getCatid)));
        }

        if (videoInfoForRecList == null || videoInfoForRecList.isEmpty()) {
            return;
        }

        List<String> recVideoList = new ArrayList<>();
        Map<Integer, List<String>> catVideoListMap = predictService.videoTopK(videoPlayRateMap, videoCatMap);
        List<String> defaultCatVideos = catVideoListMap.get(Integer.valueOf(recommendDto.getDefaultCatList().get(0)));
        if (!isEmpty(defaultCatVideos)) {
            List<String> subList = defaultCatVideos.subList(0, Math.min(5, defaultCatVideos.size()));
            recVideoList.addAll(new ArrayList<>(subList));
        }

        int needAddNum = isEmpty(recommendDto.getJlvideo()) ? 8 : 7 - recVideoList.size();
        if (!isEmpty(recommendDto.getUserCats())) {
            for (Integer userCatId : recommendDto.getUserCats()) {
                List<String> userCatVideos = catVideoListMap.get(userCatId);
                if (!isEmpty(userCatVideos) && needAddNum > 0) {
                    recVideoList.add(userCatVideos.get(0));
                    needAddNum -= 1;
                }
            }
        }

        if (!isEmpty(recommendDto.getSimilarCats())) {
            for (Integer similarCatId : recommendDto.getSimilarCats()) {
                List<String> similarCatVideos = catVideoListMap.get(similarCatId);
                if (!isEmpty(similarCatVideos) && needAddNum > 0) {
                    recVideoList.add(similarCatVideos.get(0));
                    needAddNum -= 1;
                }
            }
        }

        if (needAddNum > 0) {
            Set<Integer> catids = catVideoListMap.keySet();
            for (Integer catid : catids) {
                List<String> catVideos = catVideoListMap.get(catid);
                catVideos.removeAll(recVideoList);
                if (catVideos.size() < needAddNum) {
                    recVideoList.addAll(catVideos);
                    needAddNum -= catVideos.size();
                } else {
                    recVideoList.addAll(catVideos.subList(0, needAddNum));
                    needAddNum = 0;
                }

                if(needAddNum == 0){
                    break;
                }
            }
        }

        recommendDto.setRecommendVideo(recVideoList);
        recommendDto.setRecommendVideoCat(videoCatMap);
        log.warn("推荐0.3此次给uuid({})推荐了 {} 个视频，推荐视频ID集：{}, 激励视频 {} 个, 激励视频ID：{}",
                publicInfo.getUuid(), recVideoList.size(), JSON.toJSONString(recVideoList), recommendDto.getJlvideo().size(), JSON.toJSONString(recommendDto.getJlvideo()));

        // 将查询的视频放入Bloom过滤器
        bloomVideo(publicInfo.getUuid(), recVideoList, recommendDto.getJlvideo(), bloomFilterService, executor);
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
        log.warn("{} 推荐0.3时长，召回：{}，召回数量：{}，其中热门召回：{}，协同过滤召回：{}",
                baseDto.getUuid(), (pt2 - pt), allVideos.size(), catSum, 0);

        // 获取排序值并排序
        Map<String, BigDecimal> sortedVideoMap = predictService.getVideoListPlayRate(baseDto, allVideos);
        long pt3 = System.currentTimeMillis();

        recommendDto.getJlvideo().addAll(jlVideo.stream().map(IncentiveVideoHotspot::getVideo_id).collect(Collectors.toList()));
        recommendDto.setJlvideoCat(jlVideo.stream().collect(Collectors.toMap(IncentiveVideoHotspot::getVideo_id, IncentiveVideoHotspot::getCatid)));
        log.info("推荐0.3时长，排序总时长：" + (pt3 - pt2));
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
        List<Integer> defaultCatIds = recommendDto.getDefaultCatList();
//        List<Integer> userCatIds = recommendDto.getUserCats();
//        List<Integer> similarCatIds = recommendDto.getSimilarCats();

        if (!isEmpty(defaultCatIds)) {
            for (Integer catid : defaultCatIds) {
                catNum.put(catid, 100L);
            }
        }

//        if (!isEmpty(userCatIds)) {
//            for (Integer userCatId : userCatIds) {
//                catNum.put(userCatId, 30L);
//            }
//        }
//
//        if (!isEmpty(similarCatIds)) {
//            int defaultAddUserCatNum = catNum.size();
//            int stop = Math.min(6 - defaultAddUserCatNum, similarCatIds.size());
//            for (int i = 0; i < stop; i++) {
//                catNum.put(similarCatIds.get(i), 30L);
//            }
//        }
        Function<Integer[], Number> f = e -> {
            long pt = System.currentTimeMillis();
            VideoQueryDto<VideoHotspotVo> queryDto = new VideoQueryDto<VideoHotspotVo>(baseDto, e[0], recommendDto.getSensitiveState(), e[1]);
            List<VideoHotspotVo> videoId1 = videoHotServiceV3.findAndFilter(queryDto, null);
//            List<VideoHotspotVo> videoId1 = videoHotServiceV3.findAndFilter(uuid, null, e[0], recommendDto.getSensitiveState(), e[1], null);
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.3A 获取用户兴趣分类[{}]视频：{} 个", uuid, e[0], isEmpty(videoId1) ? 0 : videoId1.size());
            }
            if (!isEmpty(videoId1)) {
                userCatVideo.put(e[0], videoId1);
            }
            if (log.isInfoEnabled()) {
                log.info("{} 推荐0.3A 获取用户兴趣分类视频时长：{}", uuid, (System.currentTimeMillis() - pt));
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
            //int jlCatid = function.apply(recommendDto.getUserCats());
            List<IncentiveVideoHotspot> findList = incentiveVideoHotServiceV3New.findAndFilter(uuid, recommendDto.getDefaultCatList().get(0), recommendDto.getSensitiveState(), recommendDto.getIncentiveVideoNum());
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐 获取的激励视频：{} 个", uuid, isEmpty(findList) ? 0 : findList.size());
            }
            if (isEmpty(findList)) {
                log.info("推荐 未找到 uuid={} 的激励视频", uuid);
                return 0;
            } else {
                jlVideo.addAll(findList);
            }
            if (log.isInfoEnabled()) {
                log.info("{} 推荐 获取的激励视频时长：{}", uuid, (System.currentTimeMillis() - pt));
            }
            return findList.size();
        }, executor);
    }

    public void initRecommendParam(BaseDto baseDto, VideoRecommendDto recommendDto) {
        List<Integer> userCats = null;
        try (Jedis con = recDB9Pool.getResource()) {
            List<String> tmp = con.hmget("bg_sUp", baseDto.getUuid());
            if (!isEmpty(tmp)) {
                String[] str = tmp.get(0).split(",");
                if (str.length > 3) {
                    str = ArrayUtils.subarray(str, 0, 3);
                }
                userCats = Stream.of(str).map(Integer::valueOf).limit(3).collect(Collectors.toList());
                if (!isEmpty(recommendDto.getExcludeCatList())) {
                    userCats.removeAll(recommendDto.getExcludeCatList());
                }
                recommendDto.setUserCats(userCats);
            }
        } catch (Exception e) {
            log.warn("{} 用户实时分类池获取异常", baseDto.getUuid());
        }

        List<Integer> similarCat = null;
        if (isEmpty(userCats)) {
            similarCat = super.getSimilarCat(recommendDto.getDefaultCatList().get(0), recommendDto.getExcludeCatList(), baseDto.isABTest(), logMongoTemplate, videosCatService);
        } else {
            similarCat = super.getSimilarCat(userCats.get(0), recommendDto.getExcludeCatList(), baseDto.isABTest(), logMongoTemplate, videosCatService);
        }

        if (!isEmpty(similarCat)) {
            if (!isEmpty(recommendDto.getExcludeCatList())) {
                similarCat.removeAll(recommendDto.getExcludeCatList());
            }
            recommendDto.setSimilarCats(similarCat);
        }
    }

}
