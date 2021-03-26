package com.miguan.ballvideo.task;

import com.miguan.ballvideo.mapper3.BudgetSmoothMapper;
import com.miguan.ballvideo.service.dsp.nadmin.BudgetSmoothService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;

/**
 * @Description 预算平滑，计算当天每个时间端预算等定时器
 * @Author zhangbinglin
 * @Date 2020/11/26 10:51
 **/
@Slf4j
@Component
public class BudgetSmoothTask {

    @Resource
    private BudgetSmoothService budgetSmoothService;
    @Resource
    private BudgetSmoothMapper budgetSmoothMapper;
    @Value("${spring.profiles.active}")
    private String activeProfiles;

    /**
     * 每天半点和整点重新计算预算平滑和参竞率，每个时间段的预算和参竞率
     */
    @Scheduled(cron = "0 0,30 * * * ?")
    public void budgetSmooth() {
        if (!"prod".equals(activeProfiles) && !"localdev".equals(activeProfiles)) {
            return;
        }
        String hourM = DateUtil.dateStr(new Date(), "HH:mm");
        if("00:00".equals(hourM)) {
            //每天0点初始化用账号信息表
            budgetSmoothMapper.initAdvertAcount(null);
            budgetSmoothMapper.initPlanSmoothDate(null);
        }
        log.info("预算平滑算法、参竞率，根据每个时间段的日活数，计算出每个时间段的预算和参竞率（start）");
        budgetSmoothService.budgetSmooth();
        log.info("预算平滑算法、参竞率，根据每个时间段的日活数，计算出每个时间段的预算和参竞率（end）");
    }
}
