package com.miguan.recommend.service.recommend.impl;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.common.constants.ExistConstants;
import com.miguan.recommend.common.constants.RedisRecommendConstants;
import com.miguan.recommend.entity.mongo.IncentiveVideoHotspot;
import com.miguan.recommend.service.mongo.IncentiveVideoHotspotService;
import com.miguan.recommend.service.recommend.IncentiveVideoHotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("incentiveVideoHotServiceV3New")
public class IncentiveVideoHotServiceV3NewImpl implements IncentiveVideoHotService<IncentiveVideoHotspot> {

    @Resource(name = "recDB0Pool")
    private JedisPool recDB0Pool;
    @Resource
    private IncentiveVideoHotspotService incentiveVideoHotspotService;

    @Override
    public List<IncentiveVideoHotspot> findAndFilter(String uuid, Integer sensitive, int getNum) {
        List<IncentiveVideoHotspot> jlvideo = this.findFromMongoAndFilter(uuid, null, null, sensitive, null, getNum, true);
        if (CollectionUtils.isEmpty(jlvideo) || jlvideo.size() < getNum) {
            jlvideo = findFromMongoAndFilter(uuid, null, null, sensitive, null, getNum, false);
            // 删除用户历史激励视频曝光记录
            //recommendDisruptorService.pushEvent(0, uuid, null);
        }
        if(CollectionUtils.isEmpty(jlvideo)){
            return null;
        }
        return jlvideo.subList(0, getNum);
    }

    @Override
    public List<IncentiveVideoHotspot> findAndFilter(String uuid, Integer catid, Integer sensitive, int getNum) {

        List<IncentiveVideoHotspot> jlvideo = findFromMongoAndFilter(uuid, catid, null, sensitive, null, getNum, true);
        if (CollectionUtils.isEmpty(jlvideo) || jlvideo.size() < getNum) {
            jlvideo = findFromMongoAndFilter(uuid, null, null, sensitive, null, getNum, false);
            // 删除用户历史激励视频曝光记录
            //recommendDisruptorService.pushEvent(0, uuid, null);
        }
        return jlvideo.subList(0, getNum);
    }

    @Override
    public List<IncentiveVideoHotspot> findAndFilter(String uuid, List<Integer> excludeCatids, Integer sensitive, int getNum) {
        List<IncentiveVideoHotspot> jlvideo = findFromMongoAndFilter(uuid, null, excludeCatids, sensitive, null, getNum, true);
        if (CollectionUtils.isEmpty(jlvideo) || jlvideo.size() < getNum) {
            jlvideo = findFromMongoAndFilter(uuid, null, excludeCatids, sensitive, null, getNum, false);
            // 删除用户历史激励视频曝光记录
            //recommendDisruptorService.pushEvent(0, uuid, null);
        }

        if (CollectionUtils.isEmpty(jlvideo)) {
            return null;
        }
        return jlvideo.subList(0, Math.min(getNum, jlvideo.size()));
    }

    @Override
    public List<IncentiveVideoHotspot> findAndFilter(String uuid, Integer catid, List<Integer> excludeCatids, Integer sensitive, List<String> excludeSource, int getNum) {
        List<IncentiveVideoHotspot> jlvideo = findFromMongoAndFilter(uuid, catid, excludeCatids, sensitive, excludeSource, getNum, true);
        if (CollectionUtils.isEmpty(jlvideo) || jlvideo.size() < getNum) {
            jlvideo = findFromMongoAndFilter(uuid, catid, null, sensitive, excludeSource, getNum, false);
            // 删除用户历史激励视频曝光记录
            //recommendDisruptorService.pushEvent(0, uuid, null);
        }
        if (CollectionUtils.isEmpty(jlvideo)) {
            return null;
        }
        return jlvideo.subList(0, Math.min(getNum, jlvideo.size()));
    }


    private List<IncentiveVideoHotspot> findFromMongoAndFilter(String uuid, Integer catid, List<Integer> excludeCatids, Integer sensitive, List<String> excludeSource, int getNum, boolean isHistory) {
        List<String> history = null;
        List<IncentiveVideoHotspot> queryList = null;
        if (catid == null) {
            queryList = incentiveVideoHotspotService.findFromMongoOrCache(excludeCatids, sensitive);
        } else {
            queryList = incentiveVideoHotspotService.findFromMongoOrCache(catid, sensitive, excludeSource);
        }

        // 是否需要重新曝光
        boolean bloomAgain = true;
        List<IncentiveVideoHotspot> resultList = new ArrayList<IncentiveVideoHotspot>(getNum);
        if (isHistory) {
            String key = String.format(RedisRecommendConstants.user_incentive_video_log, uuid);
            try (Jedis con = recDB0Pool.getResource()) {
                String value = con.get(key);
                if (!StringUtils.isEmpty(value)) {
                    history = JSONObject.parseArray(value, String.class);
                    boolean isBloom = localBloom(history, queryList, resultList, getNum);
                    if (isBloom) {
                        log.debug("{} 推荐 [{}]分类激励视频最新曝光 {} 个", uuid, catid, history.size());
                        con.setex(key, ExistConstants.one_day_seconds, JSONObject.toJSONString(history));
                        bloomAgain = false;
                    } else {
                        log.debug("{} 推荐 [{}]分类激励视频已全部曝光", uuid, catid);
                    }
                }
            }
        }

        if (bloomAgain) {
            history = new ArrayList<String>();
            localBloom(history, queryList, resultList, getNum);
            String key = String.format(RedisRecommendConstants.user_incentive_video_log, uuid);
            try (Jedis con = recDB0Pool.getResource()) {
                log.debug("{} 推荐 [{}]分类激励视频最新曝光 {} 个", uuid, catid, history.size());
                con.setex(key, ExistConstants.one_day_seconds, JSONObject.toJSONString(history));
            }
        }
        return resultList;
    }

    private boolean localBloom(List<String> history, List<IncentiveVideoHotspot> queryList, List<IncentiveVideoHotspot> resultList, int getNum) {
        int addNum = 0;
        for (IncentiveVideoHotspot vo : queryList) {
            if (history.contains(vo.getVideo_id())) {
                continue;
            }

            resultList.add(vo);
            history.add(vo.getVideo_id());
            ++addNum;
            if (addNum == getNum) {
                break;
            }
        }
        return addNum > 0;
    }
}
