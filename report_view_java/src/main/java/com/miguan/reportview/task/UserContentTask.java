package com.miguan.reportview.task;

import com.miguan.reportview.service.SyncUserContentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;

/**
 * 定时同步用户内容信息user_content_operation
 * @author zhangbinglin
 *
 */
@Component
@Slf4j
public class UserContentTask {
    @Resource
    private SyncUserContentService syncUserContentService;
    @Value("${spring.profiles.active}")
    private String activeProfiles;

    /**
     * 同步当天实时的用户运营数据
     */
    @Scheduled(cron = "${task.scheduled.cron.user-content-operation}")
    public void SyncUserContentRealData() {
        log.info("同步当天实时的用户运营数据 当前开启的策略为 {}", activeProfiles);
        if (!"prod".equals(activeProfiles) && !"localdev".equals(activeProfiles)) {
            return;
        }

        String date = DateUtil.dateStr2(new Date());
        log.info("同步当天实时的用户运营数据(start,{})", date);
        syncUserContentService.syncUserContent(date, date);
        log.info("同步当天实时的用户运营数据(end,{})", date);
    }

    /**
     * 每天凌晨重新同步一次昨天的数据
     */
    @Scheduled(cron = "0 10 0 * * ?")
    public void SyncUserContentYesData() {
        log.info("每天凌晨重新同步一次昨天的用户内容数据任务开始 当前开启的策略为 {}", activeProfiles);
        if (!"prod".equals(activeProfiles) && !"localdev".equals(activeProfiles)) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        String date = DateUtil.dateStr2(calendar.getTime());
        log.info("每天凌晨重新同步一次昨天的用户内容数据(start,{})", date);
        syncUserContentService.syncUserContent(date, date);
        log.info("每天凌晨重新同步一次昨天的用户内容数据(end,{})", date);
    }

    /**
     * 同步当天实时的来电用户运营数据
     */
    @Scheduled(cron = "${task.scheduled.cron.user-ldcontent-operation}")
    public void SyncLdUserContentRealData() {
        log.info("同步当天实时的来电用户运营数据任务开始 当前开启的策略为 {}", activeProfiles);
        if (!"prod".equals(activeProfiles) && !"localdev".equals(activeProfiles)) {
            return;
        }

        String date = DateUtil.dateStr2(new Date());
        log.info("同步当天实时的来电用户运营数据(start,{})", date);
        syncUserContentService.syncLdUserContent(date);
        log.info("同步当天实时的来电用户运营数据(end,{})", date);
    }

    /**
     * 每天凌晨重新同步一次昨天来电的数据
     */
    @Scheduled(cron = "0 10 0 * * ?")
    public void SyncLdUserContentYesData() {
        log.info("每天凌晨重新同步一次昨天的来电用户内容数据任务开始 当前开启的策略为 {}", activeProfiles);
        if (!"prod".equals(activeProfiles) && !"localdev".equals(activeProfiles)) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        String date = DateUtil.dateStr2(calendar.getTime());
        log.info("每天凌晨重新同步一次昨天的来电用户内容数据(start,{})", date);
        syncUserContentService.syncLdUserContent(date);
        log.info("每天凌晨重新同步一次昨天的来电用户内容数据(end,{})", date);
    }
}
