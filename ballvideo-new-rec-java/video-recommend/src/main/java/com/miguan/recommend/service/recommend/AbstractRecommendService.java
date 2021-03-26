package com.miguan.recommend.service.recommend;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.miguan.recommend.bo.BaseDto;
import com.miguan.recommend.bo.VideoQueryDto;
import com.miguan.recommend.bo.VideoRecommendDto;
import com.miguan.recommend.common.constants.MongoConstants;
import com.miguan.recommend.common.constants.XyConstants;
import com.miguan.recommend.entity.mongo.CatHotspotVo;
import com.miguan.recommend.entity.mongo.IncentiveVideoHotspot;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.service.BloomFilterService;
import com.miguan.recommend.service.mongo.VideoScenairoSimilarService;
import com.miguan.recommend.service.xy.VideosCatService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
public class AbstractRecommendService {

    public final static List<String> excludeSource = Lists.newArrayList(new String[]{"98du"});

    public static ExecutorService executor = new ThreadPoolExecutor(200, 3000, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5000));

    /**
     * 初始化推荐参数
     *
     * @param recommendDto
     */
    public void initRecommendParam(BaseDto baseDto, VideoRecommendDto recommendDto, JedisPool recDB9Pool, MongoTemplate logMongTemplate, VideosCatService videosCatService) {
        String uuid = baseDto.getUuid();
        // 获取用户的兴趣分类池前3个
        List<Integer> userCat = getUserCats(recDB9Pool, uuid, recommendDto.getDefaultCatList());
        // 根据用户的兴趣分类池的第一个分类，获取相似的热度分类，并去除需要屏蔽的分类
        List<Integer> similarCat = getSimilarCat(userCat.get(0), recommendDto.getExcludeCatList(), baseDto.isABTest(), logMongTemplate, videosCatService);
        if (!isEmpty(similarCat)) {
            similarCat.removeAll(userCat);
        }

        if (!isEmpty(recommendDto.getExcludeCatList())) {
            userCat.removeAll(recommendDto.getExcludeCatList());
            similarCat.removeAll(recommendDto.getExcludeCatList());
        }

        // 用户的兴趣分类池去除需要屏蔽的分类
        // 如果用户兴趣分类池不足3个，从相似的热度分类中补足
//        int diffCount = 3 - userCat.size();
//        if (diffCount > 0 && !isEmpty(similarCat)) {
//            similarCat.removeAll(userCat);
//            if (similarCat.size() < diffCount) {
//                diffCount = similarCat.size();
//            }
//            for (int i = 0; i < diffCount; i++) {
//                userCat.add(similarCat.get(i));
//            }
//        }
        recommendDto.setUserCats(userCat);
        recommendDto.setSimilarCats(similarCat);
        log.info("{} 推荐 渠道[{}], 修改渠道[{}], 需要屏蔽的分类>>{}", uuid,
                baseDto.getPublicInfo().getChannel(),
                baseDto.getPublicInfo().getChangeChannel(),
                JSONObject.toJSONString(recommendDto.getExcludeCatList()));
        log.debug("{} 推荐 获取到的用户兴趣分类>>{}", uuid, JSONObject.toJSONString(userCat));
        log.debug("{} 推荐 获取到的用户兴趣分类的相似分类>>{}", uuid, JSONObject.toJSONString(similarCat));
    }

    /**
     * 获取用户的兴趣分类
     *
     * @param recDB9Pool    redis9库连接池
     * @param uuid          用户ID
     * @param defaultCatIds 渠道默认分类
     * @return
     */
    public List<Integer> getUserCats(JedisPool recDB9Pool, String uuid, List<Integer> defaultCatIds) {
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
        return Lists.newArrayList(defaultCatIds);
    }

    /**
     * 根据用户的第一兴趣分类，获取相似分类
     *
     * @param catid
     * @param excludeCatList
     * @return
     */
    public List<Integer> getSimilarCat(Integer catid, List<Integer> excludeCatList, boolean isABTest, MongoTemplate logMongTemplate, VideosCatService videosCatService) {
        //从mongo获取
        Query query = Query.query(Criteria.where("parent_catid").is(catid));
        if (!isEmpty(excludeCatList)) {
            query.addCriteria(Criteria.where("catid").nin(excludeCatList));
        }
        query.with(Sort.by(Sort.Order.asc("weights")));
        //query.fields().include("catid").exclude("_id");
        log.debug("相似的热度标签查询语句>>{}", query.toString());
        List<CatHotspotVo> list = logMongTemplate.find(query, CatHotspotVo.class, MongoConstants.cat_hotspot);
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
     * 根据用户的第一兴趣分类，获取相似分类
     *
     * @param catid
     * @param excludeCatList
     * @return
     */
    public List<Integer> getSimilarCatByCollectionName(Integer catid, List<Integer> excludeCatList, boolean isABTest, MongoTemplate logMongTemplate, String collectionName, VideosCatService videosCatService) {
        //从mongo获取
        Query query = Query.query(Criteria.where("parent_catid").is(catid));
        if (!isEmpty(excludeCatList)) {
            query.addCriteria(Criteria.where("catid").nin(excludeCatList));
        }
        query.with(Sort.by(Sort.Order.asc("weights")));
        //query.fields().include("catid").exclude("_id");
        log.debug("相似的热度标签查询语句>>{}", query.toString());
        List<CatHotspotVo> list = logMongTemplate.find(query, CatHotspotVo.class, collectionName);
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
     * 获取热度视频
     * @param baseDto
     * @param recommendDto
     * @param userCatVideo 查询结果集合
     * @param videoHotService
     * @return
     */
    public CompletableFuture<Void> getHotVideo(BaseDto baseDto, VideoRecommendDto recommendDto, Map<Integer, Integer> catNumMap, Map<Integer, List<VideoHotspotVo>> userCatVideo, VideoHotService videoHotService){
        Function<Integer[], Number> f = e -> {
            long pt = System.currentTimeMillis();
            VideoQueryDto<VideoHotspotVo> queryDto = new VideoQueryDto<VideoHotspotVo>(baseDto, e[0], recommendDto.getSensitiveState(), e[1]);
            if (baseDto.isVideo98Group()) {
                queryDto.setExcludedSource(excludeSource);
            }
            List<VideoHotspotVo> videoId1 = videoHotService.findAndFilter(queryDto, null);
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐 获取用户兴趣分类[{}]视频：{} 个", baseDto.getUuid(), e[0], isEmpty(videoId1) ? 0 : videoId1.size());
            }
            if (!isEmpty(videoId1)) {
                userCatVideo.put(e[0], videoId1);
            }
            if (log.isInfoEnabled()) {
                log.info("{} 推荐 获取用户兴趣分类视频时长：{}", baseDto.getUuid(), (System.currentTimeMillis() - pt));
            }
            return System.currentTimeMillis() - pt;
        };

        CompletableFuture[] listFeture = catNumMap.entrySet().stream().map(e -> {
            Integer[] params = new Integer[]{e.getKey(), e.getValue()};
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
    public CompletableFuture<Integer> getIncetiveHotVideo(BaseDto baseDto, VideoRecommendDto recommendDto, List<IncentiveVideoHotspot> jlVideo, IncentiveVideoHotService incentiveVideoHotService, ExecutorService executor) {
        String uuid = baseDto.getUuid();
        return CompletableFuture.supplyAsync(() -> {
            //lzhong 根据热度标签获取第4规则的1个视频(激励视频)
            long pt = System.currentTimeMillis();
            //int jlCatid = function.apply(recommendDto.getUserCats());
            List<IncentiveVideoHotspot> findList = incentiveVideoHotService.findAndFilter(uuid, recommendDto.getExcludeCatList(), recommendDto.getSensitiveState(), recommendDto.getIncentiveVideoNum());
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

    /**
     * 根据用户完播视频池查询相似视频
     * @param similarAndSceneVideos
     * @param uuid
     * @param videoPoolList
     * @param getNum
     * @param videoScenairoSimilarService
     * @param executor
     * @return
     */
    public CompletableFuture<Void> findSimilarVideoFromUserVideoPool(List<String> similarAndSceneVideos, String uuid, List<String> videoPoolList, int getNum, VideoScenairoSimilarService videoScenairoSimilarService, ExecutorService executor) {
        if (isEmpty(videoPoolList)) {
            return null;
        }

        Function<String, Long> f = e -> {
            long start = System.currentTimeMillis();
            List<String> similarVideos = videoScenairoSimilarService.findAndFilter(uuid, Integer.parseInt(e), getNum);
            if (isEmpty(similarVideos)) {
                log.debug("{} 推荐0.5 视频[{}]的相似视频查询到{}个", uuid, e, 0);
            } else {
                log.debug("{} 推荐0.5 视频[{}]的相似视频查询到{}个", uuid, e, similarVideos.size());
                similarAndSceneVideos.addAll(similarVideos);
            }
            long time = System.currentTimeMillis() - start;
            log.debug("{} 推荐0.5 视频[{}]的相似视频查询耗时:{}", uuid, e, time);
            return time;
        };

        CompletableFuture[] listFeture = videoPoolList.stream().map(e -> {
            return CompletableFuture.completedFuture(e).thenApplyAsync(f, executor);
        }).toArray(size -> new CompletableFuture[size]);
        return CompletableFuture.allOf(listFeture);
    }

    /**
     * 过滤曝光视频
     * @param sortedVideoMap
     * @param uuid
     * @param bloomFilterService
     * @return
     */
    public Map<String, BigDecimal> filterSortedMap(Map<String, BigDecimal> sortedVideoMap, String uuid, BloomFilterService bloomFilterService) {
        List<String> vidList = Lists.newArrayList(sortedVideoMap.keySet());
        List<String> filteredVidList = bloomFilterService.containMuil(vidList.size(), uuid, vidList);
        Map<String, BigDecimal> filteredMap = new LinkedHashMap<>();
        filteredVidList.stream().forEach(vid -> filteredMap.put(vid, sortedVideoMap.get(vid)));
        return filteredMap;
    }

    /**
     * 将返回的热度视频和激励视频放入bloom过滤器
     *
     * @param uuid
     * @param userCatVideo
     * @param incentiveVideo
     */
    public void bloomVideo(String uuid, List<String> userCatVideo, List<String> incentiveVideo, BloomFilterService bloomFilterService, ExecutorService executor) {
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
