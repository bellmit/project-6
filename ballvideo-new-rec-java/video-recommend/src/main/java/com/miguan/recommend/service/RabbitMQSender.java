package com.miguan.recommend.service;

import com.alibaba.fastjson.JSONObject;
import com.cgcg.context.util.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j
public class RabbitMQSender implements Runnable {

    private String type;
    private JSONObject params;
    private RabbitTemplate rabbitTemplate;
    private String exchange;
    private String routingKey;

    public RabbitMQSender(String type, JSONObject params, RabbitTemplate rabbitTemplate, String exchange, String routingKey) {
        this.type = type;
        this.params = params;
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    @Override
    public void run() {
        JSONObject msg = new JSONObject();
        msg.put("type", type);
        msg.put("businessId", UUIDUtils.getUUID());
        msg.put("params", params);
        log.debug("NPushMQ 发送到MQ>>{}", msg.toJSONString());
        rabbitTemplate.convertAndSend(exchange, routingKey, msg.toJSONString());
    }
}
