package com.miguan.recommend.service.mongo.impl;

import com.miguan.recommend.common.constants.ExistConstants;
import com.miguan.recommend.common.constants.RedisRecommendConstants;
import com.miguan.recommend.common.constants.SymbolConstants;
import com.miguan.recommend.entity.mongo.VideoScenarioSimilar;
import com.miguan.recommend.service.BloomFilterService;
import com.miguan.recommend.service.RedisService;
import com.miguan.recommend.service.mongo.VideoScenairoSimilarService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VideoScenairoSimilarServiceImpl implements VideoScenairoSimilarService {

    @Resource(name = "redisDB0Service")
    private RedisService redisDB0Service;
    @Resource(name = "featureMongoTemplate")
    private MongoTemplate featureMongoTemplate;
    @Resource
    private BloomFilterService bloomFilterService;

    @Override
    //@Cacheable(cacheNames = "video_scenario_similar", cacheManager = "getCacheManager")
    public List<String> findFromMongoOrCache(Integer videoId) {

        List<String> similarList = null;
        String key = String.format(RedisRecommendConstants.video_scenario_similar, videoId);
        String cacheValue = redisDB0Service.get(key);
        if (StringUtils.isEmpty(cacheValue)) {
            Query query = new Query();
            query.addCriteria(Criteria.where("video_id").is(videoId));
            query.fields().include("sim_id_list");
            VideoScenarioSimilar videoScenarioSimilar = featureMongoTemplate.findOne(query, VideoScenarioSimilar.class, "video_scenario_similar");
            if (videoScenarioSimilar != null) {
                similarList = videoScenarioSimilar.getSim_id_list().stream().map(String::valueOf).collect(Collectors.toList());
                redisDB0Service.set(key, StringUtils.collectionToDelimitedString(similarList, SymbolConstants.comma), ExistConstants.one_day_seconds);
            }
        } else {
            similarList = Stream.of(cacheValue.split(SymbolConstants.comma)).collect(Collectors.toList());
        }
        return similarList;
    }

    @Override
    public List<String> findAndFilter(String uuid, Integer videoId, int getNum) {
        List<String> similarList = this.findFromMongoOrCache(videoId);
        if (CollectionUtils.isEmpty(similarList)) {
            return null;
        }
        return bloomFilterService.containMuilSplit(getNum, uuid, similarList);
    }

}
