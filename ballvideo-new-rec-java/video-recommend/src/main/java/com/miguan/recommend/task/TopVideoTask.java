package com.miguan.recommend.task;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.common.constants.ExistConstants;
import com.miguan.recommend.common.constants.RedisRecommendConstants;
import com.miguan.recommend.common.constants.XyConstants;
import com.miguan.recommend.service.RedisService;
import com.miguan.recommend.service.ck.DwsVideoDayService;
import com.miguan.recommend.service.xy.VideosCatService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TopVideoTask {
    @Resource(name = "redisDB0Service")
    private RedisService redisDB0Service;
    @Resource
    private DwsVideoDayService dwsVideoDayService;
    @Resource
    private VideosCatService videosCatService;

    @PostConstruct
    public void initTopVideo() {
        String redisValue = redisDB0Service.get(RedisRecommendConstants.yesterday_top_video + "all");
        if (StringUtils.isEmpty(redisValue)) {
            Integer dt = this.getDt();
            this.setTopVideoToRedis(dt);
            this.setTopVideoToRedis4EveryCat(dt);
        }
    }

    /**
     * 每天的01:00:00执行
     */
    @Scheduled(cron = "0 55 0 * * ?")
    public void initTopVideoToday() {
        long incr = redisDB0Service.incr("initTopVideoToday", ExistConstants.five_minutes_seconds);
        if (incr == 1) {
            log.warn("=======================获取热门视频任务开始=======================");
            Integer dt = this.getDt();
            this.setTopVideoToRedis(dt);
            this.setTopVideoToRedis4EveryCat(dt);
            log.warn("=======================相似热门视频任务结束=======================");
        }
    }

    public Map<String, List<String>> setTopVideoToRedis4EveryCat(Integer date) {
        List<String> allCat = videosCatService.getAllCatIds(XyConstants.FIRST_VIDEO_CODE);
        Map<String, List<String>> allCatTopVideoMap = new HashMap<String, List<String>>(allCat.size());
        allCat.forEach(a -> {
            List<String> topVideos = dwsVideoDayService.findTopVideoWithCatId(date, 3000, Integer.parseInt(a));
            if(CollectionUtils.isEmpty(topVideos)){
                topVideos = new ArrayList<String>(0);
            }
            allCatTopVideoMap.put(a, topVideos);
        });

        allCatTopVideoMap.forEach((key,value) ->{
            redisDB0Service.set(RedisRecommendConstants.yesterday_top_video + key, JSONObject.toJSONString(value), ExistConstants.getTheRemainingSecondsOfTheHourForTomorrow(1));
        });

        return allCatTopVideoMap;
    }

    public List<String> setTopVideoToRedis(Integer date) {
        List<String> topVideos = dwsVideoDayService.findTopVideo(date, 3000);
        if (CollectionUtils.isEmpty(topVideos)) {
            topVideos = new ArrayList<String>(0);
        }
        redisDB0Service.set(RedisRecommendConstants.yesterday_top_video + "all", JSONObject.toJSONString(topVideos), ExistConstants.getTheRemainingSecondsOfTheHourForTomorrow(1));
        return topVideos;
    }

    private Integer getDt() {
        LocalDateTime localDateTime = LocalDateTime.now();
        String dtStr = localDateTime.minusDays(1L).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return Integer.parseInt(dtStr);
    }

}
