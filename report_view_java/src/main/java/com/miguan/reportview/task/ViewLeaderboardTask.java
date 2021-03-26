package com.miguan.reportview.task;

import com.alibaba.fastjson.JSONObject;
import com.miguan.reportview.common.utils.DateUtil;
import com.miguan.reportview.service.IVideoViewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

@Slf4j
@Component
public class ViewLeaderboardTask {

    @Resource
    private IVideoViewService videoViewService;

  //  private static ExecutorService excutorService = newFixedThreadPool(20);

    /**
     * 统计近24小时视频播放数
     */
   /* @Scheduled(cron = "${task.scheduled.cron.statistics-day-view}")
    public void statisticsDayView() {
        log.warn("统计近24小时视频播放数开始");

        Map<String, Object> query = new HashMap<String, Object>();
        query.put("dd", DateUtil.yedyyyy_MM_dd());

        // 统计近24小时视频播放记录数
        int count = videoViewService.countData(query);
        log.info("统计近24小时视频播放记录数>>{}", count);
        int startRow = 0;
        int pageSize = 5000;
        while (startRow < count) {

            Map<String, Object> listParam = new HashMap<String, Object>();
            listParam.putAll(query);
            listParam.put("startRow", startRow);
            listParam.put("pageSize", pageSize);
            excutorService.execute(() -> {
                List<Map<String, Object>> dataList = videoViewService.selectVideoViewData(listParam);
                log.info("dataList>>{}", JSONObject.toJSONString(dataList));
                dataList.forEach(t -> {
                    t.put("dd", Integer.parseInt(DateUtil.yesyyyyMMdd()));
                    t.put("dh", 0);
                    t.put("type", 2);
                });
                log.info("开始批量插入统计结果");
                videoViewService.insertBatch(dataList);
            });
            startRow = startRow + pageSize;
        }

        // 删除前48小时前的视频播放数
        LocalDate localDate = LocalDate.now();
        String deleteDay = localDate.minusDays(2).format(DateTimeFormatter.BASIC_ISO_DATE);

        Map<String, Object> deleteQuery = new HashMap<String, Object>();
        deleteQuery.put("dd", Integer.parseInt(deleteDay));
        deleteQuery.put("type", 2);
        videoViewService.deleteData(deleteQuery);
        log.warn("统计近24小时视频播放数结束");
    }


    *//**
     * 统计近1小时视频播放数
     *//*
    @Scheduled(cron = "${task.scheduled.cron.statistics-hour-view}")
    public void statisticsHourView() {
        log.warn("统计近1小时视频播放数开始");
        LocalDateTime localDateTime = LocalDateTime.now();
        int day = Integer.parseInt(localDateTime.minusHours(1).format(DateUtil.YYYYMMDD_FORMATTER));
        int dataHour = Integer.parseInt(localDateTime.minusHours(1).format(DateUtil.YYYYMMDDHH_FORMATTER));

        Map<String, Object> query = new HashMap<String, Object>();
        query.put("dh", dataHour);

        // 统计前1小时视频播放记录数
        int count = videoViewService.countData(query);
        log.warn("统计前1小时视频播放记录数>>{}", count);
        int startRow = 0;
        int pageSize = 1000;
        while (startRow < count) {

            Map<String, Object> listParam = new HashMap<String, Object>();
            listParam.putAll(query);
            listParam.put("startRow", startRow);
            listParam.put("pageSize", pageSize);
            excutorService.execute(() -> {
                List<Map<String, Object>> dataList = videoViewService.selectVideoViewData(listParam);
                dataList.forEach(t -> {
                    t.put("dd", day);
                    t.put("dh", dataHour);
                    t.put("type", 1);
                });
                log.warn("开始批量插入统计结果");
                videoViewService.insertBatch(dataList);
            });
            startRow = startRow + pageSize;
        }


        // 删除前3小时前的视频播放数
        Map<String, Object> deleteQuery = new HashMap<String, Object>();
        deleteQuery.put("dh", Integer.parseInt(localDateTime.minusHours(3).format(DateUtil.YYYYMMDDHH_FORMATTER)));
        deleteQuery.put("type", 1);
        videoViewService.deleteData(deleteQuery);
        log.warn("统计近1小时视频播放数结束");
    }*/
}
