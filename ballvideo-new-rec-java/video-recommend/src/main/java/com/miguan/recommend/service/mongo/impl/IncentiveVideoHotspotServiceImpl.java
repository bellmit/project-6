package com.miguan.recommend.service.mongo.impl;

import com.miguan.recommend.common.constants.MongoConstants;
import com.miguan.recommend.entity.mongo.IncentiveVideoHotspot;
import com.miguan.recommend.service.mongo.IncentiveVideoHotspotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class IncentiveVideoHotspotServiceImpl implements IncentiveVideoHotspotService {

    @Resource(name = "logMongoTemplate")
    private MongoTemplate mongoTemplate;

    @Override
    @Cacheable(cacheNames = "incentive_video_hotspot", cacheManager = "getCacheManager")
    public List<IncentiveVideoHotspot> findFromMongoOrCache(Integer catid, Integer sensitive, List<String> excludeSource) {
        Query query = new Query();
        if (catid != null) {
            query.addCriteria(Criteria.where("catid").is(catid));
        }
        query.addCriteria(Criteria.where("collection_id").is(0));
        //query.addCriteria(Criteria.where("album_id").is(0));
        query.addCriteria(Criteria.where("state").is(1));
        if(sensitive != null && sensitive == 1){
            query.addCriteria(Criteria.where("sensitive").is(-2));
        }
        if(!CollectionUtils.isEmpty(excludeSource)){
            query.addCriteria(Criteria.where("videos_source").nin(excludeSource));
        }
        //query.addCriteria(Criteria.where("is_low_avg").is(0));
        query.with(Sort.by(Sort.Order.desc("weights")));
        //query.fields().include("video_id").include("catid").include("video_time").exclude("_id");
        log.debug("mongo激励视频查询语句>>{}", query.toString());
        return mongoTemplate.find(query, IncentiveVideoHotspot.class, MongoConstants.incentive_video_hotspot);
    }

    @Override
    @Cacheable(cacheNames = "incentive_video_hotspot", cacheManager = "getCacheManager")
    public List<IncentiveVideoHotspot> findFromMongoOrCache(List<Integer> excludeCatids, Integer sensitive) {
        Query query = new Query();
        if (!CollectionUtils.isEmpty(excludeCatids)) {
            query.addCriteria(Criteria.where("catid").nin(excludeCatids));
        }
        query.addCriteria(Criteria.where("collection_id").is(0));
        //query.addCriteria(Criteria.where("album_id").is(0));
        query.addCriteria(Criteria.where("state").is(1));
        if(sensitive != null && sensitive == 1){
            query.addCriteria(Criteria.where("sensitive").is(-2));
        }
        query.with(Sort.by(Sort.Order.desc("weights")));
        //query.fields().include("video_id").include("catid").include("video_time").exclude("_id");
        log.debug("mongo激励视频查询语句>>{}", query.toString());
        return mongoTemplate.find(query, IncentiveVideoHotspot.class, MongoConstants.incentive_video_hotspot);
    }
}
