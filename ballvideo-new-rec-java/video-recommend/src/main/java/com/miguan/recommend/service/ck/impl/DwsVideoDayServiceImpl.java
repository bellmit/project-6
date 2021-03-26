package com.miguan.recommend.service.ck.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.recommend.entity.ck.DwsVideoDay;
import com.miguan.recommend.mapper.DwsVideoDayMapper;
import com.miguan.recommend.service.RedisService;
import com.miguan.recommend.service.ck.DwsVideoDayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@DS("clickhouse-dw")
@Service
public class DwsVideoDayServiceImpl implements DwsVideoDayService {

    @Resource
    private DwsVideoDayMapper dwsVideoDayMapper;
    @Resource(name = "redisDB0Service")
    private RedisService redisDB0Service;

    @Override
    public List<String> findTopVideo(Integer dt, Integer lowestShow) {
        List<Map<String, Object>> videoDayMapList = dwsVideoDayMapper.findTopVideoInOneDay(dt, lowestShow);
        if (isEmpty(videoDayMapList)) {
            log.info("热门视频查询结果为空");
            return null;
        } else {
            log.info("热门视频查询结果：{}", JSONObject.toJSONString(videoDayMapList));
        }
        return this.topVideoSort(videoDayMapList);
    }

    @Override
    public List<String> findTopVideoWithCatId(Integer dt, Integer lowestShow, Integer catId) {
        List<Map<String, Object>> videoDayMapList = dwsVideoDayMapper.findTopVideoWithCatId(dt, lowestShow, catId);
        if (isEmpty(videoDayMapList)) {
            log.info("分类热门视频查询结果为空");
            return null;
        } else {
            log.info("分类热门视频查询结果：{}", JSONObject.toJSONString(videoDayMapList));
        }
        return this.topVideoSort(videoDayMapList);
    }

    private List<String> topVideoSort(List<Map<String, Object>> videoDayMapList){
        List<DwsVideoDay> videoDayList = new ArrayList<DwsVideoDay>(videoDayMapList.size());
        videoDayMapList.forEach(e ->{
            DwsVideoDay videoDay = new DwsVideoDay();
            videoDay.setVideoId(Long.parseLong(e.get("video_id").toString()));
            videoDay.setShow_count(Long.parseLong(e.get("show_count").toString()));
            videoDay.setPlay_count(Long.parseLong(e.get("play_count").toString()));
            videoDay.setPlay_rate(new BigDecimal(e.get("play_rate").toString()).doubleValue());
            log.info("热门视频:{}", JSONObject.toJSONString(e));
            videoDayList.add(videoDay);
        });

        List<String> topVideo = new ArrayList<String>();
        // 曝光5000以上的按照播放率排序
        List<String> part1Video = videoDayList.stream().filter(e -> {
            log.info("videoId:{}, show_count:{}", e.getVideoId(), e.getShow_count());
            return e.getShow_count() >= 5000;
        }).sorted((e1, e2) -> {
            return e2.getPlay_rate().compareTo(e1.getPlay_rate());
        }).map(DwsVideoDay::getVideoId).map(String::valueOf).collect(Collectors.toList());

        // 曝光5000以下的按照播放数排序
        List<String> part2Video = videoDayList.stream().filter(e -> {
            log.info("videoId:{}, show_count:{}", e.getVideoId(), e.getShow_count());
            return e.getShow_count() < 5000;
        }).sorted((e1, e2) -> {
            return e2.getPlay_count().compareTo(e1.getPlay_count());
        }).map(DwsVideoDay::getVideoId).map(String::valueOf).collect(Collectors.toList());

        if (!isEmpty(part1Video)) {
            topVideo.addAll(part1Video);
        }
        if (!isEmpty(part2Video)) {
            topVideo.addAll(part2Video);
        }

        return topVideo;
    }


}
