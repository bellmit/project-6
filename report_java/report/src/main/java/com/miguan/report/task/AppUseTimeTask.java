package com.miguan.report.task;

import com.alibaba.fastjson.JSONObject;
import com.miguan.report.common.enums.AppUseTimeEnum;
import com.miguan.report.common.enums.UmengAppKeyEnum;
import com.miguan.report.common.util.DateTimeUtils;
import com.miguan.report.entity.report.AppUseTime;
import com.miguan.report.mapper.AppUseTimeMapper;
import com.miguan.report.repository.AppUseTimeRepository;
import com.miguan.report.service.report.impl.UmengServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * APP使用时长任务
 */
@Slf4j
@Component
public class AppUseTimeTask {

    @Resource
    private UmengServiceImpl umengService;
    @Resource
    private AppUseTimeRepository appUseTimeRepository;
    @Resource
    private AppUseTimeMapper appUseTimeMapper;

    /**
     * 从第三方平台获取APP使用时长
     */
    @Scheduled(cron = "${task.scheduled.cron.app-use-time}")
    public void getAppUseTimeFromThirdWay() {
        log.info("从第三方平台获取APP使用时长开始");
        Date yesterday = DateUtils.addDays(new Date(), -1);
        String yesterdayString = DateUtil.dateStr(yesterday, DateUtil.DATEFORMAT_STR_002);

        this.getDateAndSaveList(yesterdayString);
        log.info("从第三方平台获取APP使用时长结束");

    }

    public void getDateAndSaveList(String yesterdayString){
        appUseTimeMapper.deleteAllByUseDay(yesterdayString);
        // 西柚视频使用时长
        Double xyUseTimeA = umengService.getDurations(UmengAppKeyEnum.XI_YOU_VIDEO_ANDROID.getAppId(), yesterdayString);
        AppUseTime xyAndroidEntity = packageToEntity(AppUseTimeEnum.XI_YOU_VIDEO_ANDROID, yesterdayString, xyUseTimeA.intValue());
//        Double xyUseTimeI = umengService.getDurations(UmengAppKeyEnum.XI_YOU_VIDEO_IOS.getAppId(), yesterdayString);
//        AppUseTime xyIOSEntity = packageToEntity(AppUseTimeEnum.XI_YOU_VIDEO_IOS, yesterdayString, xyUseTimeI.intValue());
        // 果果视频使用时长
        Double ggUseTimeA = umengService.getDurations(UmengAppKeyEnum.GUO_GUO_VIDEO_ANDROID.getAppId(), yesterdayString);
        AppUseTime ggAndroidEntity = packageToEntity(AppUseTimeEnum.GUO_GUO_VIDEO_ANDROID, yesterdayString, ggUseTimeA.intValue());
        Double ggUseTimeI = umengService.getDurations(UmengAppKeyEnum.GUO_GUO_VIDEO_IOS.getAppId(), yesterdayString);
        AppUseTime ggIOSEntity = packageToEntity(AppUseTimeEnum.GUO_GUO_VIDEO_IOS, yesterdayString, ggUseTimeI.intValue());
        // 豆趣视频使用时长
        Double dqUseTimeA = umengService.getDurations(UmengAppKeyEnum.DOU_QU_VIDEO_ANDROID.getAppId(), yesterdayString);
        AppUseTime dqAndroidEntity = packageToEntity(AppUseTimeEnum.DOU_QU_VIDEO_ANDROID, yesterdayString, ggUseTimeA.intValue());
        // 视频汇总数据，取平均值
        int summaryUseTime = (xyUseTimeA.intValue() + ggUseTimeA.intValue() + ggUseTimeI.intValue() + dqUseTimeA.intValue()) / 4;
        AppUseTime videoSummaryEntity = packageToEntity(AppUseTimeEnum.VIDEO_SUMMARY, yesterdayString, summaryUseTime);


        // 炫来电使用时长
        Double xldUseTime = umengService.getDurations(UmengAppKeyEnum.XUAN_LAI_DIAN_ANDROID.getAppId(), yesterdayString);
        AppUseTime xldAndroidEntity = packageToEntity(AppUseTimeEnum.XUAN_LAI_DIAN_ANDROID, yesterdayString, xldUseTime.intValue());
        // 来电汇总数据，取平均值
        AppUseTime laidianSummaryEntity = packageToEntity(AppUseTimeEnum.LAI_DIAN_SUMMARY, yesterdayString, xldUseTime.intValue());


        List<AppUseTime> useTimeLIst = new ArrayList<AppUseTime>();
        useTimeLIst.add(xyAndroidEntity);
        //useTimeLIst.add(xyIOSEntity);
        useTimeLIst.add(ggAndroidEntity);
        useTimeLIst.add(ggIOSEntity);
        useTimeLIst.add(dqAndroidEntity);
        useTimeLIst.add(xldAndroidEntity);
        useTimeLIst.add(videoSummaryEntity);
        useTimeLIst.add(laidianSummaryEntity);
        log.info("保存应用时长数据>>{}", JSONObject.toJSONString(useTimeLIst));
        appUseTimeRepository.saveAll(useTimeLIst);
        appUseTimeRepository.flush();
    }

    private AppUseTime packageToEntity(AppUseTimeEnum appUseTimeEnum, String useDay, Integer useTime) {
        AppUseTime entity = new AppUseTime();
        entity.setAppType(appUseTimeEnum.getAppType());
        entity.setDataType(appUseTimeEnum.getDataType());
        entity.setUseDay(useDay);
        entity.setUseTime(useTime.intValue());
        entity.setUseTimeStr(DateTimeUtils.secondsToTimeString(useTime.intValue()));
        entity.setCreateTime(DateUtil.getTime(new Date()));
        return entity;
    }
}
