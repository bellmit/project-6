package com.miguan.xuanyuan.task;

import com.cgcg.context.util.SpringTaskUtil;
import com.miguan.xuanyuan.common.constant.RedisKeyConstant;
import com.miguan.xuanyuan.service.AbExpService;
import com.miguan.xuanyuan.service.common.RedisService;
import com.miguan.xuanyuan.service.third.ThirdPlatApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * 自动导入第三方的广告数据
 */
@Slf4j
@Component
public class ThirdPlatDataTask {

    @Resource
    private ThirdPlatApiService thirdPlatApiService;

    //调用第三方api接口，导入第三方广告数据
    @Scheduled(cron = "${task.scheduled.cron.syncThirdPlatData}")
    public void taskThirdPlatAdvData() {
        log.info("调用第三方api接口，导入第三方广告数据");
        String date = DateUtil.dateStr2(DateUtil.rollDay(new Date(),-1)); //昨天日期
        thirdPlatApiService.syncThirdPlatAdvData(date, false);
    }

    //按天保存广告配置信息
    @Scheduled(cron = "0 1 0 * * ?")
    public void taskAdConfigLog() {
        log.info("按天保存广告配置信息");
        String date = DateUtil.dateStr2(DateUtil.rollDay(new Date(),-1)); //昨天日期
        thirdPlatApiService.syncAdConfigLog(date);
    }

}
