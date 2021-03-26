package com.miguan.ballvideo.rabbitMQ.listener;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.UserCatPoolService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 用户视频分类兴趣池
 * @author laiyd
 * @date 2020-08-26
 */
@Slf4j
@EnableRabbit
@Component
public class VideosClickCatPoolMqProcessor {

    @Resource
    private UserCatPoolService userCatPoolService;

    @RabbitListener(autoStartup="#{environment['spring.rabbitmq.open']}",bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.VIDEO_CLICK_CAT_POOL_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.VIDEO_CLICK_CAT_POOL_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.VIDEO_CLICK_CAT_POOL_KEY)
    })
    public void processor(String jsonMsg) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("消费MQ信息 ==> {}", jsonMsg);
            }
            Map<String, Object> params = JSON.parseObject(jsonMsg,Map.class);
            userCatPoolService.addRedisVideosCatInfo(params);
        } catch (Exception e) {
            log.error(StringUtils.trimToEmpty(e.getMessage()).concat("更新用户视频分类兴趣池MQ消费异常,数据 ").concat(jsonMsg),e);
        }
    }
}
