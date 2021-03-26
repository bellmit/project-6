package com.miguan.bigdata.dto;

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

    public void send(){
        JSONObject msg = new JSONObject();
        msg.put("type", type);
        msg.put("businessId", UUIDUtils.getUUID());
        msg.put("params", params);
        log.debug("发送到MQ>>{}", msg.toJSONString());
        rabbitTemplate.convertAndSend(exchange, routingKey, msg.toJSONString());
        msg.clear();
    }

    @Override
    public void run() {
        this.send();
    }
}
