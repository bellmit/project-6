package com.miguan.ballvideo.rabbitMQ.listener;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.UserLabelService;
import com.miguan.ballvideo.vo.queue.UserLabelQueueVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户标签初始化
 * @author shixh
 * @date 2019-12-11
 */
@Slf4j
@EnableRabbit
@Component
public class UserLabelMqProcessor {

    @Resource
    private UserLabelService userLabelService;

    @RabbitListener(autoStartup="#{environment['spring.rabbitmq.open']}",bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.UserLabel_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.UserLabel_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.UserLabel_KEY)
    })
    public void initUserLabel(String jsonMsg, @Header(AmqpHeaders.CONSUMER_QUEUE) String queue) {
        UserLabelQueueVo vo = JSON.parseObject(jsonMsg,UserLabelQueueVo.class);
        if (containsChineseWords(vo.getAppVersion())){
            vo.setAppVersion("1.6.0");
            log.info("-------版本號包含中文(appVersion="+vo.getAppVersion()+")--------");
        }
        userLabelService.initUserLabel(vo.getDeviceId(), vo.getChannelId(), vo.getAppPackage());
    }

    public static boolean containsChineseWords(String name) {
        Pattern pattern = Pattern.compile("^.*([\u4E00-\uFA29]|[\uE7C7-\uE7F3])+.*$");
        Matcher matcher = pattern.matcher(name);
        return matcher.find();
    }

    @RabbitListener(bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.UserLabel_DELETE_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.UserLabel_DELETE_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.UserLabel_DELETE_KEY)
    })
    public void deleteUserLabel(String jsonMsg, @Header(AmqpHeaders.CONSUMER_QUEUE) String queue) {
        String [] params = jsonMsg.split(RabbitMQConstant._MQ_);
        int loop = Integer.parseInt(params[0]);
        String tableName1 = params[1];
        String tableName2 = params[2];
        userLabelService.deleteUserLabel(loop,tableName1,tableName2);
    }
}
