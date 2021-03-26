package com.miguan.laidian.rabbitMQ.listener;

import com.alibaba.fastjson.JSON;
import com.miguan.laidian.rabbitMQ.util.RabbitMQConstant;
import com.miguan.laidian.service.AudioService;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 更新用户收藏数量、分享数量、点击数
 */
@EnableRabbit
@Component
public class AudioUpdateCountMqProcessor {

    @Resource
    private AudioService audioService;

    @RabbitListener(autoStartup = "#{environment['spring.rabbitmq.open']}", bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.AUDIO_UPDATECOUNT_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.AUDIO_UPDATECOUNT_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.AUDIO_UPDATECOUNT_KEY)
    })
    public void processor(String jsonMsg) {
        Map<String, Object> params = JSON.parseObject(jsonMsg, Map.class);
        audioService.updateAudioCount(params);
    }
}
