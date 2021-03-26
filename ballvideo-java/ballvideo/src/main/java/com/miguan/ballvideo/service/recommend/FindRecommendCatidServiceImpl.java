package com.miguan.ballvideo.service.recommend;

import com.google.common.collect.Lists;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.constants.VideoContant;
import com.miguan.ballvideo.entity.UserLabelDefault;
import com.miguan.ballvideo.service.UserLabelDefaultService;
import com.miguan.ballvideo.service.VideosCatService;
import com.miguan.ballvideo.vo.mongodb.CatHotspotVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
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
import java.util.stream.Stream;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * 查询推荐标签
 */
@Slf4j
@Service
public class FindRecommendCatidServiceImpl {

    @Resource(name = "recDB9Pool")
    private JedisPool recDB9Pool;
    @Resource(name = "logMongoTemplate")
    private MongoTemplate mongoTemplate;
    @Resource
    private UserLabelDefaultService userLabelDefaultService;
    @Resource
    private VideosCatService videosCatService;

    public List<Integer> getUserCatids(String uuid, String channelId) {
        //从redis获取
        String userCatids = null;
        try (Jedis con = recDB9Pool.getResource()) {
            List<String> tmp = con.hmget("bg_sUp", uuid);
            if (!isEmpty(tmp)) {
                userCatids = tmp.get(0);
            }
            tmp = null;
        } catch (Exception e) {
        }
        if (isNotBlank(userCatids)) {
            String[] str = userCatids.split(",");
            if(str.length > 3){
                str = ArrayUtils.subarray(str, 0 ,3);
            }
            return Stream.of(str).map(Integer::valueOf).collect(Collectors.toList());
        }
        ArrayList<Integer> list = Lists.newArrayList();
        //lzhong 如果没有要从默认渠道获取
        if (isBlank(channelId)) {
            log.error("获取用户视频分类标签失败，但前端未传渠道ID所以无法获取默认的标签");
            return list;
        }
        UserLabelDefault userLabelDefault = userLabelDefaultService.getUserLabelDefault(channelId);

        if (userLabelDefault.getCatId1() != null) {
            list.add(userLabelDefault.getCatId1().intValue());
        }
        if (userLabelDefault.getCatId2() != null) {
            list.add(userLabelDefault.getCatId2().intValue());
        }
        return list;
    }

    public List<Integer> getHotspotCatids(int catid, List<Integer> excludeCatid) {
        //从mongo获取
        Query query = Query.query(Criteria.where("parent_catid").is(catid));
        if (!isEmpty(excludeCatid)) {
            query.addCriteria(Criteria.where("catid").nin(excludeCatid));
        }
        query.with(Sort.by(Sort.Order.asc("weights")));
//        query.limit(6);
        query.fields().include("catid").exclude("_id");
        List<CatHotspotVo> list = mongoTemplate.find(query, CatHotspotVo.class, "cat_hotspot");
        if (isEmpty(list)) {
            log.error("此标签 {} 相似的热度标签未找到", catid);
            //lzhong 需要给定几个默认的标签
            // return Lists.newArrayList(251, 4, 1, 2, 3, 4, 38, 48, 49, 50, 51, 52, 54, 194, 195, 196, 197, 198, 235, 242, 250, 999, 1000, 1001, 1004, 1005, 1006, 1008, 1009, 1010, 1011, 1012, 1016, 1017, 1019);
            List<String> catIds = videosCatService.getCatIdsByStateAndType(Constant.open, VideoContant.FIRST_VIDEO_CODE);
            List<Integer> intCatIds = catIds.stream().map(Integer::valueOf).collect(Collectors.toList());
            if(!isEmpty(excludeCatid)){
                intCatIds.removeAll(excludeCatid);
            }
            return intCatIds;
        }
        return list.stream().map(CatHotspotVo::getCatid).collect(Collectors.toList());
    }

}