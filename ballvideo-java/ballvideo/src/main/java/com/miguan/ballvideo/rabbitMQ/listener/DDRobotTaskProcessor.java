package com.miguan.ballvideo.rabbitMQ.listener;

import com.alibaba.fastjson.JSONObject;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.DingtalkChatbotClient;
import com.miguan.ballvideo.vo.ClUserOpinionVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 向钉钉发送用户意见反馈消息信息
 *
 * @author laiyd
 * @date 2020-11-3
 */
@Slf4j
@EnableRabbit
@Component
public class DDRobotTaskProcessor {

    @Resource
    private DingtalkChatbotClient dingtalkChatbotClient;

    @RabbitListener(autoStartup = "#{environment['spring.rabbitmq.open']}", bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.DINGTALK_ROBOT_MSG_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.DINGTALK_ROBOT_MSG_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.DINGTALK_ROBOT_MSG_KEY)
    })
    public void processor(String jsonMsg) {
        try {
            ClUserOpinionVo opinionVo = JSONObject.parseObject(jsonMsg, ClUserOpinionVo.class);
            String result = dingtalkChatbotClient.sendMsg(opinionVo);
            if (StringUtils.isNotEmpty(result)) {
                log.info("向钉钉发送用户意见反馈消息信息：{}",result);
            }
        } catch (Exception e) {
            log.error(StringUtils.trimToEmpty(e.getMessage()).concat("钉钉意见反馈消息MQ消费异常,数据 ").concat(jsonMsg),e);
        }
    }
}
