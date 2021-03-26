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
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service(value = "videoRecommendServiceV3")
public class VideoRecommendServiceV3Impl extends AbstractRecommendService implements VideoRecommendService<VideoRecommendDto> {

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
    @Resource(name = "offLineVideoServiceV3")
    private OffLineVideoService offLineVideoServiceV3;
    @Resource
    private BloomFilterService bloomFilterService;
    @Autowired
    private PredictService predictService;
    @Resource
    private FeatureService featureService;
    @Resource
    private EmbeddingService embeddingService;

    //public static ExecutorService executor = new ThreadPoolExecutor(200, 2000, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5000));

    /**
     * 计算每个分类视频个数函数
     */
    Function<List<Integer>, Integer> function = catids -> {
        int rn = RandomUtils.nextInt(1, 7);
        if (rn < 4) {
            return catids.get(0);
        } else if (rn < 6) {
            return catids.size() < 2 ? catids.get(catids.size() - 1) : catids.get(1);
        }
        return catids.size() < 3 ? catids.get(catids.size() - 1) : catids.get(2);
    };

    @Override
    public void recommend(BaseDto baseDto, VideoRecommendDto recommendDto) {
        PublicInfo publicInfo = baseDto.getPublicInfo();
        super.initRecommendParam(baseDto, recommendDto, recDB9Pool, mongoTemplate, videosCatService);

        boolean needFilter = false;
        // 判断是否是新用户
        int cacheExpireTime = publicInfo.isNew() ? 30 : 60;
        List<Object> videoInfoForRecList = null;
        try (Jedis con = recDB9Pool.getResource()) {
            String key = String.format(RedisRecommendConstants.key_user_rec_video_list, publicInfo.getUuid());
            String cacheList = con.get(key);
//            String cacheList = null;
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
            videoPlayRateMap = filterSortedMap(videoPlayRateMap, publicInfo.getUuid());
            CompletableFuture<Integer> incetiveHotVideo = getIncetiveHotVideo(baseDto, recommendDto, jlVideo);
            incetiveHotVideo.join();
            recommendDto.getJlvideo().addAll(jlVideo.stream().map(IncentiveVideoHotspot::getVideo_id).collect(Collectors.toList()));
            recommendDto.setJlvideoCat(jlVideo.stream().collect(Collectors.toMap(IncentiveVideoHotspot::getVideo_id, IncentiveVideoHotspot::getCatid)));
        }

        List<String> recVideoList = new ArrayList<>();
        if (videoInfoForRecList == null || videoInfoForRecList.isEmpty()) {
            return;
        }

        int needCount = recommendDto.getVideoNum();
        if (CollectionUtils.isEmpty(recommendDto.getJlvideo())) {
            ++needCount;
        }

        int oneCatLimit = 2;
        int limitMulti = 20;
        if (baseDto.getPublicInfo().isNew()) {
            oneCatLimit = 3;
            limitMulti = 2;
        }

        recVideoList = predictService.videoTopK(videoPlayRateMap, videoCatMap, needCount, oneCatLimit, limitMulti);
        recommendDto.setRecommendVideo(recVideoList);
        recommendDto.setRecommendVideoCat(videoCatMap);
        log.warn("推荐0.3此次给uuid({})推荐了 {} 个视频，推荐视频ID集：{}, 激励视频 {} 个, 激励视频ID：{}",
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
        // 获取用户的离线视频
//        List<VideoHotspotVo> offLineVideos = new ArrayList<VideoHotspotVo>();
//        CompletableFuture<Integer> offlineVideoFuture = getOffLineVideo(recommendDto, offLineVideos);

        CompletableFuture<Void> allFuture = null;
        if (recommendDto.getIncentiveVideoNum() == 0) {
//            allFuture = CompletableFuture.allOf(userCatVideoFuture, offlineVideoFuture);
            allFuture = CompletableFuture.allOf(userCatVideoFuture);
        } else {
//            allFuture = CompletableFuture.allOf(userCatVideoFuture, jlVideoFuture, offlineVideoFuture);
            allFuture = CompletableFuture.allOf(userCatVideoFuture, jlVideoFuture);
        }
        long pt = System.currentTimeMillis();
        allFuture.join();

        // 所有用户兴趣分类视频和离线视频
        List<VideoHotspotVo> allVideos = new ArrayList<VideoHotspotVo>();
        Map<String, Integer> userCatVideoAndOfflineVideoMap = new LinkedHashMap<>();
        int catSum = 0;
        if("2".equals(baseDto.getEmbeddingGroup())){
            //双塔向量视频召回
            List<VideoHotspotVo> videoEmbedding = embeddingService.findFromEsVideoEmbedding(baseDto.publicInfo, allVideos);
            if(!isEmpty(videoEmbedding)){
                Map<Integer, List<VideoHotspotVo>> embeddingCatVideoMap = videoEmbedding.stream().collect(Collectors.groupingBy(VideoHotspotVo::getCatid));
                userCatVideoMap = this.mergeEmbeddingMap(userCatVideoMap, embeddingCatVideoMap);
            }
        }
        for (Integer cat : userCatVideoMap.keySet()) {
            List<VideoHotspotVo> catVideos = userCatVideoMap.get(cat);
            if (isEmpty(catVideos)) {
                continue;
            }
            if(!isEmpty(recommendDto.getExcludeCatList())){
                catVideos = catVideos.stream().filter(e -> !recommendDto.getExcludeCatList().contains(e.getCatid())).collect(Collectors.toList());
            }
            catSum += catVideos.size();
            allVideos.removeAll(catVideos);
            allVideos.addAll(catVideos);
            catVideos.stream().forEach(e -> {
                userCatVideoAndOfflineVideoMap.put(e.getVideo_id(), e.getCatid());
            });
        }


//        if(!isEmpty(offLineVideos)){
//            allVideos.removeAll(offLineVideos);
//            allVideos.addAll(offLineVideos);
//            offLineVideos.stream().forEach(e -> {
//                userCatVideoAndOfflineVideoMap.put(e.getVideo_id(), e.getCatid());
//            });
//        }
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
     * 第二个map合并到第一个map
     * @param userCatVideoMap 热门召回
     * @param embeddingCatVideoMap 向量召回
     * @return
     */
    private Map<Integer, List<VideoHotspotVo>> mergeEmbeddingMap(Map<Integer, List<VideoHotspotVo>> userCatVideoMap, Map<Integer, List<VideoHotspotVo>> embeddingCatVideoMap) {
        if(isEmpty(embeddingCatVideoMap)) {
            return userCatVideoMap;
        }
        for (Map.Entry<Integer, List<VideoHotspotVo>> entry : embeddingCatVideoMap.entrySet()) {
            List<VideoHotspotVo> embeddingVideos = entry.getValue();
            Integer catId = entry.getKey();
            if(isEmpty(embeddingVideos)) {
                continue;
            }
            List<VideoHotspotVo> catVideos = userCatVideoMap.get(catId);
            if(isEmpty(catVideos)) {
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
        // 计算用户的兴趣分类，每个分类的个数
        Map<Integer, Long> catNum = new HashMap<Integer, Long>();
        List<Integer> catIds = recommendDto.getUserCats();
        List<Integer> similarCatIds = recommendDto.getSimilarCats();

        int needAddCatNum = 7 - catIds.size();
        catIds.addAll(similarCatIds.subList(0, Math.min(needAddCatNum, similarCatIds.size())));

        long catFindCount = 100;
        if("2".equals(baseDto.getEmbeddingGroup())){
            catFindCount = 20;
        }
        for (Integer catId : catIds) {
            if (catFindCount <= 20) {
                catFindCount = 20;
            }
            catNum.put(catId, catFindCount);
            catFindCount -= 20;
        }
//        UserFeature userFeature = baseDto.getUserFeature();
//        List<String> blockCity = Arrays.asList("北京市","上海市","广州市","杭州市","深圳市");
//        if(baseDto.isABTest() && userFeature.getPublicInfo().isNew()
//                && !blockCity.contains(userFeature.getCity())){
//            for(Integer cid:catNum.keySet()){
//                catNum.put(cid, Math.min(60L,catNum.get(cid)));
//            }
//            catNum.put(-1, 80L);
//            log.debug("is head new user");
//        }
//        log.debug("is head is ABTEst:"+(baseDto.isABTest()?"true":"false")+"is new:"+(userFeature.getPublicInfo().isNew()?"true":"false")
//                +"city:"+userFeature.getCity());
        Function<Integer[], Number> f = e -> {
            long pt = System.currentTimeMillis();
            VideoQueryDto<VideoHotspotVo> queryDto = new VideoQueryDto<VideoHotspotVo>(baseDto, e[0], recommendDto.getSensitiveState(), e[1]);
            if(baseDto.isVideo98Group()){
                queryDto.setExcludedSource(excludeSource);
            }
            List<VideoHotspotVo> videoId1 = videoHotServiceV3.findAndFilter(queryDto, null);
//            if (log.isInfoEnabled()) {
                log.info("{} 推荐0.3 获取用户兴趣分类[{}]视频：{} 个", uuid, e[0], isEmpty(videoId1) ? 0 : videoId1.size());
//            }
            if (!isEmpty(videoId1)) {
                userCatVideo.put(e[0], videoId1);
            }
            if (log.isInfoEnabled()) {
                log.info("{} 推荐0.3 获取用户兴趣分类视频时长：{}", uuid, (System.currentTimeMillis() - pt));
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
     * 通过相似分类获取视频ID
     *
     * @param recommendDto
     * @return
     */
    public CompletableFuture<Integer> getSimilarVideo(BaseDto baseDto, VideoRecommendDto recommendDto) {
        String uuid = baseDto.getUuid();
        return CompletableFuture.supplyAsync(() -> {
            //lzhong 根据热度标签获取第3规则的1个视频
            long pt = System.currentTimeMillis();
            Integer hotsportCatid = function.apply(recommendDto.getSimilarCats());
            //这里要去除这个标签，因为后面补足视频的时候需要用到hotspotCatids变量
            recommendDto.getSimilarCats().remove(hotsportCatid);
            List<String> videoId2 = videoHotServiceV3.findAndFilter(uuid, null, hotsportCatid, recommendDto.getSensitiveState(), 1, null);
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.3 获取用户兴趣分类的相似分类[{}]视频：{}", uuid, hotsportCatid, isEmpty(videoId2) ? null : JSON.toJSONString(videoId2));
            }
            if (!isEmpty(videoId2)) {
                recommendDto.setSimilarvideo(videoId2);
            }
            if (log.isInfoEnabled()) {
                log.info("{} 推荐0.3 用户兴趣分类的相似分类时长：{}", uuid, (System.currentTimeMillis() - pt));
            }
            return videoId2 == null ? 0 : videoId2.size();
        }, executor);
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
                log.debug("{} 推荐0.3 获取的激励视频：{} 个", uuid, isEmpty(findList) ? 0 : findList.size());
            }
            if (isEmpty(findList)) {
                log.info("推荐0.3 未找到 uuid={} 的激励视频", uuid);
                return 0;
            } else {
                jlVideo.addAll(findList);
            }
            if (log.isInfoEnabled()) {
                log.info("{} 推荐0.3 获取的激励视频时长：{}", uuid, (System.currentTimeMillis() - pt));
            }
            return findList.size();
        }, executor);
    }

    /**
     * 获取离线视频
     *
     * @param recommendDto
     * @return
     */
    public CompletableFuture<Integer> getOffLineVideo(BaseDto baseDto, VideoRecommendDto recommendDto, List<VideoHotspotVo> offLineVideos) {
        String uuid = baseDto.getUuid();
        return CompletableFuture.supplyAsync(() -> {
            //lzhong 根据热度标签获取第2规则的1个视频
            long pt = System.currentTimeMillis();
            List<VideoHotspotVo> findList = offLineVideoServiceV3.find(uuid, 100, recommendDto.getExcludeCatList());
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.3 获取离线的视频：{}", uuid, isEmpty(findList) ? null : JSON.toJSONString(findList));
            }
            if (!isEmpty(findList)) {
                offLineVideos.addAll(findList);
            }
            if (log.isInfoEnabled()) {
                log.info("{} 推荐0.3 获取离线的视频时长：{}", uuid, (System.currentTimeMillis() - pt));
            }
            return findList.size();
        }, executor);
    }

    /**
     * 补充视频
     *
     * @param uuid              用户的UUId
     * @param userCat           用户的兴趣分类
     * @param userCatVideoMap   根据用户的兴趣分类，查询到的视频ID MAP
     * @param similarvideo      相似分类视频
     * @param recommendVideo    最终返回的推荐热度视频ID集合(不含激励视频)
     * @param incentiveVideoNum 需要返回的激励视频个数
     * @return
     */
    public int replenishVideo(String uuid, List<Integer> userCat, Map<Integer, List<String>> userCatVideoMap, List<String> similarvideo, List<String> recommendVideo, int incentiveVideoNum) {
        long pt = System.currentTimeMillis();
        log.debug("{} 推荐0.3 各分类获取视频情况：{}", uuid, JSON.toJSONString(userCatVideoMap));
        userCat.forEach(cat -> {
            if (userCatVideoMap.containsKey(cat)) {
                List<String> tmpL = userCatVideoMap.get(cat);
                recommendVideo.addAll(tmpL);
                userCatVideoMap.remove(cat);
            }
        });
        recommendVideo.addAll(similarvideo);

        // 计算需要补充视频的个数
        int replenishCount = 8 - recommendVideo.size();
        if (incentiveVideoNum > 0) {
            replenishCount = replenishCount - incentiveVideoNum;
        }

        if (replenishCount > 0) {
            log.error("{} 推荐0.3 的视频数量不足，需要补{}个视频", uuid, replenishCount);
            List<String> replenishVideo = videoHotServiceV3.findAndFilter(uuid, userCat, null, null, replenishCount, recommendVideo);
            if (log.isInfoEnabled()) {
                log.info("{} 推荐0.3 补足视频查询耗时：{}", uuid, System.currentTimeMillis() - pt);
            }
            if (isEmpty(replenishVideo) || replenishVideo.size() < replenishCount) {
                log.error("{} 推荐0.3 没有补全视频", uuid);
            } else {
                recommendVideo.addAll(replenishVideo);
            }
        }
        log.info("{} 推荐0.3 补足视频耗时：{}", uuid, System.currentTimeMillis() - pt);
        return 0;
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

}
