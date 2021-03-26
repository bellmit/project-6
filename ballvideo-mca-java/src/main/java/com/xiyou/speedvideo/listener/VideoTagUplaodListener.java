package com.xiyou.speedvideo.listener;

import com.alibaba.fastjson.JSONObject;
import com.xiyou.speedvideo.service.FirstVideoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description 算法解析的视频关键字标签 上报给中台标签库
 * @Author zhangbinglin
 * @Date 2021/2/1 13:59
 **/
@Slf4j
@Component
public class VideoTagUplaodListener {

    @Resource
    private FirstVideoService firstVideoService;

    /**
     * 算法通过双塔向量解析成功视频标签后，把标签上报给中台标签库
     * @param record
     */
    @KafkaListener(topics = {"video_tag"}, groupId="zt_bq")
    public void onMessage1(ConsumerRecord<String, String> record){
        // 消费的哪个topic、partition的消息,打印出消息内容
        log.info("VideoTagUplaodListener 监听到消息>>{}", record.value());
        String value = record.value();
        JSONObject json = JSONObject.parseObject(value);
        Integer videoId = json.getInteger("video_id");
        String fullLabel = json.getString("full_label");
        firstVideoService.labelUpLoad(videoId, null, fullLabel, 2);  //调用视频标签上报接口
    }
}
