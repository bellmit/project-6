package com.miguan.recommend.service.recommend.normativeimpl;

import com.google.common.collect.Lists;
import com.miguan.recommend.common.constants.MongoConstants;
import com.miguan.recommend.common.constants.RedisUserConstant;
import com.miguan.recommend.common.constants.SymbolConstants;
import com.miguan.recommend.common.constants.XyConstants;
import com.miguan.recommend.entity.mongo.CatHotspotVo;
import com.miguan.recommend.service.BloomFilterService;
import com.miguan.recommend.service.RedisService;
import com.miguan.recommend.service.xy.VideosCatService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Component
public class NormativeRecommendService {

    private static ExecutorService executor =
            new ThreadPoolExecutor(200, 2000, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5000));
    @Resource(name = "redisDB11Service")
    private RedisService redisDB11Service;
    @Resource
    private VideosCatService videosCatService;
    @Resource(name = "logMongoTemplate")
    private MongoTemplate logMongoTemplate;
    @Resource
    private BloomFilterService bloomFilterService;

    /**
     * 获取默认分类
     *
     * @return
     */
    public List<Integer> getDefaultCat() {
        // 直接返回分类表ID
//        List<String> catIds = videosCatService.getCatIdsByStateAndType(XyConstants.open, XyConstants.FIRST_VIDEO_CODE);
        Query query = Query.query(Criteria.where("weights").is(1));
        log.debug("相似的热度标签查询语句>>{}", query.toString());
        List<CatHotspotVo> catIds = logMongoTemplate.find(query, CatHotspotVo.class, MongoConstants.cat_hotspot);
        return catIds.stream().distinct().map(CatHotspotVo::getCatid).collect(Collectors.toList());
    }

    /**
     * 获取用户实时兴趣分类池
     *
     * @param userTag 用户标识
     * @return
     */
    public List<Integer> getUserCat(String userTag) {
        // 查询用户实时兴趣分类，返回前3个分类
        String redisKey = RedisUserConstant.n_user_cat_pool + userTag;
        String value = redisDB11Service.get(redisKey);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return Lists.newArrayList(value.split(SymbolConstants.comma)).stream().map(Integer::new).collect(Collectors.toList());
    }

    /**
     * 获取目标分类的相似分类
     *
     * @param parentCat 目标分类
     * @return
     */
    public List<Integer> getSimilarCat(Integer parentCat) {
        Query query = Query.query(Criteria.where("parent_catid").is(parentCat));
        query.with(Sort.by(Sort.Order.asc("weights")));
        log.debug("相似的热度标签查询语句>>{}", query.toString());
        List<CatHotspotVo> list = logMongoTemplate.find(query, CatHotspotVo.class, MongoConstants.cat_hotspot);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.stream().map(CatHotspotVo::getCatid).collect(Collectors.toList());
    }

    /**
     * 对缓存的预估视频进行Bloom过滤
     *
     * @param sortedVideoMap 缓存的视频
     * @param userTag        用户标识
     * @return
     */
    public Map<String, BigDecimal> bloomSortedMap(Map<String, BigDecimal> sortedVideoMap, String userTag) {
        List<String> vidList = Lists.newArrayList(sortedVideoMap.keySet());
        List<String> filteredVidList = bloomFilterService.containMuil(vidList.size(), userTag, vidList);
        Map<String, BigDecimal> filteredMap = new LinkedHashMap<>();
        filteredVidList.stream().forEach(vid -> filteredMap.put(vid, sortedVideoMap.get(vid)));
        return filteredMap;
    }

    public void cacheVideo(String uuid, List<String> userCatVideo, List<String> incentiveVideo) {
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
