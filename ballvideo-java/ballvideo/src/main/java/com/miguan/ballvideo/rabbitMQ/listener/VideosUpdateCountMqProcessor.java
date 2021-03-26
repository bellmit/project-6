package com.miguan.ballvideo.rabbitMQ.listener;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.FirstVideosOldService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 初始化视频评论信息
 * @author laiyd
 * @date 2019-12-23
 */
@Slf4j
@EnableRabbit
@Component
public class VideosUpdateCountMqProcessor {

    @Resource
    private FirstVideosOldService firstVideosOldService;

    @RabbitListener(autoStartup="#{environment['spring.rabbitmq.open']}",bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.VIDEO_UPDATECOUNT_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.VIDEO_UPDATECOUNT_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.VIDEO_UPDATECOUNT_KEY)
    })
    public void processor(String jsonMsg) {
        try {
            Map<String, Object> params = JSON.parseObject(jsonMsg,Map.class);
            firstVideosOldService.updateVideosCount(params);
            log.info("-------视频相关统计信息更新完成--------");
        } catch (Exception e) {
            log.error(StringUtils.trimToEmpty(e.getMessage()).concat(" 视频相关统计信息MQ消费异常,数据 ").concat(jsonMsg),e);
        }
    }
}
