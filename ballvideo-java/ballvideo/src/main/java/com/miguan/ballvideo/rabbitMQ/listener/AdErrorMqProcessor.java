package com.miguan.ballvideo.rabbitMQ.listener;


import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.AdErrorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 广告 错误信息保存
 * @author hyl
 * @date 2020年4月20日17:18:50
 */
@Slf4j
@EnableRabbit
@Component
public class AdErrorMqProcessor {

    @Resource
    AdErrorService adErrorService;

    @RabbitListener(autoStartup="#{environment['spring.rabbitmq.open']}",bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.AD_ERROR_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.AD_ERROR_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.AD_ERROR_KEY)
    })
    public void addError(String jsonMsg){
        adErrorService.addError(jsonMsg);
    }
}
