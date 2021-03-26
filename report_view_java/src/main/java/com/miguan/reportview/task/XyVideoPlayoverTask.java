package com.miguan.reportview.task;

import com.miguan.reportview.service.IXyBuryingPointService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Slf4j
@Component
public class XyVideoPlayoverTask {

    @Resource
    private IXyBuryingPointService xyBuryingPointService;
    @Value("${spring.profiles.active}")
    private String activeProfiles;
    /**
     * 每10秒钟，将mongodb中关于xy_video_playover的埋点数据，批量导入到clickhouse表xy_video_playover
     */
    //@Scheduled(cron = "0,30 * * * * ?")
    public void copyXyVideoPlayoverDataToClickhouseFromMongo(){
        if (!"prod".equals(activeProfiles) && !"localdev".equals(activeProfiles)) {
            log.info("xy_video_playover埋点数据迁移到clickhouse任务开始 当前开启的策略为 {}", activeProfiles);
            return;
        }
        log.info("mongodb xy_video_playover埋点数据迁移到clickhouse任务开始");
        xyBuryingPointService.copyToClickHouseFromMongo(LocalDateTime.now());
        log.info("mongodb xy_video_playover埋点数据迁移到clickhouse任务结束");
    }
}
