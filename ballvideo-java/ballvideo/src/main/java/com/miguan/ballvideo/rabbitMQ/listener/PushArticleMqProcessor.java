package com.miguan.ballvideo.rabbitMQ.listener;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.PushSevice;
import com.miguan.ballvideo.vo.push.PushResultCountVo;
import com.vivo.push.sdk.notofication.InvalidUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 消息推送
 * @author laiyd
 * @date 2020-10-22
 */
@Slf4j
@EnableRabbit
@Component
public class PushArticleMqProcessor {

    @Resource
    private PushSevice pushSevice;

    @RabbitListener(autoStartup="#{environment['spring.rabbitmq.open']}",bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.PUSH_VIVO_ERROR_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.PUSH_VIVO_ERROR_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.PUSH_VIVO_ERROR_KEY)
    })
    public void vivoInvalidUserSave(String jsonMsg) {
        try {
            List<InvalidUser> invalidUserList = JSON.parseArray(jsonMsg,InvalidUser.class);
            pushSevice.vivoInvalidUserSave(invalidUserList);
        } catch (Exception e) {
            log.error(StringUtils.trimToEmpty(e.getMessage()).concat("保存vivo无效token信息MQ消费异常,数据 ").concat(jsonMsg),e);
        }
    }

    @RabbitListener(bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.PUSH_COUNT_INFO_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.PUSH_COUNT_INFO_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.PUSH_COUNT_INFO_KEY)
    })
    public void savePushCountInfo(String jsonMsg) {
        try {
            PushResultCountVo countVo = JSON.parseObject(jsonMsg,PushResultCountVo.class);
            pushSevice.savePushCountInfo(countVo);
        } catch (Exception e) {
            log.error(StringUtils.trimToEmpty(e.getMessage()).concat("保存消息推送总数信息MQ消费异常,数据 ").concat(jsonMsg),e);
        }
    }
}
