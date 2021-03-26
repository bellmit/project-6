package com.miguan.report.task;

import com.miguan.report.common.constant.CommonConstant;
import com.miguan.report.service.report.ShenceDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Description 每天同步神策数据
 * @Author zhangbinglin
 * @Date 2020/6/30 15:58
 **/
@Slf4j
@Component
public class SyncShenceTask {

    @Resource
    private ShenceDataService shenceDataService;

    /**
     * 每天从神策获取视频的数据
     */
    @Scheduled(cron = "${task.scheduled.cron.shence-data-video}")
    public void getVideoShenceData() {
        log.info("从第三方平台获取神策视频数据");
        Date yesterday = DateUtils.addDays(new Date(), -1);
        String yesterdayString = DateUtil.dateStr(yesterday, DateUtil.DATEFORMAT_STR_002);

        shenceDataService.saveShenceData(yesterdayString, CommonConstant.VIDEO_APP_TYPE);
        log.info("从第三方平台获取神策视频数据结束");
    }

    /**
     * 每天从神策获取视频的数据
     */
    @Scheduled(cron = "${task.scheduled.cron.shence-data-call}")
    public void getLaidianShenceData() {
        log.info("从第三方平台获取神策来电数据");
        Date yesterday = DateUtils.addDays(new Date(), -1);
        String yesterdayString = DateUtil.dateStr(yesterday, DateUtil.DATEFORMAT_STR_002);

        shenceDataService.saveShenceData(yesterdayString, CommonConstant.CALL_APP_TYPE);
        log.info("从第三方平台获取神策来电数据结束");
    }
}
