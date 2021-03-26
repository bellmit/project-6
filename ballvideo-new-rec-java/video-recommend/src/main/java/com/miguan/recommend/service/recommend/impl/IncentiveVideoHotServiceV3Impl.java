package com.miguan.recommend.service.recommend.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.miguan.recommend.common.constants.MongoConstants;
import com.miguan.recommend.entity.mongo.IncentiveVideoHotspot;
import com.miguan.recommend.entity.mongo.UserIncentiveLog;
import com.miguan.recommend.service.recommend.IncentiveVideoHotService;
import com.miguan.recommend.service.recommend.RecommendDisruptorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service("incentiveVideoHotServiceV3")
public class IncentiveVideoHotServiceV3Impl implements IncentiveVideoHotService<IncentiveVideoHotspot> {

    @Resource(name = "recDB9Pool")
    private JedisPool recDB9Pool;
    @Resource
    private MongoTemplate mongoTemplate;
    @Autowired
    private RecommendDisruptorService recommendDisruptorService;


    @Override
    public List<IncentiveVideoHotspot> findAndFilter(String uuid, Integer sensitive, int getNum) {
        return null;
    }

    @Override
    public List<IncentiveVideoHotspot> findAndFilter(String uuid, Integer catid, Integer sensitive, int getNum) {

        List<IncentiveVideoHotspot> jlvideo = findFromMongoAndFilter(uuid, catid, sensitive, getNum, true);
        if (CollectionUtils.isEmpty(jlvideo) || jlvideo.size() < getNum) {
            jlvideo = findFromMongoAndFilter(uuid, null, sensitive, getNum, false);
            // 删除用户历史激励视频曝光记录
            recommendDisruptorService.pushEvent(0, uuid, null);
        }
        return jlvideo.subList(0, getNum);
    }

    @Override
    public List<IncentiveVideoHotspot> findAndFilter(String uuid, List<Integer> excludeCatids, Integer sensitive, int getNum) {
        return null;
    }

    @Override
    public List<IncentiveVideoHotspot> findAndFilter(String uuid, Integer catid, List<Integer> excludeCatids, Integer sensitive, List<String> excludeSource, int getNum) {
        return null;
    }

    private List<IncentiveVideoHotspot> findFromMongoAndFilter(String uuid, Integer catid, Integer sensitive, int getNum, boolean isHistory) {

        Query query = new Query();
        if (catid != null) {
            query.addCriteria(Criteria.where("catid").is(catid));
        }
        if (isHistory) {
            List<String> history = this.findHistory(uuid);
            if (!CollectionUtils.isEmpty(history)) {
                query.addCriteria(new Criteria().and("video").nin(history));
            }
        }
        query.addCriteria(Criteria.where("collection_id").is(0));
        //query.addCriteria(Criteria.where("album_id").is(0));
        query.addCriteria(Criteria.where("state").is(1));
        if (sensitive != null && sensitive == 1) {
            query.addCriteria(Criteria.where("sensitive").is(-2));
        }
        query.with(Sort.by(Sort.Order.desc("weights")));
        query.limit(getNum + 20);
        //query.fields().include("video_id").include("catid").include("video_time").exclude("_id");
        long mongoStart = System.currentTimeMillis();
        List<IncentiveVideoHotspot> videoIdsObj = mongoTemplate.find(query, IncentiveVideoHotspot.class, MongoConstants.incentive_video_hotspot);
        log.info("mongo查询激励视频耗时: {}", System.currentTimeMillis() - mongoStart);
        if (isEmpty(videoIdsObj)) {
            return null;
        }

        if (isHistory) {
            List<IncentiveVideoHotspot> rdata = Lists.newArrayList();
            String[] keys = videoIdsObj.stream().map(IncentiveVideoHotspot::getVideo_id).toArray(String[]::new);
            try (Jedis con = recDB9Pool.getResource()) {
                List<String> ktmp = con.mget(keys);
                for (int i = 0; i < keys.length; i++) {
                    String k = ktmp.get(i);
                    //存在缓存中则过滤
                    if (k != null) {
                        continue;
                    }
                    rdata.add(videoIdsObj.get(i));
                    if (getNum <= rdata.size()) {
                        break;
                    }
                }
            }
            videoIdsObj = null;
            return rdata;
        }

        List<IncentiveVideoHotspot> list = new ArrayList<>(getNum);
        for (int i = 0; i < getNum; i++) {
            list.add(videoIdsObj.get(i));
        }
        return list;
    }

    private List<String> findHistory(String uuid) {
        Query query = new Query();
        query.addCriteria(Criteria.where("distinct_id").is(uuid));
        query.fields().include("video_id").exclude("_id");
        //查找用户的历史记录
        List<UserIncentiveLog> history = mongoTemplate.find(query, UserIncentiveLog.class, "user_incentive_log");
        if (log.isDebugEnabled()) {
            log.debug("推荐 激励查找到uuid({})历史记录：{}", uuid, history == null ? "" : JSON.toJSONString(history));
        }
        if (isEmpty(history)) {
            return null;
        }
        return history.stream().map(UserIncentiveLog::getVideo_id).collect(Collectors.toList());
    }
}
