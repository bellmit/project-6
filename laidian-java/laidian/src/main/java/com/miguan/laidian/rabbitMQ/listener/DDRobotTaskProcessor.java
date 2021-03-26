package com.miguan.laidian.rabbitMQ.listener;

import com.alibaba.fastjson.JSONObject;
import com.miguan.laidian.common.dingtalk.SendResult;
import com.miguan.laidian.rabbitMQ.util.RabbitMQConstant;
import com.miguan.laidian.service.DingtalkChatbotClient;
import com.miguan.laidian.vo.ClUserOpinionVo;
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
            SendResult result = dingtalkChatbotClient.send(opinionVo);
            if (StringUtils.isNotEmpty(result.getErrorMsg())) {
                log.info("向钉钉发送用户意见反馈消息信息：{}",result.getErrorMsg());
            }
        } catch (Exception e) {
            log.error(StringUtils.trimToEmpty(e.getMessage()).concat("钉钉意见反馈消息MQ消费异常,数据 ").concat(jsonMsg),e);
        }
    }
}
