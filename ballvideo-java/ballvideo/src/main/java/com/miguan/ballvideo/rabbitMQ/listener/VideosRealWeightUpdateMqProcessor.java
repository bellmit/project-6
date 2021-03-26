package com.miguan.ballvideo.rabbitMQ.listener;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.FirstVideosOldService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 修改视频真实权重
 */
@Slf4j
@EnableRabbit
@Component
public class VideosRealWeightUpdateMqProcessor {

    @Resource
    private FirstVideosOldService firstVideosOldService;

    @RabbitListener(autoStartup="#{environment['spring.rabbitmq.open']}",bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.VIDEO_REALWEIGHT_UPDATE_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.VIDEO_REALWEIGHT_UPDATE_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.VIDEO_REALWEIGHT_UPDATE_KEY)
    })
    public void processor(String jsonMsg) {
        try {
            Map<Long, Long> params = JSON.parseObject(jsonMsg,Map.class);
            firstVideosOldService.updateVideosRealWeight(params);
        } catch (Exception e) {
            log.error(StringUtils.trimToEmpty(e.getMessage()).concat(" 修改视频真实权重MQ消费异常,数据 ").concat(jsonMsg),e);
        }
    }
}
