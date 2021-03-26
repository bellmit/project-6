package com.miguan.laidian.rabbitMQ.listener;

import com.alibaba.fastjson.JSON;
import com.miguan.laidian.entity.Video;
import com.miguan.laidian.rabbitMQ.util.RabbitMQConstant;
import com.miguan.laidian.service.VideoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 更新用户收藏数量、分享数量、点击数
 */
@EnableRabbit
@Component
public class VideoExposureMqProcessor {

    @Resource
    private VideoService videosService;

    @RabbitListener(autoStartup = "#{environment['spring.rabbitmq.open']}", bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.VIDEO_EXPOSURE_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.VIDEO_EXPOSURE_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.VIDEO_EXPOSURE_KEY)
    })
    public void processor(String jsonMsg) {
        List<Video> videoList = JSON.parseArray(jsonMsg, Video.class);
        videosService.addVideoExposure(videoList);
    }
}
