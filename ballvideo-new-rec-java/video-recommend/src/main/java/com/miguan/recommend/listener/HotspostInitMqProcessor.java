package com.miguan.recommend.listener;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.bo.VideoInitDto;
import com.miguan.recommend.common.constants.RabbitMqConstants;
import com.miguan.recommend.service.es.EsVideoInitService;
import com.miguan.recommend.service.mongo.MongoVideoInitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 索引数据操作MQ
 *
 * @author shixh
 * @date 2020-09-01
 */
@Slf4j
@EnableRabbit
@Component
public class HotspostInitMqProcessor {

    @Resource
    private MongoVideoInitService mongoVideoInitService;
    @Resource
    private EsVideoInitService esVideoInitService;

    @RabbitListener(autoStartup = "#{environment['spring.rabbitmq.open']}", bindings = {
            @QueueBinding(value = @Queue(value = RabbitMqConstants.HOTSPOST_INIT_QUEUE),
                    exchange = @Exchange(value = RabbitMqConstants.HOTSPOST_INIT_EXCHANGE, autoDelete = "true"),
                    key = RabbitMqConstants.HOTSPOST_INIT_RUTE_KEY)
    })
    public void processor(String msg) {
        log.info("HotspostInitMqProcessor 监听到信息>>{}", msg);
        VideoInitDto dto = JSONObject.parseObject(msg, VideoInitDto.class);
        switch (dto.getType()) {
            case VideoInitDto.wash_incentive_video:
                mongoVideoInitService.doWashIncentiveVideo(dto);
                break;
            case VideoInitDto.init_video:
            case VideoInitDto.wash_hotpots_video:
                mongoVideoInitService.doWashHotspotVideo(dto);
                break;
            case VideoInitDto.video_paddle_tag_top5:
                mongoVideoInitService.doVideoTagTop5Ids(dto);
                break;
            case VideoInitDto.es_video_title_init:
                esVideoInitService.doVideoTitleInitToMQ(dto);
                break;
            default:
                log.warn("HotspostInitMqProcessor 未处理监听到的信息>>{}", msg);
        }
    }
}
