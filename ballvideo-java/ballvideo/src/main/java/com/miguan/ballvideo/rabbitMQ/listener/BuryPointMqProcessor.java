package com.miguan.ballvideo.rabbitMQ.listener;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.DeviceVideoLogService;
import com.miguan.ballvideo.service.UserBuriedPointService;
import com.miguan.ballvideo.vo.BuryingActionType;
import com.miguan.ballvideo.vo.userBuryingPoint.UserBuryingPointVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 异步处理埋点
 * @author xujinbang
 * @date 2019-11-7
 */
@Slf4j
@EnableRabbit
@Component
public class BuryPointMqProcessor {

    @Resource
    private UserBuriedPointService userBuriedPointService;
    @Resource
    private DeviceVideoLogService deviceVideoLogService;

    @RabbitListener(autoStartup="#{environment['spring.rabbitmq.open']}",bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.BURYPOINT_LABEL_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.BURYPOINT_LABEL_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.BURYPOINT_LABEL_KEY)
    })
    public void processor(String jsonMsg, @Header(AmqpHeaders.CONSUMER_QUEUE) String queue) {
        UserBuryingPointVo userBuryingPointVo = JSON.parseObject(jsonMsg, UserBuryingPointVo.class);
        userBuriedPointService.insert(userBuryingPointVo);
    }

}
