package com.miguan.bigdata.task;

import com.miguan.bigdata.service.AdDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Description 从大数据 同步数据到dsp的idea_advert_report表（存储计划的有效曝光数，有效点击数等指标）
 * @Author zhangbinglin
 * @Date 2020/11/6 10:40
 **/
@Slf4j
@Component
public class DspPlanTask {

    @Resource
    private AdDataService adDataService;

    /**
     * 实时同步idea_advert_report的数据
     */
    @Scheduled(cron = "${task.scheduled.cron.dspPlan}")
    public void realTimeSyncDspPlan() {
        log.info("实时同步idea_advert_report的数据（start）");
        Integer date = Integer.parseInt(DateUtil.dateStr7(new Date())); //获取当天日期
        adDataService.syncDspPlan(date, date);
        log.info("实时同步idea_advert_report的数据（end）");
    }

    /**
     * 每天凌晨，重新同步次idea_advert_report的数据
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void yesSyncDspPlan() {
        log.info("每天凌晨，重新同步次idea_advert_report的数据（start）");
        Date yesDate = DateUtil.rollDay(new Date(), -1);  //获取前一天日期
        Integer date = Integer.parseInt(DateUtil.dateStr7(yesDate));
        adDataService.syncDspPlan(date, date);
        log.info("每天凌晨，重新同步次idea_advert_report的数据（end）");
    }
}
