package com.miguan.ballvideo.rabbitMQ.listener;

import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.UserBuriedPointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 异步处理垃圾清理埋点
 * @author xujinbang
 * @date 2019-11-7
 */
@Slf4j
@EnableRabbit
@Component
public class BuryPointGarbageProcessor {

    @Resource
    private UserBuriedPointService userBuriedPointService;

    @RabbitListener(autoStartup="#{environment['spring.rabbitmq.open']}",bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.BURYPOINT_GARBAGE_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.BURYPOINT_GARBAGE_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.BURYPOINT_GARBAGE_KEY)
    })
    public void processor(String jsonString, @Header(AmqpHeaders.CONSUMER_QUEUE) String queue) {
        try {
            userBuriedPointService.saveEntryGarbageBuryingPoint(jsonString);
        } catch (Exception e) {
            log.error("垃圾清理埋点出现异常：" + e.getMessage());
        }
    }
}