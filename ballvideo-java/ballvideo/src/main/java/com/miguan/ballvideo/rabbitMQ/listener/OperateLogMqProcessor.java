package com.miguan.ballvideo.rabbitMQ.listener;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.entity.OperateLog;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.OperateLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 接口日志记录
 * @author shixh
 * @date 2019-12-23
 */
@Slf4j
@EnableRabbit
@Component
public class OperateLogMqProcessor {

    @Resource
    private OperateLogService operateLogService;

    @RabbitListener(autoStartup="#{environment['spring.rabbitmq.open']}",bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.OPERATE_LOG_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.OPERATE_LOG_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.OPERATE_LOG_KEY)
    })
    public void processor(String jsonMsg, @Header(AmqpHeaders.CONSUMER_QUEUE) String queue) {
        try{
            OperateLog operateLog = JSON.parseObject(jsonMsg,OperateLog.class);
            operateLogService.save(operateLog);
            log.info("-------日志记录保存成功--------");
        }catch(Exception e){
            log.error("jsonMsg="+jsonMsg);
            log.error(e.getMessage(),e);
        }
    }
}
