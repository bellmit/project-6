package com.miguan.recommend.service.recommend.impl;

import com.alibaba.fastjson.JSON;
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

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service("offLineVideoServiceV2")
public class OffLineVideoServiceV2Impl implements OffLineVideoService<String> {

    @Resource(name = "recDB9Pool")
    private JedisPool recDB9Pool;
    @Resource(name = "logMongoTemplate")
    private MongoTemplate mongoTemplate;
    @Autowired
    private BloomFilterService bloomFilterService;

    @Override
    public List<String> find(String uuid, int getNum, List<Integer> excludeCatIds) {
        List<String> videoIds = null;
        try (Jedis con = recDB9Pool.getResource()) {
            String key = RedisOfflineConstants.keyPrix.concat(uuid);

            //添加key转换获取
            //这个存储依赖于埋点上报收集应用里的缓存
            String uuidint = con.get(key);
            if (uuidint == null) {
                log.warn("{} 推荐 从redis未找到uuid的uuidint", uuid);
                return new ArrayList<String>();
            }

            key = RedisOfflineConstants.keyPrifixed.concat(uuidint);
            String videoIdsStr = con.get(key);
            if (log.isInfoEnabled()) {
                log.info("{} 推荐 离线视频：key={} value={}", uuid, key, videoIdsStr);
            }
            if (StringUtils.isNotBlank(videoIdsStr)) {
                videoIds = Lists.newArrayList(videoIdsStr.split(","));
            }
        }
        if (isEmpty(videoIds)) {
            return new ArrayList<String>();
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
            return new ArrayList<String>();
        }
        //通过布隆过滤器过滤，限制条数得到想要的条数结束遍历
        int index = 1;
        List<String> ouputList = Lists.newArrayList();
        List<VideoHotspotVo> filterTmplist = Lists.newArrayList();
        List<String> tmpVideoIdList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            VideoHotspotVo tmpVo = list.get(i);
            //过滤屏蔽的分类
            if (!isEmpty(excludeCatIds)) {
                Integer catid = tmpVo.getCatid();
                if (excludeCatIds.contains(catid)) {
                    continue;
                }
            }
            filterTmplist.add(tmpVo);
            tmpVideoIdList.add(tmpVo.getVideo_id());
        }

        // 曝光过滤
        long pt = System.currentTimeMillis();
        List<String> getTmp = bloomFilterService.containMuil(getNum, uuid, tmpVideoIdList);
        log.info("{}   bloom耗时长{}：{}", uuid, index, System.currentTimeMillis() - pt);

        if (!isEmpty(getTmp)) {
            for (VideoHotspotVo vo : filterTmplist) {
                if (getTmp.contains(vo.getVideo_id())) {
                    ouputList.add(vo.getVideo_id());
                }
            }
        }
        filterTmplist.clear();

        if (log.isInfoEnabled()) {
            log.info("{} 推荐 获取离线推荐视频过滤后数据：{}", uuid, JSON.toJSONString(ouputList));
        }
        list.clear();
        return ouputList;

    }
}
