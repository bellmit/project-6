package com.miguan.reportview.task;

import com.google.common.collect.Lists;
import com.miguan.reportview.entity.RpTotalDay;
import com.miguan.reportview.entity.RpTotalHour;
import com.miguan.reportview.mapper.SyncRpTotalDataMapper;
import com.miguan.reportview.service.SyncRpTotalDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.*;

/**
 * 定时同步当天的rp_total_day和rp_total_hour信息
 * @author zhangbinglin
 * @date 2020-08-07 
 *
 */
@Component
@Slf4j
public class RpTotalDataTask {
    @Resource
    private SyncRpTotalDataMapper syncRpTotalDataMapper;
    @Resource
    private SyncRpTotalDataService syncRpTotalDataService;

    /**
     * 实时统计rp_total_day和rp_total_hour和rp_total_minute的数据
     */
    @Scheduled(cron = "${task.scheduled.cron.sync-rp-total-data}")
    public void syncRpTotalData() {
        log.info("实时统计rp_total_day和rp_total_hour和rp_total_minute的数据");
        Calendar calendar = Calendar.getInstance();
        String nowDay = DateUtil.dateStr2(calendar.getTime());  //当前日期
        int nowDh = Integer.parseInt(DateUtil.dateStr(calendar.getTime(), "yyyyMMddHH")); //当前小时

        //按天统计
        syncRpTotalDataService.syncRpTotalDay(nowDay, nowDay);
        //按小时统计
        syncRpTotalDataService.syncRpTotalHour(nowDh, nowDh);

        //按分钟统计
        Date now = new Date();
        long endDm = Long.parseLong(DateUtil.dateStr(DateUtil.rollMinute(now, -1), "yyyyMMddHHmm")); //减1分钟
        Long startDm = syncRpTotalDataMapper.maxDmTotalMinute();  //查询出数据库中最后一条记录的分钟
        if(startDm == null) {
            startDm = Long.parseLong(DateUtil.dateStr(DateUtil.rollMinute(now, -5), "yyyyMMddHHmm"));  //减5分钟
        } else {
            Date startDate = DateUtil.valueOf(String.valueOf(startDm), "yyyyMMddHHmm");
            startDate = DateUtil.rollMinute(startDate, 1);  //加1分钟
            startDm = Long.parseLong(DateUtil.dateStr(startDate, "yyyyMMddHHmm"));
        }
        syncRpTotalDataService.syncRpTotalMinute(startDm, endDm);
        log.info("实时统计rp_total_day和rp_total_hour和rp_total_minute的数据(end)");
    }

    /**
     * 每天凌晨重新汇总昨天的rp_total_day数据
     */
    @Scheduled(cron = "0 1 0 * * ?")
    public void syncLastRpTotalDayData() {
        log.info("每天凌晨重新汇总昨天的rp_total_day数据");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        String yesDay = DateUtil.dateStr2(calendar.getTime());  //前一天日期
        syncRpTotalDataService.syncRpTotalDay(yesDay, yesDay);
        log.info("每天凌晨重新汇总昨天的rp_total_day数据(end)");
    }

    /**
     * 每小时1分，重新汇总上个小时的数据rp_total_hour
     */
    @Scheduled(cron = "0 1 * * * ?")
    public void syncLastRpTotalHourData() {
        log.info("每小时1分，重新汇总上个小时的数据rp_total_hour");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        String yesDay = DateUtil.dateStr2(calendar.getTime());  //前一天日期
        syncRpTotalDataService.syncRpTotalDay(yesDay, yesDay);
        log.info("每小时1分，重新汇总上个小时的数据rp_total_hour(end)");
    }

    /**
     * 实时统计ld_rp_total_day和ld_rp_total_hour和ld_rp_total_minute的数据
     */
    @Scheduled(cron = "${task.scheduled.cron.sync-ldrp-total-data}")
    public void syncLdRpTotalData() {
        log.info("实时统计ld_rp_total_day和ld_rp_total_hour和ld_rp_total_minute的数据start");
        Calendar calendar = Calendar.getInstance();
        String nowDay = DateUtil.dateStr2(calendar.getTime());  //当前日期
        int nowDh = Integer.parseInt(DateUtil.dateStr(calendar.getTime(), "yyyyMMddHH")); //当前小时

        //按天统计
        syncRpTotalDataService.syncLdRpTotalDay(nowDay, nowDay);
        //按小时统计
        syncRpTotalDataService.syncLdRpTotalHour(nowDh, nowDh);

        //按分钟统计
        Date now = new Date();
        long endDm = Long.parseLong(DateUtil.dateStr(DateUtil.rollMinute(now, -1), "yyyyMMddHHmm")); //减1分钟
        Long startDm = syncRpTotalDataMapper.maxDmLdTotalMinute();  //查询出数据库中最后一条记录的分钟
        if(startDm == null) {
            startDm = Long.parseLong(DateUtil.dateStr(DateUtil.rollMinute(now, -5), "yyyyMMddHHmm"));  //减5分钟
        } else {
            Date startDate = DateUtil.valueOf(String.valueOf(startDm), "yyyyMMddHHmm");
            startDate = DateUtil.rollMinute(startDate, 1);  //加1分钟
            startDm = Long.parseLong(DateUtil.dateStr(startDate, "yyyyMMddHHmm"));
        }
        syncRpTotalDataService.syncLdRpTotalMinute(startDm, endDm);
        log.info("实时统计ld_rp_total_day和ld_rp_total_hour和ld_rp_total_minute的数据start(end)");
    }

    /**
     * 每天凌晨重新汇总昨天的ld_rp_total_day数据
     */
    @Scheduled(cron = "0 1 0 * * ?")
    public void syncLastLdRpTotalDayData() {
        log.info("每天凌晨重新汇总昨天的ld_rp_total_day数据");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        String yesDay = DateUtil.dateStr2(calendar.getTime());  //前一天日期
        syncRpTotalDataService.syncLdRpTotalDay(yesDay, yesDay);
        log.info("每天凌晨重新汇总昨天的ld_rp_total_day数据(end)");
    }

    /**
     * 每小时1分，重新汇总上个小时的数据ld_rp_total_hour
     */
    @Scheduled(cron = "0 1 * * * ?")
    public void syncLastLdRpTotalHourData() {
        log.info("每小时1分，重新汇总上个小时的数据ld_rp_total_hour");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        int lastHour = Integer.parseInt(DateUtil.dateStr(calendar.getTime(), "yyyyMMddHH"));  //上一个小时
        syncRpTotalDataService.syncLdRpTotalHour(lastHour, lastHour);
        log.info("每小时1分，重新汇总上个小时的数据ld_rp_total_hour(end)");
    }
}
