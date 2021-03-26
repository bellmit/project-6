package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.DeviceVideoLogService;
import com.miguan.ballvideo.service.VideoExposureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DeviceVideoLogServiceImpl implements DeviceVideoLogService {
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private VideoExposureService videoExposureService;

//    @Resource
//    private RedisRecoDB8Service redisRecoDB8Service;

    @Override
    public void saveVideoInfoToRedis(String deviceId, Long videoId) {
        /** 推荐算法优化 调用更新 video_id 的 show 方法*/
        //videoExposureService.videoExposureCountSendToMQ(deviceId, String.valueOf(videoId));
        //saveCtrShowToRedis(deviceId,videoId);
        /** 推荐算法优化 用户点击视频，更新点击数量到 redis 结束 */
    }

    @Override
    public void saveDeviceCatShowIdToRedis(String deviceId, Long catId, Long videoId) {
        JSONObject message = new JSONObject();
        message.put("deviceId", deviceId);
        message.put("catId", catId);
        message.put("videoId", videoId);
        rabbitTemplate.convertAndSend(RabbitMQConstant.RCMD_REDIS_DEVICE_SHOW_SAVE_EXCHANGE,
                RabbitMQConstant.RCMD_REDIS_DEVICE_SHOW_SAVE_KEY, message.toJSONString());

//        String key = RedisKeyConstant.RECO_DEVICE_SHOW_ID + deviceId + ":" + catId.toString();
//        //从配置文件取配置，记录用户曝光的视频id的上限是多少，超过就不再记录
//        Integer limit = Global.getInt("exposure_max_limit");
//        Map<String, String> map = (redisRecoDB8Service.hgetAll(key) == null ? new HashMap<>() : redisRecoDB8Service.hgetAll(key));
//        if (map.size() < limit) {
//            String showDay = DateUtils.formatDate(new Date(), "yyyyMMdd");
//            redisRecoDB8Service.hset(key, videoId.toString(), showDay);
//        } else {
//            log.info("设备:{}，分类:{}，超过允许记录的视频总数，视频id:{},不再记录", deviceId, catId, videoId);
//        }
    }

    @Override
    public void saveCtrShowToRedis(String deviceId,Long videoId) {
        try {
            Map<String, Object> showMap = new HashMap<>();
            showMap.put("device_id", deviceId);
            showMap.put("video_id", String.valueOf(videoId));
            String showDataStr = JSON.toJSONString(showMap);
            rabbitTemplate.convertAndSend(RabbitMQConstant.RCMD_CTR_REDIS_SHOW_SAVE_EXCHANGE,
                    RabbitMQConstant.RCMD_CTR_REDIS_SHOW_SAVE_KEY, showDataStr);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void saveCtrClickToRedis(String deviceId,Long videoId) {
        try{
            Map<String, Object> clickMap = new HashMap<>();
            clickMap.put("device_id", deviceId);
            clickMap.put("video_id", String.valueOf(videoId));
            String clickDataStr = JSON.toJSONString(clickMap);
            rabbitTemplate.convertAndSend(RabbitMQConstant.RCMD_CTR_REDIS_CLICK_SAVE_EXCHANGE,
                    RabbitMQConstant.RCMD_CTR_REDIS_CLICK_SAVE_KEY, clickDataStr);
            //1小時點擊數
            String jsonMsg = deviceId + "@" + videoId;
            rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_HOUR_CLICK_SAVE_EXCHANGE,
                    RabbitMQConstant.VIDEO_HOUR_CLICK_SAVE_KEY, jsonMsg);
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

}
