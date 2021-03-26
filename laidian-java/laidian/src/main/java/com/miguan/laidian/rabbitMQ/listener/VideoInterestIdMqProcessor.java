package com.miguan.laidian.rabbitMQ.listener;

import com.alibaba.fastjson.JSON;
import com.miguan.laidian.rabbitMQ.util.RabbitMQConstant;
import com.miguan.laidian.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 感兴趣视频ID列表保存redis
 * @author laiyd
 */
@Slf4j
@EnableRabbit
@Component
public class VideoInterestIdMqProcessor {

    @Resource
    private VideoService videoService;

    @RabbitListener(autoStartup = "#{environment['spring.rabbitmq.open']}", bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.VIDEO_INTEREST_ID_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.VIDEO_INTEREST_ID_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.VIDEO_INTEREST_ID_KEY)
    })
    public void processor(String jsonMsg) {
        try {
            Map<String, String> params = JSON.parseObject(jsonMsg, Map.class);
            String ids = MapUtils.getString(params, "ids");
            String deviceId = MapUtils.getString(params, "deviceId");
            String interestCatIds = MapUtils.getString(params, "interestCatIds");
            videoService.saveInterestVideoIds(ids, deviceId, interestCatIds);
        } catch (Exception e) {
            log.error(StringUtils.trimToEmpty(e.getMessage()).concat("感兴趣视频ID列表保存redis,MQ消费异常：{}").concat(jsonMsg),e);
        }
    }
}
