package com.miguan.recommend.service.mongo.impl;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.common.constants.MongoConstants;
import com.miguan.recommend.entity.mongo.ScenarioVideo;
import com.miguan.recommend.service.mongo.ScenairoVideoService;
import com.miguan.recommend.service.mongo.VideoMcaResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class ScenairoVideoServiceImpl implements ScenairoVideoService {

    @Resource(name = "featureMongoTemplate")
    private MongoTemplate featureMongoTemplate;
    @Resource
    private VideoMcaResultService videoMcaResultService;

    @Override
    @Cacheable(cacheNames = "scenario_num", cacheManager = "getCacheManager")
    public Integer findScenarioNumIdFromMongoOrCache(Integer videoId) {
        String scenario = videoMcaResultService.findSceneFromMongoOrCache(videoId);
        if(scenario == null){
            return null;
        }
        Query sceneNumQuery = new Query();
        sceneNumQuery.addCriteria(Criteria.where("scenario").is(scenario));
        ScenarioVideo sceneNum = featureMongoTemplate.findOne(sceneNumQuery, ScenarioVideo.class, MongoConstants.scenario_video);
        if (sceneNum == null) {
            return null;
        }
        log.debug("场景[{}]编号查询结果>>{}", scenario, sceneNum.getNum_id());
        return sceneNum.getNum_id();
    }

    @Override
    @Cacheable(cacheNames = "scenario_num", cacheManager = "getCacheManager")
    public List<String> findVideoFromMongoOrCache(Integer sceneNum) {
        Query sceneNumQuery = new Query();
        sceneNumQuery.addCriteria(Criteria.where("num_id").is(sceneNum));
        ScenarioVideo scenarioVideo = featureMongoTemplate.findOne(sceneNumQuery, ScenarioVideo.class, MongoConstants.scenario_video);
        if (scenarioVideo == null) {
            return null;
        }
        log.debug("场景[{}]视频查询结果>>{}", sceneNum, JSONObject.toJSONString(scenarioVideo.getVideo_ids()));
        return scenarioVideo.getVideo_ids();
    }
}
