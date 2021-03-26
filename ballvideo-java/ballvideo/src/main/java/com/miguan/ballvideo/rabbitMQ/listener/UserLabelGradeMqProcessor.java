package com.miguan.ballvideo.rabbitMQ.listener;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.UserLabelGradeService;
import com.miguan.ballvideo.vo.queue.UserLabelGradeQueueVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用户标签权重分更新
 * @author laiyd
 * @date 2020-01-09
 */
@Slf4j
@EnableRabbit
@Component
public class UserLabelGradeMqProcessor {

    @Resource
    private UserLabelGradeService userLabelGradeService;

    @RabbitListener(autoStartup="#{environment['spring.rabbitmq.open']}",bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.USERLABELGRADE_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.USERLABELGRADE_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.USERLABELGRADE_KEY)
    })
    public void processor(String jsonMsg, @Header(AmqpHeaders.CONSUMER_QUEUE) String queue) {
        UserLabelGradeQueueVo vo = JSON.parseObject(jsonMsg, UserLabelGradeQueueVo.class);
        userLabelGradeService.updateUserLabelGrade(vo.getDeviceId(), vo.getCatId(), vo.getActionId());
        //log.info("-------设备Id："+vo.getDeviceId()+",分类Id："+vo.getCatId()+",启动事件标识："+vo.getActionId()+";用户标签权重分更新完成--------");
    }
}
