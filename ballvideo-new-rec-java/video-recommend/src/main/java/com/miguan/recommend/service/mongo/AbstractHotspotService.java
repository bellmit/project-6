package com.miguan.recommend.service.mongo;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public abstract class AbstractHotspotService {

    public void remove(MongoTemplate mongoTemplate, String collectionName, String videoId){
        Query query = new Query();
        query.addCriteria(Criteria.where("video_id").is(videoId));
        mongoTemplate.findAllAndRemove(query, collectionName);
    }

    public void updateStateByVideoId(MongoTemplate mongoTemplate, String collectionName, String videoId, Integer state){
        Query query = new Query();
        query.addCriteria(Criteria.where("video_id").is(videoId));
        Update update = new Update();
        update.set("state", state);
        mongoTemplate.updateMulti(query, update, collectionName);
    }

    public void updateAblumByVideoId(MongoTemplate mongoTemplate, String collectionName, String videoId, Integer ablum_id){
        Query query = new Query();
        query.addCriteria(Criteria.where("video_id").is(videoId));
        Update update = new Update();
        update.set("ablum_id", ablum_id);
        mongoTemplate.updateMulti(query, update, collectionName);
    }

    public void updateScoreByVideoId(MongoTemplate mongoTemplate, String collectionName, String videoId, Integer is_score, Double score){
        Query query = new Query();
        query.addCriteria(Criteria.where("video_id").is(videoId));
        Update update = new Update();
        update.set("is_score", is_score);
        update.set("score", score);
        mongoTemplate.updateMulti(query, update, collectionName);
    }
}
