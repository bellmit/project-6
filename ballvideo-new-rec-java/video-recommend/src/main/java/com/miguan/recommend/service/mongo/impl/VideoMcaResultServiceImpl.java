package com.miguan.recommend.service.mongo.impl;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.common.constants.MongoConstants;
import com.miguan.recommend.entity.mongo.VideoMcaResult;
import com.miguan.recommend.service.mongo.VideoMcaResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class VideoMcaResultServiceImpl implements VideoMcaResultService {

    @Resource(name = "featureMongoTemplate")
    private MongoTemplate featureMongoTemplate;

    @Override
    @Cacheable(cacheNames = "video_scenario", cacheManager = "getCacheManager")
    public String findSceneFromMongoOrCache(Integer videoId) {
        Query sceneQuery = new Query();
        sceneQuery.addCriteria(Criteria.where("video_id").is(videoId));
        sceneQuery.fields().include("video_id").include("priority_label").exclude("_id");
        VideoMcaResult videoMcaResult = featureMongoTemplate.findOne(sceneQuery, VideoMcaResult.class, MongoConstants.video_mca_result);
        log.debug("视频[{}]的场景查询结果>>{}", videoId, JSONObject.toJSONString(videoMcaResult));
        if(videoMcaResult == null){
            return null;
        }
        return videoMcaResult.getPriority_label();
    }
}
