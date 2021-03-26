package com.miguan.ballvideo.service.recommend;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.miguan.ballvideo.vo.mongodb.IncentiveVideoHotspot;
import com.miguan.ballvideo.vo.mongodb.UserIncentiveLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
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
import java.util.stream.Stream;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**激励视频推荐
 */
@Service
@Slf4j
public class FindRecommendJLVideosServiceImpl {

    @Resource(name = "logMongoTemplate")
    private MongoTemplate mongoTemplate;
    @Resource(name = "recDB9Pool")
    private JedisPool recDB9Pool;
    @Autowired
    private RecommendDisruptorService recommendDisruptorService;
    private final String KEY_PREFIX = "recj:";

    /**
     *
     * @param uuid
     * @param catid
     * @param count
     * @param excludeCatids
     * @param excludeCollectids
     * @return
     */
    public List<IncentiveVideoHotspot> getVideoId(String uuid, int catid, int count, List<Integer> excludeCatids, List<Integer> excludeCollectids, boolean isABTest) {
        //新通过分类获取激励视频
        long pt = System.currentTimeMillis();

//        List<IncentiveVideoHotspot> jlvideo = this.getVideoId(uuid, catid, count, excludeCollectids, isABTest);
        List<IncentiveVideoHotspot> jlvideo = findAndFilterVideo(uuid, count, catid, excludeCatids, excludeCollectids, true, isABTest);
        log.warn("推荐0.1规则4-1时长：{}", (System.currentTimeMillis() - pt));
        //如果没有获取到激励视频则将用户点击历史记录给删除再获取
        if (isEmpty(jlvideo)) {
            log.warn("推荐0.1设备标识(dintinctId={})没有补全激励视频", uuid);
            log.warn("推荐0.1规则4-2时长：{}", (System.currentTimeMillis() - pt));
            //重新获取激励视频
            jlvideo = findAndFilterVideo(uuid, count, null, excludeCatids, excludeCollectids, false, isABTest);
            //删除用户历史记录
            recommendDisruptorService.pushEvent(0, uuid, null);
        }
        return jlvideo;
    }

    /**
     * 根据条件获取
     * @param uuid
     * @param catid
     * @param count
     * @param excludeCollectids
     * @return
     */
    private List<IncentiveVideoHotspot> getVideoId(String uuid, int catid, int count, List<Integer> excludeCollectids, boolean isABTest) {
        return findAndFilterVideo(uuid, count, catid, null, excludeCollectids, true, isABTest);
    }

    /**
     *
     * @param uuid 设备标识，通过此设备标识进行过滤用户历史记录
     * @param getSize 此次要获取的视频目标数量
     * @param catid  筛选此分类的视频集
     * @param excludeCatids  屏蔽分类
     * @param excludeCollectids 屏蔽用户合集
     * @param isHistory 是否过滤用户的历史记录
     * @return
     */
    private List<IncentiveVideoHotspot> findAndFilterVideo(String uuid, int getSize, Integer catid, List<Integer> excludeCatids, List<Integer> excludeCollectids, boolean isHistory, boolean isABTest) {
        List<UserIncentiveLog> history = null;
        //是否过滤用户历史记录
        if (isHistory) {
            Query query = new Query();
            query.addCriteria(Criteria.where("distinct_id").is(uuid));
            query.fields().include("video_id").exclude("_id");
            //查找用户的历史记录
            history = mongoTemplate.find(query, UserIncentiveLog.class, "user_incentive_log");
            if (log.isDebugEnabled()) {
                log.debug("推荐0.1激励查找到uuid({})历史记录：{}", uuid, history == null ? "" : JSON.toJSONString(history));
            }
        }
        Criteria criteria = new Criteria();
        if (!isEmpty(excludeCatids)) {
            criteria.and("catid").nin(excludeCatids);
        }
        //按运营要求，推荐里不出现合集视频
        criteria.and("collection_id").is(0);
        criteria.and("album_id").is(0);
        ConditionalOperators.Cond cond = ConditionalOperators.Cond.when(Criteria.where("catid").is(catid)).then(0).otherwise(1);
        ProjectionOperation project = project().andInclude("video_id", "catid").andExclude("_id")
                .and(cond).as("sortv");
        SortOperation sort = null;
        if(isABTest){
            sort = sort(Sort.by(Sort.Order.asc("sortv"), Sort.Order.desc("weights1")));
        } else {
            sort = sort(Sort.by(Sort.Order.asc("sortv"), Sort.Order.desc("weights")));
        }
        //这里加20是保证了redis缓存中会过滤的视频数。预加载20条
        LimitOperation limit = limit(getSize + 20);
        if (!isEmpty(history)) {
            List<String> videoIds = history.stream().map(UserIncentiveLog::getVideo_id).collect(Collectors.toList());
            criteria.and("video_id").nin(videoIds);
            history.clear();
        }
        criteria.and("state").is(1);
        MatchOperation match = match(criteria);
        Aggregation agg = Aggregation.newAggregation(match, project, sort, limit);
        AggregationResults<IncentiveVideoHotspot> aggVideoIdsObj = mongoTemplate.aggregate(agg, "incentive_video_hotspot", IncentiveVideoHotspot.class);
        List<IncentiveVideoHotspot> videoIdsObj = aggVideoIdsObj.getMappedResults();
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
                    if (getSize <= rdata.size()) {
                        break;
                    }
                }
            }
            videoIdsObj = null;
            return rdata;
        }

        List<IncentiveVideoHotspot> list = new ArrayList<>(getSize);
        for(int i = 0; i < getSize; i++){
            list.add(videoIdsObj.get(i));
        }
        return list;
    }

    /**
     * 记录历史记录
     * @param uuid
     * @param videoIds
     * @return
     */
    public boolean recordHistory(String uuid, List<IncentiveVideoHotspot> videoIds) {
        if(CollectionUtils.isEmpty(videoIds)){
            return false;
        }
        List<String> keys = Lists.newArrayList();
        String[] list = videoIds.stream().flatMap(e -> {
            String key = KEY_PREFIX.concat(DigestUtils.md5Hex(uuid.concat(e.getVideo_id())));
            keys.add(key);
            return Stream.of(key, "1");
        }).toArray(String[]::new);
        try (Jedis con = recDB9Pool.getResource()) {
            con.mset(list);
        }
        recommendDisruptorService.pushEvent(1, uuid, keys);
        return true;
    }
}