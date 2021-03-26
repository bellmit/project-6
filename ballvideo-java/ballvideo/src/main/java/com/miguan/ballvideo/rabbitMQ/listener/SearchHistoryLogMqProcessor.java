package com.miguan.ballvideo.rabbitMQ.listener;


import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.VideoEsService;
import com.miguan.ballvideo.vo.SearchHistoryLogVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 搜索历史记录
 * @author layd
 * @date 20201021
 */
@Slf4j
@EnableRabbit
@Component
public class SearchHistoryLogMqProcessor {
    @Resource
    private VideoEsService videoEsService;

    @RabbitListener(autoStartup="#{environment['spring.rabbitmq.open']}",bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.SEARCH_HISTORY_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.SEARCH_HISTORY_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.SEARCH_HISTORY_KEY)
    })
    public void processor(String jsonMsg) {
        SearchHistoryLogVo vo = JSON.parseObject(jsonMsg,SearchHistoryLogVo.class);
        if (vo!=null){
            try {
                videoEsService.saveSearchInfo(vo);
            } catch (Exception e) {
                log.error(StringUtils.trimToEmpty(e.getMessage()).concat("保存搜索历史记录消费异常,数据 ").concat(jsonMsg),e);
            }
        }

    }
}
