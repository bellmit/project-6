package com.miguan.laidian.rabbitMQ.listener;

import com.alibaba.fastjson.JSON;
import com.miguan.laidian.entity.LdBuryingPoint;
import com.miguan.laidian.entity.LdBuryingPointAdditional;
import com.miguan.laidian.entity.LdBuryingPointEvery;
import com.miguan.laidian.rabbitMQ.util.RabbitMQConstant;
import com.miguan.laidian.service.UserBuriedPointService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 异步处理埋点
 *
 * @author hyl
 * @date 2019年11月18日17:10:29
 */
@Slf4j
@EnableRabbit
@Component
public class BuryPointMqProcessor {

    @Resource
    UserBuriedPointService ldUserBuriedPointService;

    /**
     * 用户行为2数据埋点，参照文档：https://www.kdocs.cn/l/sYi9e9oZd?f=111（用户行为2数据埋点）
     *
     * @param jsonMsg the data
     * @param queue   the queue
     */
    @RabbitListener(autoStartup = "#{environment['spring.rabbitmq.open']}", bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.BURYPOINT_QUEUE + "." + RabbitMQConstant.TOPIC_USERBURIEDPOINT_MQ),
                    exchange = @Exchange(value = RabbitMQConstant.BURYPOINT_EXCHANGE + "." + RabbitMQConstant.TOPIC_USERBURIEDPOINT_MQ, autoDelete = "true"),
                    key = RabbitMQConstant.BURYPOINT_RUTE_KEY + "." + RabbitMQConstant.TOPIC_USERBURIEDPOINT_MQ)
    })
    public void processor(String jsonMsg, @Header(AmqpHeaders.CONSUMER_QUEUE) String queue) {
        log.info("从队列【{}】收到消息：{}", queue, jsonMsg);
        if (StringUtils.isNotBlank(jsonMsg)) {
            LdBuryingPoint ldBuryingPoint = JSON.parseObject(jsonMsg, LdBuryingPoint.class);
            ldUserBuriedPointService.userBuriedPointSecondEdition(ldBuryingPoint);
            //log.info("[消费rabbitmq消息队列处理成功]");
        } else {
            log.error("[消费rabbitmq消息队列数据失败.数据异常]");
        }
    }

    /**
     * 用户行为1数据埋点，参照文档：https://www.kdocs.cn/l/sYi9e9oZd?f=111（用户行为1数据埋点）
     *
     * @param jsonMsg the data
     * @param queue   the queue
     */
    @RabbitListener(autoStartup = "#{environment['spring.rabbitmq.open']}", bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.BURYPOINT_QUEUE + "." + RabbitMQConstant.TOPIC_USER_BURIED_POINT_ADDITIONAL_MQ),
                    exchange = @Exchange(value = RabbitMQConstant.BURYPOINT_EXCHANGE + "." + RabbitMQConstant.TOPIC_USER_BURIED_POINT_ADDITIONAL_MQ, autoDelete = "true"),
                    key = RabbitMQConstant.BURYPOINT_RUTE_KEY + "." + RabbitMQConstant.TOPIC_USER_BURIED_POINT_ADDITIONAL_MQ)
    })
    public void userBuriedAdditional(String jsonMsg, @Header(AmqpHeaders.CONSUMER_QUEUE) String queue) {
        //log.info("从队列【{}】收到消息：{}", queue, jsonMsg);
        if (StringUtils.isNotBlank(jsonMsg)) {
            try {
                LdBuryingPointAdditional ldBuryingPointAdditional = JSON.parseObject(jsonMsg, LdBuryingPointAdditional.class);
                ldUserBuriedPointService.ldBuryingPointAdditionalSecondEdition(ldBuryingPointAdditional);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            //log.info("[消费rabbitmq消息队列处理成功]");
        } else {
            log.error("[消费rabbitmq消息队列数据失败.数据异常]");
        }
    }


    /**
     * 广告位-数据埋点，来电秀分类-数据埋点，参照文档：https://www.kdocs.cn/l/sYi9e9oZd?f=111（广告位-数据埋点，来电秀分类-数据埋点）
     *
     * @param jsonMsg
     */
    @RabbitListener(autoStartup = "#{environment['spring.rabbitmq.open']}", bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.BURYPOINT_QUEUE + "." + RabbitMQConstant.TOPIC_LDBURYINGPOINTEVERY_MQ),
                    exchange = @Exchange(value = RabbitMQConstant.BURYPOINT_EXCHANGE + "." + RabbitMQConstant.TOPIC_LDBURYINGPOINTEVERY_MQ, autoDelete = "true"),
                    key = RabbitMQConstant.BURYPOINT_RUTE_KEY + "." + RabbitMQConstant.TOPIC_LDBURYINGPOINTEVERY_MQ)
    })
    public void userLdBuryingPointEvery(String jsonMsg, @Header(AmqpHeaders.CONSUMER_QUEUE) String queue) {
        //log.info("从队列【{}】收到消息：{}", queue, jsonMsg);
        if (StringUtils.isNotBlank(jsonMsg)) {
            try {
                LdBuryingPointEvery ldBuryingPointEvery = JSON.parseObject(jsonMsg, LdBuryingPointEvery.class);
                ldUserBuriedPointService.userBuriedPointEvery(ldBuryingPointEvery);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            //log.info("[消费rabbitmq消息队列处理成功]");
        } else {
            log.error("[消费rabbitmq消息队列数据失败.数据异常]");
        }
    }

}
