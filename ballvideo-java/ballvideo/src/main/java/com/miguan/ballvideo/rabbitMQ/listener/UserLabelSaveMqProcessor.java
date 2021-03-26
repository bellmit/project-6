package com.miguan.ballvideo.rabbitMQ.listener;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.entity.UserLabel;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.UserLabelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用户标签保存
 * @author shixh
 * @date 2019-12-11
 */
@Slf4j
@EnableRabbit
@Component
public class UserLabelSaveMqProcessor {

    @Resource
    private UserLabelService userLabelService;

    @RabbitListener(autoStartup="#{environment['spring.rabbitmq.open']}",bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.UserLabel_SAVE_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.UserLabel_SAVE_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.UserLabel_SAVE_KEY)
    })
    public void processor(String jsonMsg, @Header(AmqpHeaders.CONSUMER_QUEUE) String queue) {
        UserLabel vo = JSON.parseObject(jsonMsg,UserLabel.class);
        if (vo!=null){
            vo = userLabelService.saveToDB(vo);
            if (vo!=null) {
                userLabelService.updateIdFromRedis(vo);
            }
        }

    }

}
