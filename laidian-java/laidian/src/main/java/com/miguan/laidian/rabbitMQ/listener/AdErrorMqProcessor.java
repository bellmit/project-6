package com.miguan.laidian.rabbitMQ.listener;


import com.miguan.laidian.rabbitMQ.util.RabbitMQConstant;
import com.miguan.laidian.service.AdErrorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 广告错误日志保存
 */
@Slf4j
@EnableRabbit
@Component
public class AdErrorMqProcessor {

    @Resource
    private AdErrorService adErrorService;

    @RabbitListener(autoStartup = "#{environment['spring.rabbitmq.open']}", bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.AD_ERROR_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.AD_ERROR_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.AD_ERROR_KEY)
    })
    public void addError(String jsonMsg) {
        adErrorService.addError(jsonMsg);
    }
}
