package com.miguan.ballvideo.rabbitMQ.listener;


import com.cgcg.context.util.StringUtils;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;


/**
 * 测试 新埋点MQ使用
 */
@Slf4j
//@EnableRabbit
//@Component
public class AdvDspMqProcessor {

    @RabbitListener(autoStartup="#{environment['spring.rabbitmq.open']}",bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.BURYPOINT_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.BURYPOINT_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.BURYPOINT_RUTE_KEY)
    })
    public void processor(String jsonMsg){
        if (StringUtils.isBlank(jsonMsg))return;
        try{
            System.out.println(jsonMsg);
        }catch (Exception e){
            log.error(jsonMsg);
            log.error(e.getMessage(),e);
        }
    }

}
