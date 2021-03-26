package com.miguan.ballvideo.rabbitMQ.listener.recommend;

import com.miguan.ballvideo.common.util.DateUtil;
import com.miguan.ballvideo.common.util.TimeUtil;
import com.miguan.ballvideo.dynamicquery.DynamicQuery;
import com.miguan.ballvideo.service.recommend.VideoHotspotService;
import com.miguan.ballvideo.vo.mongodb.IncentiveVideoHotspot;
import com.miguan.ballvideo.vo.mongodb.VideoHotspotVo;
import com.miguan.ballvideo.vo.video.RecVideosVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 索引数据操作MQ
 * @author shixh
 * @date 2020-09-01
 */
@Slf4j
@EnableRabbit
@Component
public class HotspostInitMqProcessor {

    @Resource
    private DynamicQuery dynamicQuery;
    @Resource(name = "logMongoTemplate")
    private MongoTemplate mongoTemplate;
    @Resource(name = "recDB9Pool")
    private JedisPool recDB9Pool;

    @Autowired
    private VideoHotspotService videoHotspotService;

    /*@RabbitListener(autoStartup = "#{environment['spring.rabbitmq.open']}", bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.HOTSPOST_INIT_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.HOTSPOST_INIT_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.HOTSPOST_INIT_RUTE_KEY)
    })*/
    public void processor(String sql) {
        if ("washIncentiveVideo".equals(sql)) {
            washIncentiveVideo();
            return;
        }
        log.info("(start)初始化mongodb权重信息, 执行语句{}：", sql);
        try {
            List<RecVideosVo> videos = dynamicQuery.nativeQueryList(RecVideosVo.class, sql);
            //  从redis获取初始权重值
            String weights = null;
            String weights1 = null;
            try (Jedis jedis = recDB9Pool.getResource()) {
                weights = jedis.get("bg_videoCount:allVpr");
                weights1 = jedis.get("bg_videoCount:allVptr");
            }
            weights = StringUtils.isBlank(weights) ? "0" : weights;
            weights1 = StringUtils.isBlank(weights1) ? "0" : weights1;
            double initweight = Double.parseDouble(weights);
            double initweight1 = Double.parseDouble(weights1);
            for (RecVideosVo video : videos) {
                int state = video.getState() == 1 ? 1 : 0;
                //更新普通视频的权重信息到mongodb
                Date date = new Date();
                String onLinedate = DateUtil.dateStr4(video.getOnlineDate());
                Update update = new Update();
                update.set("catid", video.getCatId());
                update.set("state", state);
                update.set("collection_id", video.getGatherId());
                update.set("online_time", onLinedate);
                int videoTime = TimeUtil.changeMinuteStringToSecond(video.getVideoTime());
                update.set("video_time", videoTime);
                //更新激励视频的权重信息到mongodb
                if (video.getState() == 1 && video.getIncentiveVideo() == 1) {
                    //初始激励视频权重的时候，只有是激励视频才保存到权重表
                    Query query = new Query(Criteria.where("video_id").is(video.getId().toString()));

                    IncentiveVideoHotspot vo = mongoTemplate.findAndModify(query, update, IncentiveVideoHotspot.class, "incentive_video_hotspot");
                    if (vo == null) {
                        IncentiveVideoHotspot incentiveVideoHotspot = new IncentiveVideoHotspot(video.getId().toString(), video.getCatId().intValue(), state, video.getGatherId().intValue(), initweight, initweight1, onLinedate, videoTime, date, date);
                        mongoTemplate.save(incentiveVideoHotspot);
                    }
                } else if (video.getState() == 1) {
                    Query query = new Query(Criteria.where("video_id").is(video.getId().toString()));
                    VideoHotspotVo vo = mongoTemplate.findAndModify(query, update, VideoHotspotVo.class, "video_hotspot");
                    if (vo == null) {
                        VideoHotspotVo videoHotspot = new VideoHotspotVo(video.getId().toString(), video.getCatId().intValue(), state, video.getGatherId().intValue(), initweight, initweight1, onLinedate, videoTime, date, date);
                        mongoTemplate.save(videoHotspot);
                    }
                }
            }
        } catch (Exception e) {
            log.error("初始化mongodb权重信息, 执行语句:{}", sql);
            log.error("初始化mongodb权重信息异常", e);
        }
        log.info("(end)初始化mongodb权重信息, 执行语句：{}", sql);
    }

    private void washIncentiveVideo() {
        String sql = "select id,state,is_incentive incentiveVideo, cat_id catId, IFNULL(gather_id, 0) AS gatherId, online_date as onlineDate, video_time as videoTime from first_videos where is_incentive = 1";
        List<RecVideosVo> videos = dynamicQuery.nativeQueryList(RecVideosVo.class, sql);
        List<String> ids = videos.stream().map(e -> e.getId().toString()).collect(Collectors.toList());
        Query query = new Query(Criteria.where("video_id").nin(ids));
        //将不是激励视频全部改成禁用
        mongoTemplate.updateMulti(query, Update.update("state", 0).set("update_at", new Date()), "incentive_video_hotspot");
        String weights = null;
        String weights1 = null;
        //  从redis获取初始权重值
        try (Jedis jedis = recDB9Pool.getResource()) {
            weights = jedis.get("bg_videoCount:allVpr");
            weights1 = jedis.get("bg_videoCount:allVptr");
        }
        Double weight = StringUtils.isBlank(weights) ? Double.valueOf(0) : Double.valueOf(weights);
        Double weight1 = StringUtils.isBlank(weights1) ? Double.valueOf(0) : Double.valueOf(weights1);
        //遍历全部激励视频，如果没有的则新增
        for (int i = 0; i < videos.size(); i++) {
            RecVideosVo video = videos.get(i);
            String onLinedate = DateUtil.dateStr4(video.getOnlineDate());
            videoHotspotService.addOrUpdateHotspot(video.getId().toString(), true, video.getCatId().intValue(), video.getState() == 1 ? 1 : 0, video.getGatherId().intValue(), onLinedate, video.getVideoTime(), weight, weight1);
        }
    }
}
