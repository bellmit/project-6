package com.miguan.ballvideo.service.recommend;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.common.util.TimeUtil;
import com.miguan.ballvideo.dynamicquery.DynamicQuery;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.vo.mongodb.IncentiveVideoHotspot;
import com.miguan.ballvideo.vo.mongodb.VideoHotspotVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Description 处理mongodb中的激励视频和普通视频的权重表
 * @Author zhangbinglin
 * @Date 2020/8/13 15:19
 **/
@Slf4j
@Service
public class VideoHotspotServiceImpl implements VideoHotspotService {

    @Resource(name = "logMongoTemplate")
    private MongoTemplate mongoTemplate;
    @Resource
    private DynamicQuery dynamicQuery;
    @Resource
    private RabbitTemplate rabbitTemplate;


    /**
     *
     * @param videoId
     * @param isIncentive 是否激励视频
     * @param catid
     * @param state
     * @param collectionId
     * @param onLinedate
     * @param videoTime 视频时长
     * @param weight
     * @param weight1
     */
    @Override
    public void addOrUpdateHotspot(String videoId, boolean isIncentive, Integer catid, Integer state, Integer collectionId, String onLinedate, String videoTime, Double weight, Double weight1) {
        int videoSecond = TimeUtil.changeMinuteStringToSecond(videoTime);

        Date date = new Date();
        Update update = new Update();
        if (catid != null) {
            update.set("catid", catid);
        }
        if (state != null) {
            update.set("state", state);
        }
        if (collectionId != null) {
            update.set("collection_id", collectionId);
        }
        if (onLinedate != null) {
            update.set("online_time", onLinedate);
        }
        update.set("update_at", date);
        if (isIncentive && state == 1) {
            //是激励视频
            Query query = new Query(Criteria.where("video_id").is(videoId).and("state").is(1));
            //查找激励视频库是否有启用的视频记录,有则更新
            IncentiveVideoHotspot incentiveVideoHotspot = mongoTemplate.findAndModify(query, update, IncentiveVideoHotspot.class);
            if (incentiveVideoHotspot != null) {
                //如果激励视频库已有启用的视频记录，则更新完成
            } else {
                //找不到启用的激励视频则找普通视频启用状态的记录
                VideoHotspotVo videoHotspot = mongoTemplate.findOne(query, VideoHotspotVo.class);
                //如果找到普通视频有启用的记录，则迁移权重
                if (videoHotspot != null) {
                    weight = videoHotspot.getWeights();
                    weight1 = videoHotspot.getWeights1();
                } else {
                    //如果找不到有启用的普通视频记录，则查找历史记录
                    Double[] weights = getWeight(videoId, weight, weight1);
                    weight = weights[0];
                    weight1 = weights[1];
                }
                incentiveVideoHotspot = new IncentiveVideoHotspot(videoId, catid, state, collectionId, weight, weight1, onLinedate, videoSecond, date, date);
                log.warn("推荐0.1新增激励视频TOP记录：{}", JSON.toJSONString(incentiveVideoHotspot));
                mongoTemplate.save(incentiveVideoHotspot);
            }
            //为了纠正视频情况，则将普通视频修改状态为禁用
            mongoTemplate.findAndModify(query, Update.update("state", 0), VideoHotspotVo.class);
            //删除冗余禁用的激励视频
            query = new Query(Criteria.where("video_id").is(videoId).and("state").is(0));
            mongoTemplate.remove(query, IncentiveVideoHotspot.class);
        } else if (!isIncentive && state == 1) {
            //不是激励视频，是普通视频
            Query query = new Query(Criteria.where("video_id").is(videoId).and("state").is(1));
            VideoHotspotVo videoHotspotVo = mongoTemplate.findAndModify(query, update, VideoHotspotVo.class);
            if (videoHotspotVo != null) {
                //如果普通视频库已有启用的视频记录，则更新完成
            } else {
                //找不到启用的普通视频则找激励视频启用状态的记录
                IncentiveVideoHotspot incentiveVideoHotspot = mongoTemplate.findOne(query, IncentiveVideoHotspot.class);
                //如果找到激励视频有启用的记录，则迁移权重
                if (incentiveVideoHotspot != null) {
                    weight = incentiveVideoHotspot.getWeights();
                    weight1 = incentiveVideoHotspot.getWeights1();
                } else {
                    //如果找不到有启用的激励视频记录，则查找历史记录
                    Double[] weights = getWeight(videoId, weight, weight1);
                    weight = weights[0];
                    weight1 = weights[1];
                }
                videoHotspotVo = new VideoHotspotVo(videoId, catid, state, collectionId, weight, weight1, onLinedate, videoSecond, date, date);
                log.warn("推荐0.1新增视频TOP记录：{}", JSON.toJSONString(videoHotspotVo));
                mongoTemplate.save(videoHotspotVo);
            }
            //为了纠正视频情况，则将激励视频修改状态为禁用
            mongoTemplate.findAndModify(query, Update.update("state", 0), IncentiveVideoHotspot.class);
            //删除冗余禁用的普通视频
            query = new Query(Criteria.where("video_id").is(videoId).and("state").is(0));
            mongoTemplate.remove(query, VideoHotspotVo.class);
        } else if (state == 0) {
            Query query = new Query(Criteria.where("video_id").is(videoId).and("state").is(1));
            mongoTemplate.findAndModify(query, Update.update("state", 0), IncentiveVideoHotspot.class);
            mongoTemplate.findAndModify(query, Update.update("state", 0), VideoHotspotVo.class);
        }
    }

    /**
     * 查找普通视频和激励视频中最新的视频权重记录，以最新更新时间的记录里的权重为准
     * @param videoId
     * @param defaultWeight
     * @return
     */
    private Double[] getWeight(String videoId, Double defaultWeight, Double defaultWeight1) {
        Double[] weight = new Double[]{defaultWeight, defaultWeight1};
        Query query = new Query(Criteria.where("video_id").is(videoId));
        query.with(Sort.by(Sort.Order.desc("update_at")));
        IncentiveVideoHotspot incentiveVideoHotspot = mongoTemplate.findOne(query, IncentiveVideoHotspot.class, "incentive_video_hotspot");
        VideoHotspotVo videoHotspotVo = mongoTemplate.findOne(query, VideoHotspotVo.class, "video_hotspot");
        if (incentiveVideoHotspot == null && videoHotspotVo == null) {
            return weight;
        }
        if (incentiveVideoHotspot != null && videoHotspotVo != null) {
            int cn = incentiveVideoHotspot.getCreate_at().compareTo(videoHotspotVo.getCreate_at());
            if (cn == -1) {
                weight[0] = videoHotspotVo.getWeights();
                weight[1] = videoHotspotVo.getWeights1();
            } else {
                weight[0] = incentiveVideoHotspot.getWeights();
                weight[1] = incentiveVideoHotspot.getWeights1();
            }
        } else if (incentiveVideoHotspot != null) {
            weight[0] = incentiveVideoHotspot.getWeights();
            weight[1] = incentiveVideoHotspot.getWeights1();
        } else if (videoHotspotVo != null) {
            weight[0] = videoHotspotVo.getWeights();
            weight[1] = videoHotspotVo.getWeights1();
        }
        return weight;

    }

    /**
     * 保存普通视频权重
     * @param videoId  视频id
     * @param catid  分类id
     * @param state  状态： 0 = 禁用 1 = 启用
     * @param collectionId  合集id
     */
    @Override
    public void updateHotspot(String videoId, Integer catid, Integer state, Integer collectionId) {
        Query query = new Query(Criteria.where("video_id").is(videoId));
        Date date = new Date();
        Update update = new Update();
        if (catid != null) {
            update.set("catid", catid);
        }
        if (state != null) {
            update.set("state", state);
        }
        if (collectionId != null) {
            update.set("collection_id", collectionId);
        }
        update.set("update_at", date);
        mongoTemplate.findAndModify(query, update, VideoHotspotVo.class);
    }

    /**
     * 保存激励视频权重
     * @param videoId  视频id
     * @param catid  分类id
     * @param state  状态： 0 = 禁用 1 = 启用
     * @param collectionId  合集id
     */
    @Override
    public void updateIncentiveHotspot(String videoId, Integer catid, Integer state, Integer collectionId) {
        Query query = new Query(Criteria.where("video_id").is(videoId));
        Date date = new Date();
        Update update = new Update();
        if (catid != null) {
            update.set("catid", catid);
        }
        if (state != null) {
            update.set("state", state);
        }
        if (collectionId != null) {
            update.set("collection_id", collectionId);
        }
        update.set("update_at", date);
        mongoTemplate.findAndModify(query, update, IncentiveVideoHotspot.class);

    }

    /**
     * 设置普通视频权重表的状态为：禁用
     * @param videoId 视频id
     */
    @Override
    public void deleteHotspot(String videoId) {
        Query query = new Query(Criteria.where("video_id").is(videoId));
        Update update = Update.update("state", 0);
        mongoTemplate.findAndModify(query, update, VideoHotspotVo.class, "video_hotspot");
    }

    /**
     * 设置激励视频权重表的状态为：禁用
     * @param videoId
     */
    @Override
    public void deleteIncentiveHotspot(String videoId) {
        Query query = new Query(Criteria.where("video_id").is(videoId));
        Update update = Update.update("state", 0);
        mongoTemplate.findAndModify(query, update, IncentiveVideoHotspot.class, "incentive_video_hotspot");
    }

    /**
     * 删除全部普通视频权重信息
     */
    public void deleteAllHotspot() {
        Query query = new Query();
        mongoTemplate.remove(query, VideoHotspotVo.class);
    }

    /**
     * 删除全部激励视频权重信息
     */
    public void deleteAllIncentiveHotspot() {
        Query query = new Query();
        mongoTemplate.remove(query, IncentiveVideoHotspot.class);
    }

    /**
     * 初始化mongodb权重信息(慎重：此接口会先删除全部数据，在重新同步)
     */
    @Override
    public void hotspotInit() {
        String countSQL = "select count(*) from first_videos where state = 1";
        String querySql = "select id,state,is_incentive incentiveVideo, cat_id catId, IFNULL(gather_id, 0) AS gatherId,online_date as onlineDate, video_time as videoTime from first_videos ";
        Object o = dynamicQuery.nativeQueryObject(countSQL);
        if (o == null) {
            return;
        }
        int count = Integer.parseInt(o + "");
        int loop = 0;

        deleteAllHotspot();  //删除全部普通视频权重信息
        deleteAllIncentiveHotspot();  //删除全部激励视频权重信息
        while (true) {
            String append = " limit " + loop + ", 5000";
            String sql = querySql + append;
            rabbitTemplate.convertAndSend(RabbitMQConstant.HOTSPOST_INIT_EXCHANGE, RabbitMQConstant.HOTSPOST_INIT_RUTE_KEY, sql);
            if (loop >= count) {
                break;
            }
            loop += 5000;
        }
    }

    @Override
    public void washIncentiveVideo() {
        rabbitTemplate.convertAndSend(RabbitMQConstant.HOTSPOST_INIT_EXCHANGE, RabbitMQConstant.HOTSPOST_INIT_RUTE_KEY, "washIncentiveVideo");
    }
}
