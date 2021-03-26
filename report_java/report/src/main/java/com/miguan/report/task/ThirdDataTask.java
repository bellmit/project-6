package com.miguan.report.task;

import com.miguan.report.common.constant.CommonConstant;
import com.miguan.report.dto.AdIdAndNameDto;
import com.miguan.report.entity.BannerRule;
import com.miguan.report.service.adv.AdvertCodeJoinService;
import com.miguan.report.service.report.BannerRuleService;
import com.miguan.report.service.sync.SyncDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * 第三方平台数据获取任务
 **/
@Slf4j
@Component
public class ThirdDataTask {

    @Resource
    private AdvertCodeJoinService advertCodeJoinService;
    @Resource
    private BannerRuleService bannerRuleService;
    @Resource
    private SyncDataService syncDataService;

    @Scheduled(cron = "${task.scheduled.cron.plat_form_third}")
    public void getDataFromThird() {
        log.info("从第三方广告平台获取数据任务开始");
        Date todayDate = new Date();
        Date yesterday = DateUtils.addDays(todayDate, -1);
        Map<String, AdIdAndNameDto> videoAdnameMap = advertCodeJoinService.queryAdIdAndName(CommonConstant.VIDEO_APP_TYPE);
        Map<String, AdIdAndNameDto> callAdnameMap = advertCodeJoinService.queryAdIdAndName(CommonConstant.CALL_APP_TYPE);
        Map<String, BannerRule> bannerRuleMap = bannerRuleService.findDistinctByTodal();
        //从快手获取数据
        try {
            syncDataService.getDataFromKs(yesterday, videoAdnameMap, callAdnameMap, bannerRuleMap);
        } catch (Exception e) {
            log.error("获取快手数据异常", e);
        }

        try {
            syncDataService.getDataFromGdt(yesterday, videoAdnameMap, callAdnameMap, bannerRuleMap);
        } catch (Exception e) {
            log.error("获取广点通数据异常", e);
        }

        log.info("从第三方广告平台获取数据任务结束");
    }
}
