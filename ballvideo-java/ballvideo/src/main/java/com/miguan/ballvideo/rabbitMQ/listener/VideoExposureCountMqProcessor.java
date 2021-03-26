package com.miguan.ballvideo.rabbitMQ.listener;

import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.VideoExposureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 曝光视频信息
 * @author laiyd
 * @date 2020-06-05
 */
@Slf4j
@EnableRabbit
@Component
public class VideoExposureCountMqProcessor {

    @Resource
    private VideoExposureService videoExposureService;

    @RabbitListener(autoStartup="#{environment['spring.rabbitmq.open']}",bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.VIDEO_EXPOSURE_COUNT_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.VIDEO_EXPOSURE_COUNT_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.VIDEO_EXPOSURE_COUNT_KEY)
    })
    public void videoExposureCountSendToMQ(String jsonMsg){
        videoExposureService.videoExposureSendToRedis(jsonMsg);
    }
}
