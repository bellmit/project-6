package com.miguan.reportview.task;

import com.miguan.reportview.common.exception.NullParameterException;
import com.miguan.reportview.common.utils.DateUtil;
import com.miguan.reportview.service.IRealTimeStatisticsService;
import com.miguan.reportview.vo.ParamsBuilder;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ClickhouseHourTask {

    @Value("${spring.profiles.active}")
    private String activeProfiles;
    @Autowired
    private IRealTimeStatisticsService realTimeStatisticsService;


    private String preDate;

    public String getPreDate() {
        String time = StringUtils.isBlank(preDate) ? "上次执行时间为空，当前时间：".concat(DateUtil.currentTime()) : preDate;
        return time.concat(" 当前环境策略：").concat(activeProfiles)
                .concat(" ").concat(Boolean.toString(!StringUtils.trimToEmpty(activeProfiles).equals("prod")));
    }

    /**
     * rp_total_hour_accumulated 汇总数据每日小时累计统计
     */
    @Scheduled(cron = "0 56 * * * ?")
    public void hourStatistics() {
        log.info("汇总数据每日小时累计统计任务开始");
        if (!"prod".equals(activeProfiles) && !"localdev".equals(activeProfiles)) {
            log.info("无法开始实时统计汇总 当前开启的策略为 {}", activeProfiles);
            return;
        }
        log.info("开始实时统计汇总");
        doWork(LocalDateTime.now());

    }

    public void doWork(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new NullParameterException();
        }
        preDate = dateTime.format(DateUtil.YYYY_MM_DDBHHMMSS_FORMATTER);
        String timeBegin = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE).concat(" 00:00:00");
        String timeEnd = dateTime.format(DateUtil.YYYY_MM_DDTHH_FORMATTER).concat(":00:00");

        Integer timeBeginDh = Integer.valueOf(dateTime.format(DateUtil.YYYYMMDD_FORMATTER)+"00");
        Integer timeEndDh = Integer.valueOf(dateTime.format(DateUtil.YYYYMMDDHH_FORMATTER));
        Integer dt = Integer.valueOf(dateTime.format(DateUtil.YYYYMMDD_FORMATTER));
        log.info("每日小时累计统计,当前开始时间：{}  结束时间：{}", timeBegin, timeEnd);
        Map<String, Object> parmas = new HashMap<>();
        parmas.put("startDate", timeBegin);
        parmas.put("endDate", timeEnd);
        parmas.put("timeBeginDh", timeBeginDh);
        parmas.put("timeEndDh", timeEndDh);
        parmas.put("dt", dt);
        realTimeStatisticsService.staData(parmas);
        log.info("汇总数据每日小时累计统计任务结束,当前开始时间：{}  结束时间：{}", timeBegin, timeEnd);
    }

    /**
     * ld_rp_total_hour_accumulated 汇总数据每日小时累计统计
     */
    @Scheduled(cron = "0 55 * * * ?")
    public void hourLdStatistics() {
        log.info("汇总来电数据每日小时累计统计任务开始");
        if (!"prod".equals(activeProfiles) && !"localdev".equals(activeProfiles)) {
            log.info("无法开始实时统计汇总 当前开启的策略为 {}", activeProfiles);
            return;
        }
        log.info("开始实时统计来电汇总");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        String day = sdf.format(calendar.getTime()).substring(0,8);
        String startDhStr = day + "00";
        String endDhStr = sdf.format(calendar.getTime());
        realTimeStatisticsService.staLdData(Integer.parseInt(startDhStr), Integer.parseInt(endDhStr), Integer.parseInt(endDhStr));  //同步数据
        log.info("汇总来电数据每日小时累计统计任务结束,startDh:{},endDh:{},showDh:{}",startDhStr, endDhStr, endDhStr);
    }
}
