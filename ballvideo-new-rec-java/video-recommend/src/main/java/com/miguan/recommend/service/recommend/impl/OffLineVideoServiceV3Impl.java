package com.miguan.recommend.service.recommend.impl;

import com.google.common.collect.Lists;
import com.miguan.recommend.common.constants.MongoConstants;
import com.miguan.recommend.common.constants.RedisOfflineConstants;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.service.BloomFilterService;
import com.miguan.recommend.service.recommend.OffLineVideoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service("offLineVideoServiceV3")
public class OffLineVideoServiceV3Impl implements OffLineVideoService<VideoHotspotVo> {

    @Resource(name = "recDB9Pool")
    private JedisPool recDB9Pool;
    @Resource(name = "logMongoTemplate")
    private MongoTemplate mongoTemplate;
    @Autowired
    private BloomFilterService bloomFilterService;

    @Override
    public List<VideoHotspotVo> find(String uuid, int getNum, List<Integer> excludeCatIds) {
        List<String> videoIds = null;
        try (Jedis con = recDB9Pool.getResource()) {
            String key = RedisOfflineConstants.keyPrix.concat(uuid);

            //添加key转换获取
            //这个存储依赖于埋点上报收集应用里的缓存
            String uuidint = con.get(key);
            if (uuidint == null) {
                log.warn("{} 推荐 从redis未找到uuid的uuidint", uuid);
                return new ArrayList<VideoHotspotVo>();
            }

            key = RedisOfflineConstants.keyPrifixed1.concat(uuidint);
            String videoIdsStr = con.get(key);
            if (log.isInfoEnabled()) {
                log.info("{} 推荐 离线视频：key={} value={}", uuid, key, videoIdsStr);
            }
            if (StringUtils.isNotBlank(videoIdsStr)) {
                videoIds = Lists.newArrayList(videoIdsStr.split(","));
            }
        }
        if (isEmpty(videoIds)) {
            return new ArrayList<VideoHotspotVo>();
        }
        //为什么要用这个的操作，是为了筛选屏蔽分类标签等
        Query query = new Query(Criteria.where("video_id").in(videoIds)
                .and("collection_id").is(0).and("state").is(1));
        query.with(Sort.by(Sort.Order.desc("weights")));
        //query.fields().include("video_id").include("catid").include("video_time").exclude("_id");
        //从mongo中获取视频
        List<VideoHotspotVo> list = mongoTemplate.find(query, VideoHotspotVo.class, MongoConstants.video_hotspot);
        if (log.isInfoEnabled()) {
            log.info("{} 推荐 获取离线推荐视频mongodb筛选：{}", uuid, videoIds);
        }
        if (isEmpty(list)) {
            return new ArrayList<VideoHotspotVo>();
        }

        // 曝光过滤
        List<String> listIds = list.stream().map(VideoHotspotVo::getVideo_id).collect(Collectors.toList());
        List<String> getTmp = bloomFilterService.containMuil(getNum, uuid, listIds);
        List<VideoHotspotVo> ouputList = Lists.newArrayList();
        list.stream().forEach(e -> {
            if (getTmp.contains(e.getVideo_id()) && !excludeCatIds.contains(e.getCatid())) {
                ouputList.add(e);
            }
        });
        return ouputList;

    }
}
