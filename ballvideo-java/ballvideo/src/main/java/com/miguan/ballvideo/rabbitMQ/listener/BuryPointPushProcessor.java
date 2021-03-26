package com.miguan.ballvideo.rabbitMQ.listener;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.entity.UserBuryingPointPush;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.UserBuriedPointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 异步处理推送埋点
 * @author hyl
 * @date 2019年12月20日16:18:48
 */
@Slf4j
@Component
public class BuryPointPushProcessor {

    @Resource
    private UserBuriedPointService userBuriedPointService;

    @RabbitListener(autoStartup="#{environment['spring.rabbitmq.open']}",bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.BURYPOINT_PUSH_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.BURYPOINT_PUSH_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.BURYPOINT_PUSH_KEY)
    })
    public void processor(String jsonMsg, @Header(AmqpHeaders.CONSUMER_QUEUE) String queue) {
        UserBuryingPointPush buryingPointPush = JSON.parseObject(jsonMsg, UserBuryingPointPush.class);
        userBuriedPointService.savePushBuryingPoint(buryingPointPush);
    }
}
