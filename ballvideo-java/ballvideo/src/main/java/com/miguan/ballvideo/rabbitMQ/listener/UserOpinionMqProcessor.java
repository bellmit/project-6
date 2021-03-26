package com.miguan.ballvideo.rabbitMQ.listener;


import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.entity.PushArticle;
import com.miguan.ballvideo.entity.UserLabel;
import com.miguan.ballvideo.mapper.ClUserOpinionMapper;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.AdErrorService;
import com.miguan.ballvideo.service.ClUserOpinionService;
import com.miguan.ballvideo.service.UserLabelService;
import com.miguan.ballvideo.vo.ClUserOpinionVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用户新增系统通知（来自消息推送）
 * @author daoyu
 * @date 2020年6月04日17:18:50
 */
@Slf4j
@EnableRabbit
@Component
public class UserOpinionMqProcessor {

    @Resource
    ClUserOpinionService clUserOpinionService;
    @Resource
    private ClUserOpinionMapper clUserOpinionMapper;

    @RabbitListener(autoStartup="#{environment['spring.rabbitmq.open']}",bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.UserOption_SAVE_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.UserOption_SAVE_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.UserOption_SAVE_KEY)
    })
    public void processor(String jsonMsg, @Header(AmqpHeaders.CONSUMER_QUEUE) String queue) {
        ClUserOpinionVo vo = JSON.parseObject(jsonMsg,ClUserOpinionVo.class);
        if (vo!=null){
            clUserOpinionMapper.saveClUserOpinionPlus(vo);
        }

    }
}
