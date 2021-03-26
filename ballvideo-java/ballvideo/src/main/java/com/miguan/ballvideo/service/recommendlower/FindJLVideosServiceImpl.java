package com.miguan.ballvideo.service.recommendlower;

import com.google.common.collect.Lists;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.FindJLVideosService;
import com.miguan.ballvideo.service.RedisDB8Service;
import com.miguan.ballvideo.vo.mongodb.IncentiveVideoHotspot;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

/**激励视频推荐
 */
@Service
@Slf4j
public class FindJLVideosServiceImpl implements FindJLVideosService {

    @Resource(name = "logMongoTemplate")
    private MongoTemplate mongoTemplate;
    @Resource(name = "redisDB8Service")
    private RedisDB8Service redisDB8Service;

    /**
     *
     * @param showedVideoIds
     * @param catid
     * @param count
     * @param excludeCatids
     * @param excludeCollectids
     * @return
     */
    @Override
    public List<IncentiveVideoHotspot> getVideoId(String deviceId, String showedVideoIds, int catid, int count, List<Integer> excludeCatids, List<Integer> excludeCollectids) {
        //新通过分类获取激励视频
        long pt = System.currentTimeMillis();
        List<IncentiveVideoHotspot> jlvideo = this.getVideoId(showedVideoIds, catid, count, excludeCatids, excludeCollectids);
        //log.warn("推荐0.0规则4-1时长：{}", (System.currentTimeMillis() - pt));
        //如果没有获取到激励视频则将用户点击历史记录给删除再获取
        if (isEmpty(jlvideo)) {
            //log.warn("推荐0.0规则4-2时长：{}", (System.currentTimeMillis() - pt));
            String key = RedisKeyConstant.SHOWEDIDS_KEY.concat(deviceId).concat(":").concat(RedisKeyConstant.INCENTIVEVIDEO);
            redisDB8Service.del(key);
            jlvideo = this.getVideoId(null, catid, count, excludeCatids, excludeCollectids);
        }
        return jlvideo;
    }

    /**
     *
     * @param showedVideoIds
     * @param catid
     * @param count
     * @param excludeCatids
     * @param excludeCollectids
     * @return
     */
    @Override
    public List<IncentiveVideoHotspot> getVideoId(String showedVideoIds, int catid, int count, List<Integer> excludeCatids, List<Integer> excludeCollectids) {
        //新通过分类获取激励视频
        long pt = System.currentTimeMillis();
        ArrayList<String> showedVideoList = StringUtils.isNotBlank(showedVideoIds) ? Lists.newArrayList(showedVideoIds.split(",")) : null;
        List<IncentiveVideoHotspot> jlvideo = this.findAndFilterVideo(showedVideoList, count, catid, excludeCatids, excludeCollectids);
        //log.warn("推荐0.0规则4-1时长：{}", (System.currentTimeMillis() - pt));
        return jlvideo;
    }

    /**
     * 根据条件获取
     * @param catid
     * @param count
     * @param excludeCollectids
     * @return
     */
    private List<IncentiveVideoHotspot> getVideoId(String showedVideoIds, int catid, int count, List<Integer> excludeCollectids) {
        ArrayList<String> showedVideoList = StringUtils.isNotBlank(showedVideoIds) ? Lists.newArrayList(showedVideoIds.split(",")) : null;
        return findAndFilterVideo(showedVideoList, count, catid, null, excludeCollectids);
    }

    /**
     *
     * @param getSize 此次要获取的视频目标数量
     * @param catid  筛选此分类的视频集
     * @param excludeCatids  屏蔽分类
     * @param excludeCollectids 屏蔽用户合集
     * @return
     */
    private List<IncentiveVideoHotspot> findAndFilterVideo(List<String> showedVideoIds, int getSize, Integer catid, List<Integer> excludeCatids, List<Integer> excludeCollectids) {
        Criteria criteria = new Criteria();
        if (!isEmpty(excludeCatids)) {
            criteria.and("catid").nin(excludeCatids);
        }
//        if (!isEmpty(excludeCollectids)) {
//            criteria.and("collection_id").nin(excludeCollectids);
//        }
        //按运营要求，推荐里不出现合集视频
        criteria.and("collection_id").is(0);
        criteria.and("album_id").is(0);
        ConditionalOperators.Cond cond = ConditionalOperators.Cond.when(Criteria.where("catid").is(catid)).then(0).otherwise(1);
        ProjectionOperation project = project().andInclude("video_id", "catid").andExclude("_id")
                .and(cond).as("sortv");
        SortOperation sort = sort(Sort.by(Sort.Order.asc("sortv"), Sort.Order.desc("weights")));
        LimitOperation limit = limit(getSize);
        if (!isEmpty(showedVideoIds)) {
            criteria.and("video_id").nin(showedVideoIds);
        }
        criteria.and("state").is(1);
        MatchOperation match = match(criteria);
        Aggregation agg = Aggregation.newAggregation(match, project, sort, limit);
        AggregationResults<IncentiveVideoHotspot> aggVideoIdsObj = mongoTemplate.aggregate(agg, "incentive_video_hotspot", IncentiveVideoHotspot.class);
        List<IncentiveVideoHotspot> videoIdsObj = aggVideoIdsObj.getMappedResults();
        if (isEmpty(videoIdsObj)) {
            return null;
        }
        return videoIdsObj;
    }
}