package com.miguan.recommend.listener;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.common.util.StringUtils;
import com.miguan.recommend.service.es.EsVideoInitService;
import com.miguan.recommend.service.mongo.MongoVideoInitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class VideoTagListener {

    @Resource
    private MongoVideoInitService mongoVideoInitService;

    @KafkaListener(topics = {"video_tag"})
    public void videoTag(ConsumerRecord<String, String> record) {
        String value = record.value();
        if(value == null || value.length() == 0){
            return;
        }
        log.info("监听到视频标签>>{}", value);
        JSONObject jsonObject = JSONObject.parseObject(value);
        mongoVideoInitService.updateVideoTagTop5Ids(jsonObject.getIntValue("video_id"));
    }

}
