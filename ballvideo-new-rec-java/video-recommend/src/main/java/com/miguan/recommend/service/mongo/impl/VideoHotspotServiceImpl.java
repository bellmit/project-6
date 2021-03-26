package com.miguan.recommend.service.mongo.impl;

import com.miguan.recommend.common.constants.MongoConstants;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.service.mongo.VideoHotspotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service
public class VideoHotspotServiceImpl implements VideoHotspotService {

    @Resource(name = "logMongoTemplate")
    private MongoTemplate mongoTemplate;

    @Override
    @Cacheable(cacheNames = "video_hotspot", cacheManager = "getCacheManager")
    public List<VideoHotspotVo> findFromMongoOrCache(Integer catId, Integer sensitive, List<String> excludeSource, int skipNum, int size) {
        Query query = new Query();
        if (catId != null) {
            if (catId == -1) {
                query.addCriteria(Criteria.where("is_score").is(1));
            } else {
                query.addCriteria(Criteria.where("catid").is(catId.intValue()));
            }
        }
//        if (queryDto.getCatid() == null && !isEmpty(queryDto.getIncludeCatid())) {
//            query.addCriteria(Criteria.where("catid").in(queryDto.getIncludeCatid()));
//        }
        //按运营要求，推荐里不出现合集视频
        query.addCriteria(Criteria.where("collection_id").is(0));
        //query.addCriteria(Criteria.where("album_id").is(0));
        query.addCriteria(Criteria.where("state").is(1));
        if (sensitive != null && sensitive == 1) {
            query.addCriteria(Criteria.where("sensitive").is(-2));
        }
        if(!isEmpty(excludeSource)){
            query.addCriteria(Criteria.where("videos_source").nin(excludeSource));
        }
//        if(queryDto.getIsLowAvg() != null){
//            query.addCriteria(Criteria.where("is_low_avg").is(queryDto.getIsLowAvg().intValue()));
//        }
//        if (queryDto.getCatid() != null && queryDto.getCatid() == -1) {
//            query.with(Sort.by(Sort.Order.desc("score")));
//        } else {
            query.with(Sort.by(Sort.Order.desc("weights")));
            //query.fields().exclude("score");
//        }
        query.skip(skipNum);
        query.limit(size);
        //query.fields().include("video_id").include("catid").include("video_time").exclude("_id");
        //从mongo中获取视频
        log.debug("mongo热度视频查询语句>>{}", query.toString());
        return mongoTemplate.find(query, VideoHotspotVo.class, MongoConstants.video_hotspot);
    }

    /**
     * 从Mongo或本地缓存里查询
     *
     * @param includeCatid 允许的分类ID列表
     * @param catid        指定的分类ID
     * @param size         获取个数
     * @param skipNum      跳过个数
     * @return
     */
    @Override
    @Cacheable(cacheNames = "video_hotspot", cacheManager = "getCacheManager")
    public List<VideoHotspotVo> findFromMongoOrCache(List<Integer> includeCatid, Integer catid, Integer sensitive, int size, int skipNum) {
        Query query = new Query();
        if (catid != null) {
            if (catid == -1) {
                query.addCriteria(Criteria.where("is_score").is(1));
            } else {
                query.addCriteria(Criteria.where("catid").is(catid.intValue()));
            }
        }
        if (catid == null && !isEmpty(includeCatid)) {
            query.addCriteria(Criteria.where("catid").in(includeCatid));
        }
        //按运营要求，推荐里不出现合集视频
        query.addCriteria(Criteria.where("collection_id").is(0));
        //query.addCriteria(Criteria.where("album_id").is(0));
        query.addCriteria(Criteria.where("state").is(1));
        if (sensitive != null && sensitive == 1) {
            query.addCriteria(Criteria.where("sensitive").is(-2));
        }
//        query.addCriteria(Criteria.where("videos_source").ne("yiyouliao"));
        query.addCriteria(Criteria.where("is_low_avg").is(0));
        if (catid != null && catid == -1) {
            query.with(Sort.by(Sort.Order.desc("score")));
        } else {
            query.with(Sort.by(Sort.Order.desc("weights")));
            //query.fields().exclude("score");
        }
        query.skip(skipNum);
        query.limit(size);
        //query.fields().include("video_id").include("catid").include("video_time").exclude("_id");
        //从mongo中获取视频
        log.debug("mongo热度视频查询语句>>{}", query.toString());
        return mongoTemplate.find(query, VideoHotspotVo.class, MongoConstants.video_hotspot);
    }

    /**
     * 通过视频ID，从Mongo获取
     *
     * @param videoIds
     * @return
     */
    @Override
    public List<VideoHotspotVo> findFromMongoById(List<String> videoIds, Integer sensitive) {
        Query query = new Query();
        query.addCriteria(Criteria.where("video_id").in(videoIds));
        //按运营要求，推荐里不出现合集视频
        query.addCriteria(Criteria.where("collection_id").is(0));
        //query.addCriteria(Criteria.where("album_id").is(0));
        query.addCriteria(Criteria.where("state").is(1));
        if (sensitive != null && sensitive == 1) {
            query.addCriteria(Criteria.where("sensitive").is(-2));
        }
        log.debug("mongo热度视频查询语句>>{}", query.toString());
        return mongoTemplate.find(query, VideoHotspotVo.class, MongoConstants.video_hotspot);
    }

    @Override
    @Cacheable(cacheNames = "select_videos", cacheManager = "getCacheManager")
    public List<VideoHotspotVo> findFromMongoById(List<String> videoIds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("video_id").in(videoIds));
        query.addCriteria(Criteria.where("collection_id").is(0));
        query.addCriteria(Criteria.where("sensitive").is(-2));
        query.addCriteria(Criteria.where("state").is(1));
        return mongoTemplate.find(query, VideoHotspotVo.class, MongoConstants.video_hotspot);
    }

    @Override
    @Cacheable(cacheNames = "select_videos", cacheManager = "getCacheManager")
    public List<VideoHotspotVo> findFromMongoByOnlineDateDesc(int skipNum, int size) {
        Query query = new Query();
        query.addCriteria(Criteria.where("sensitive").is(-2));
        query.with(Sort.by(Sort.Order.desc("online_time")));
        query.skip(skipNum);
        query.limit(size);
        return mongoTemplate.find(query, VideoHotspotVo.class, MongoConstants.video_hotspot);
    }
}
