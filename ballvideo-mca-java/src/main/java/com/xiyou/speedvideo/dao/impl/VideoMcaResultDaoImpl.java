package com.xiyou.speedvideo.dao.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.xiyou.speedvideo.dao.VideoMcaResultDao;
import com.xiyou.speedvideo.entity.mongo.VideoMcaResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * description:
 *
 * @author huangjx
 * @date 2020/11/5 5:58 下午
 */
@Component
public class VideoMcaResultDaoImpl implements VideoMcaResultDao {

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public VideoMcaResult saveVideoMcaResult(VideoMcaResult videoMcaResult) {
        return mongoTemplate.save(videoMcaResult);
    }

    @Override
    public VideoMcaResult findByVideoId(Integer videoId){
        Query query = new Query(Criteria.where("video_id").is(videoId));
        VideoMcaResult videoMcaResult = mongoTemplate.findOne(query, VideoMcaResult.class);
        return videoMcaResult;
    }

    @Override
    public long updateByVideoId(VideoMcaResult videoMcaResult) {

        Query query = new Query(Criteria.where("video_id").is(videoMcaResult.getVideoId()));

        Update update = new Update();
        update.set("results", videoMcaResult.getResults());
        update.set("label_list", videoMcaResult.getLabelList());
        update.set("scenario_Label", videoMcaResult.getScenarioLabel());
        update.set("priority_label", videoMcaResult.getPriorityLabel());

        UpdateResult result = mongoTemplate.updateFirst(query, update, VideoMcaResult.class);
        return result.getModifiedCount();
    }

    @Override
    public long removeByVideoId(Integer videoId) {
        Query query = new Query(Criteria.where("video_id").is(videoId));
        DeleteResult result = mongoTemplate.remove(query,"video_mca_result");
        return result.getDeletedCount();
    }
}
