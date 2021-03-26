package com.miguan.ballvideo.rabbitMQ.listener;


import com.alibaba.fastjson.JSON;
import com.cgcg.context.util.StringUtils;
import com.miguan.ballvideo.entity.AdvertErrorCountLog;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.AdvertErrorCountLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@EnableRabbit
@Component
public class AdErrorCountMqProcessor {

    @Resource
    private AdvertErrorCountLogService advertErrorCountLogService;

    @RabbitListener(autoStartup="#{environment['spring.rabbitmq.open']}",bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.AD_ERROR_COUNT_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.AD_ERROR_COUNT_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.AD_ERROR_COUNT_KEY)
    })
    public void processor(String jsonMsg){
        if (StringUtils.isBlank(jsonMsg))return;
        try{
            List<AdvertErrorCountLog> datas = JSON.parseArray(jsonMsg,AdvertErrorCountLog.class);
            advertErrorCountLogService.save(datas);
        }catch (Exception e){
            log.error(jsonMsg);
            log.error(e.getMessage(),e);
        }
    }

}
