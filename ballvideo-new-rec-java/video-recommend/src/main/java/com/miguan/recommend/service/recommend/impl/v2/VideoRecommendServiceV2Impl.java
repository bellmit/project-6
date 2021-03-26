package com.miguan.recommend.service.recommend.impl.v2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.bo.BaseDto;
import com.miguan.recommend.bo.PublicInfo;
import com.miguan.recommend.bo.VideoRecommendDto;
import com.miguan.recommend.common.constants.XyConstants;
import com.miguan.recommend.entity.mongo.CatHotspotVo;
import com.miguan.recommend.service.BloomFilterService;
import com.miguan.recommend.service.recommend.IncentiveVideoHotService;
import com.miguan.recommend.service.recommend.OffLineVideoService;
import com.miguan.recommend.service.recommend.VideoHotService;
import com.miguan.recommend.service.recommend.VideoRecommendService;
import com.miguan.recommend.service.xy.VideosCatService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service(value = "videoRecommendServiceV2")
public class VideoRecommendServiceV2Impl implements VideoRecommendService<VideoRecommendDto> {

    @Resource(name = "recDB9Pool")
    private JedisPool recDB9Pool;
    @Resource(name = "logMongoTemplate")
    private MongoTemplate mongoTemplate;
    @Resource
    private VideosCatService videosCatService;
    @Resource(name = "videoHotServiceV2")
    private VideoHotService videoHotServiceV2;
    @Resource(name = "incentiveVideoHotServiceV2")
    private IncentiveVideoHotService incentiveVideoHotServiceV2;
    @Resource(name = "offLineVideoServiceV2")
    private OffLineVideoService offLineVideoService;
    @Resource
    private BloomFilterService bloomFilterService;

    ExecutorService executor = new ThreadPoolExecutor(200, 1000, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5000));

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
        this.initRecommendParam(baseDto, recommendDto);
        // 获取用户的兴趣分类视频
        CompletableFuture<Void> userCatVideoFuture = getHotVideo(baseDto, recommendDto);
        // 获取1个相似分类的热度视频
        CompletableFuture<Integer> similarVideoFuture = getSimilarVideo(baseDto, recommendDto);
        // 获取激励视频
        CompletableFuture<Integer> jlVideoFuture = null;
        if (recommendDto.getIncentiveVideoNum() > 0) {
            jlVideoFuture = getIncetiveHotVideo(baseDto, recommendDto);
        }
        // 获取用户的离线视频
        CompletableFuture<Integer> offlineVideoFuture = getOffLineVideo(baseDto, recommendDto);

        List<String> recommendVideo = new ArrayList<>();
        CompletableFuture<Integer> replenishVideoFuture =
                userCatVideoFuture.thenCombineAsync(similarVideoFuture,
                        (d1, d2) -> replenishVideo(publicInfo.getUuid(), recommendDto.getUserCats(), recommendDto.getUserCatVideoMap(),
                                recommendDto.getSimilarvideo(), recommendVideo, recommendDto.getIncentiveVideoNum(), recommendDto.getSensitiveState()), executor);

        CompletableFuture<Void> allFuture = null;
        if (recommendDto.getIncentiveVideoNum() == 0) {
            allFuture = CompletableFuture.allOf(offlineVideoFuture, replenishVideoFuture);
        } else {
            allFuture = CompletableFuture.allOf(offlineVideoFuture, jlVideoFuture, replenishVideoFuture);
        }
        allFuture.join();

        if (!isEmpty(recommendVideo) && !isEmpty(recommendDto.getOffLinevideo())) {
            List<String> offLinevideo = recommendDto.getOffLinevideo();
            log.warn("推荐0.2 此次给uuid({})【离线】推荐了 {} 个视频，推荐视频ID集：{}", publicInfo.getUuid(), offLinevideo.size(), JSON.toJSONString(offLinevideo));
            // 将离线视频放到前面，并移除与离线视频数量相同的，排在前面的原有视频
            recommendVideo.removeAll(offLinevideo);
            recommendVideo.addAll(0, offLinevideo);
            int videoSize = recommendDto.getIncentiveVideoNum() == 0 ? 8 : 7;
            while (recommendVideo.size() > videoSize) {
                recommendVideo.remove(recommendVideo.size() - 2);
            }
        }
        recommendDto.setRecommendVideo(recommendVideo);
        // 将查询的视频放入Bloom过滤器
        bloomVideo(publicInfo.getUuid(), recommendVideo, recommendDto.getJlvideo());
    }

    /**
     * 初始化推荐参数
     *
     * @param recommendDto
     */
    public void initRecommendParam(BaseDto baseDto, VideoRecommendDto recommendDto) {
        String uuid = baseDto.getUuid();
        // 获取用户的兴趣分类池前3个
        List<Integer> userCat = getUserCats(uuid, recommendDto.getDefaultCatList());
        // 根据用户的兴趣分类池的第一个分类，获取相似的热度分类，并去除需要屏蔽的分类
        List<Integer> similarCat = getSimilarCat(userCat.get(0), recommendDto.getExcludeCatList());
        if (!isEmpty(recommendDto.getExcludeCatList())) {
            similarCat.removeAll(recommendDto.getExcludeCatList());
            similarCat.removeAll(userCat);
            recommendDto.setSimilarCats(similarCat);
        } else {
            similarCat.add(userCat.get(0));
        }
        // 用户的兴趣分类池去除需要屏蔽的分类
        // 如果用户兴趣分类池不足3个，从相似的热度分类中补足
        int diffCount = 3 - userCat.size();
        if (diffCount > 0 && !isEmpty(similarCat)) {
            similarCat.removeAll(userCat);
            if (similarCat.size() < diffCount) {
                diffCount = similarCat.size();
            }
            for (int i = 0; i < diffCount; i++) {
                userCat.add(similarCat.get(i));
            }
        }
        recommendDto.setUserCats(userCat);
        log.debug("{} 推荐0.2 获取到的用户兴趣分类>>{}", uuid, JSONObject.toJSONString(userCat));
        log.debug("{} 推荐0.2 获取到的用户兴趣分类的相似分类>>{}", uuid, JSONObject.toJSONString(similarCat));
    }

    /**
     * 获取用户的兴趣分类
     *
     * @param uuid          用户ID
     * @param defaultCatIds 渠道默认分类
     * @return
     */
    public List<Integer> getUserCats(String uuid, List<Integer> defaultCatIds) {
        //从redis获取
        String userCatids = null;
        try (Jedis con = recDB9Pool.getResource()) {
            List<String> tmp = con.hmget("bg_sUp", uuid);
            if (!isEmpty(tmp)) {
                userCatids = tmp.get(0);
            }
            tmp = null;
        } catch (Exception e) {
        }
        if (isNotBlank(userCatids)) {
            String[] str = userCatids.split(",");
            if (str.length > 3) {
                str = ArrayUtils.subarray(str, 0, 3);
            }
            return Stream.of(str).map(Integer::valueOf).collect(Collectors.toList());
        }

        if (isEmpty(defaultCatIds)) {
            log.error("获取渠道默认分类为空");
            return new ArrayList<>();
        }
        return defaultCatIds;
    }

    /**
     * 根据用户的第一兴趣分类，获取相似分类
     *
     * @param catid
     * @param excludeCatList
     * @return
     */
    public List<Integer> getSimilarCat(Integer catid, List<Integer> excludeCatList) {
        //从mongo获取
        Query query = Query.query(Criteria.where("parent_catid").is(catid));
        if (!isEmpty(excludeCatList)) {
            query.addCriteria(Criteria.where("catid").nin(excludeCatList));
        }
        query.with(Sort.by(Sort.Order.asc("weights")));
        //query.fields().include("catid").exclude("_id");
        List<CatHotspotVo> list = mongoTemplate.find(query, CatHotspotVo.class, "cat_hotspot");
        if (isEmpty(list)) {
            log.error("此标签 {} 相似的热度标签未找到", catid);
            List<String> catIds = videosCatService.getCatIdsByStateAndType(XyConstants.open, XyConstants.FIRST_VIDEO_CODE);
            List<Integer> intCatIds = catIds.stream().map(Integer::valueOf).collect(Collectors.toList());
            if (!isEmpty(excludeCatList)) {
                intCatIds.removeAll(excludeCatList);
            }
            return intCatIds;
        }
        return list.stream().map(CatHotspotVo::getCatid).collect(Collectors.toList());
    }

    /**
     * 通过用户的兴趣分类获取视频ID
     *
     * @param recommendDto
     * @return
     */
    public CompletableFuture<Void> getHotVideo(BaseDto baseDto, VideoRecommendDto recommendDto) {
        String uuid = baseDto.getUuid();
        int hotVideoNum = 8;
        if (recommendDto.getIncentiveVideoNum() > 0) {
            hotVideoNum -= recommendDto.getIncentiveVideoNum();
        }
        // 计算用户的兴趣分类，每个分类的个数
        Map<Integer, Long> catNum = IntStream.range(1, hotVideoNum).mapToObj(i -> function.apply(recommendDto.getUserCats()))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Function<Integer[], Number> f = e -> {
            long pt = System.currentTimeMillis();
            List<String> videoId1 = videoHotServiceV2.findAndFilter(uuid, null, e[0], recommendDto.getSensitiveState(), e[1], null);
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.2 获取用户兴趣分类[{}]视频：{}", uuid, e[0], isEmpty(videoId1) ? null : JSON.toJSONString(videoId1));
            }
            if (!isEmpty(videoId1)) {
                recommendDto.getUserCatVideoMap().put(e[0], videoId1);
            }
            if (log.isInfoEnabled()) {
                log.info("{} 推荐0.2 获取用户兴趣分类视频时长：{}", uuid, (System.currentTimeMillis() - pt));
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
            List<String> videoId2 = videoHotServiceV2.findAndFilter(uuid, null, hotsportCatid, recommendDto.getSensitiveState(), 1, null);
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.2 获取用户兴趣分类的相似分类[{}]视频：{}", uuid, hotsportCatid, isEmpty(videoId2) ? null : JSON.toJSONString(videoId2));
            }
            if (!isEmpty(videoId2)) {
                recommendDto.setSimilarvideo(videoId2);
            }
            if (log.isInfoEnabled()) {
                log.info("{} 推荐0.2 用户兴趣分类的相似分类时长：{}", uuid, (System.currentTimeMillis() - pt));
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
    public CompletableFuture<Integer> getIncetiveHotVideo(BaseDto baseDto, VideoRecommendDto recommendDto) {
        String uuid = baseDto.getUuid();
        return CompletableFuture.supplyAsync(() -> {
            //lzhong 根据热度标签获取第4规则的1个视频(激励视频)
            long pt = System.currentTimeMillis();
            int jlCatid = function.apply(recommendDto.getUserCats());
            List<String> jlvideo = incentiveVideoHotServiceV2.findAndFilter(uuid, jlCatid, recommendDto.getSensitiveState(), recommendDto.getIncentiveVideoNum());
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.2 获取的激励视频：{}", uuid, isEmpty(jlvideo) ? null : JSON.toJSONString(jlvideo));
            }
            if (isEmpty(jlvideo)) {
                log.warn("推荐0.2 未找到 uuid={} 的激励视频", uuid);
            } else {
                recommendDto.setJlvideo(jlvideo);
            }
            if (log.isInfoEnabled()) {
                log.info("{} 推荐0.2 获取的激励视频时长：{}", uuid, (System.currentTimeMillis() - pt));
            }
            return jlvideo.size();
        }, executor);
    }

    /**
     * 获取离线视频
     *
     * @param recommendDto
     * @return
     */
    public CompletableFuture<Integer> getOffLineVideo(BaseDto baseDto, VideoRecommendDto recommendDto) {
        String uuid = baseDto.getUuid();
        return CompletableFuture.supplyAsync(() -> {
            //lzhong 根据热度标签获取第2规则的1个视频
            long pt = System.currentTimeMillis();
            List<String> videoId2 = offLineVideoService.find(uuid, 5, recommendDto.getExcludeCatList());
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.2 获取离线的视频：{}", uuid, isEmpty(videoId2) ? null : JSON.toJSONString(videoId2));
            }
            if (!isEmpty(videoId2)) {
                recommendDto.getOffLinevideo().addAll(videoId2);
            }
            if (log.isInfoEnabled()) {
                log.info("{} 推荐0.2 获取离线的视频时长：{}", uuid, (System.currentTimeMillis() - pt));
            }
            return videoId2.size();
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
    public int replenishVideo(String uuid, List<Integer> userCat, Map<Integer, List<String>> userCatVideoMap, List<String> similarvideo, List<String> recommendVideo, int incentiveVideoNum, Integer sensitive) {
        long pt = System.currentTimeMillis();
        log.debug("{} 推荐0.2 各分类获取视频情况：{}", uuid, JSON.toJSONString(userCatVideoMap));
        userCat.forEach(cat -> {
            if (userCatVideoMap.containsKey(cat)) {
                List<String> tmpL = userCatVideoMap.get(cat);
                recommendVideo.addAll(tmpL);
                userCatVideoMap.remove(cat);
            }
        });
        if (!isEmpty(similarvideo)) {
            recommendVideo.addAll(similarvideo);
        }

        // 计算需要补充视频的个数
        int replenishCount = 8 - recommendVideo.size();
        if (incentiveVideoNum > 0) {
            replenishCount = replenishCount - incentiveVideoNum;
        }

        if (replenishCount > 0) {
            log.error("{} 推荐0.2 的视频数量不足，需要补{}个视频", uuid, replenishCount);
            List<String> replenishVideo = videoHotServiceV2.findAndFilter(uuid, userCat, null, replenishCount, sensitive, recommendVideo);
            if (log.isInfoEnabled()) {
                log.info("{} 推荐0.2 补足视频查询耗时：{}", uuid, System.currentTimeMillis() - pt);
            }
            if (isEmpty(replenishVideo) || replenishVideo.size() < replenishCount) {
                log.error("{} 推荐0.2 没有补全视频", uuid);
            } else {
                recommendVideo.addAll(replenishVideo);
            }
        }
        log.info("{} 推荐0.2 补足视频耗时：{}", uuid, System.currentTimeMillis() - pt);
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
}
