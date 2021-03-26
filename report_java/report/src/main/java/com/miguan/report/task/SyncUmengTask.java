package com.miguan.report.task;

import com.miguan.report.common.constant.CommonConstant;
import com.miguan.report.service.report.ShenceDataService;
import com.miguan.report.service.report.impl.UmengServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Description 每天同步友盟数据
 * @Author zhangbinglin
 * @Date 2020/6/30 15:58
 **/
@Slf4j
@Component
public class SyncUmengTask {

    @Resource
    private UmengServiceImpl umengService;

    /**
     * 每天从友盟获取数据
     */
    @Scheduled(cron = "${task.scheduled.cron.umeng-data}")
    public void syncUmengData() {
        log.info("从第三方平台获取友盟视频数据");
        Date yesterday = DateUtils.addDays(new Date(), -1);
        String yesterdayString = DateUtil.dateStr(yesterday, DateUtil.DATEFORMAT_STR_002);
        //新增umeng_data友盟数据
//        int count = umengService.countByDate(yesterdayString);
        umengService.saveUmengData(yesterdayString);   //同步友盟数据到umeng_data表
        log.info("从第三方平台获取友盟视频数据结束");
    }

    /**
     * 每天从友盟获取渠道数据
     */
    @Scheduled(cron = "${task.scheduled.cron.umeng-channel-data}")
    public void syncUmengChannelData() {
        log.info("从第三方平台获取友盟渠道数据");
        Date yesterday = DateUtils.addDays(new Date(), -1);
        String yesterdayString = DateUtil.dateStr(yesterday, DateUtil.DATEFORMAT_STR_002);
        umengService.saveUmengChannel(yesterdayString);  //同步友盟渠道数据到umeng_channel_data
        Date last2Day = DateUtils.addDays(new Date(), -2);
        String last2DayString = DateUtil.dateStr(last2Day, DateUtil.DATEFORMAT_STR_002);
        umengService.saveUmengChannel(last2DayString);  //同步友盟渠道数据到umeng_channel_data
        log.info("从第三方平台获取友盟渠道数据结束");
    }

    /**
     * 每天从友盟获取渠道数据
     */
    @Scheduled(cron = "${task.scheduled.cron.umeng-channel-data-min}")
    public void syncUmengMinChannelData() {
        log.info("每20min中从第三方平台获取友盟渠道数据");
        String todayString = DateUtil.dateStr(new Date(), DateUtil.DATEFORMAT_STR_002);
        umengService.saveUmengChannel(todayString);  //同步友盟渠道数据到umeng_channel_data
        log.info("每20min中从第三方平台获取友盟渠道数据结束");
    }

}
