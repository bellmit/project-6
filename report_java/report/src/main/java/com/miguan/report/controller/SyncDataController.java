package com.miguan.report.controller;

import com.miguan.report.common.constant.CommonConstant;
import com.miguan.report.common.enums.PlatFormEnum;
import com.miguan.report.common.util.DateUtil;
import com.miguan.report.dto.AdIdAndNameDto;
import com.miguan.report.entity.BannerRule;
import com.miguan.report.service.adv.AdvertCodeJoinService;
import com.miguan.report.service.report.BannerRuleService;
import com.miguan.report.service.report.ShenceDataService;
import com.miguan.report.service.report.UmengService;
import com.miguan.report.service.sync.SyncDataService;
import com.miguan.report.task.UserBehaviorTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Api(description = "/api/sync/data", tags = "同步数据到报表库")
@Slf4j
@RestController
@RequestMapping("/api/sync/data")
public class SyncDataController {

    @Resource
    private UmengService umengService;
    @Resource
    private ShenceDataService shenceDataService;
    @Resource
    private SyncDataService syncDataService;
    @Autowired
    UserBehaviorTask userBehaviorTask;
    @Autowired
    private AdvertCodeJoinService advertCodeJoinService;
    @Autowired
    private BannerRuleService bannerRuleService;

    @ApiOperation(value = "初始化友盟数据表")
    @PostMapping("/initUmengData")
    public void initUmengData(@ApiParam(value = "统计开始时间,格式：yyyy-MM-dd", required = true) String startDate,
                              @ApiParam(value = "统计结束时间,格式：yyyy-MM-dd", required = true) String endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.strToDate(startDate, "yyyy-MM-dd"));
        Date endTime = DateUtil.strToDate(endDate, "yyyy-MM-dd");
        while (endTime.getTime() >= calendar.getTimeInMillis()) {
            umengService.saveUmengData(DateUtil.dateToStr(calendar.getTime(), "yyyy-MM-dd"));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    @ApiOperation(value = "初始化友盟渠道数据表")
    @PostMapping("/initUmengChannelData")
    public void initUmengChannelData(@ApiParam(value = "统计开始时间,格式：yyyy-MM-dd", required = true) String startDate,
                                     @ApiParam(value = "统计结束时间,格式：yyyy-MM-dd", required = true) String endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.strToDate(startDate, "yyyy-MM-dd"));
        Date endTime = DateUtil.strToDate(endDate, "yyyy-MM-dd");
        while (endTime.getTime() >= calendar.getTimeInMillis()) {
            umengService.saveUmengChannel(DateUtil.dateToStr(calendar.getTime(), "yyyy-MM-dd"));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    @ApiOperation(value = "初始化神策数据表")
    @PostMapping("/initShenceData")
    public void initShenceData(@ApiParam(value = "统计开始时间,格式：yyyy-MM-dd", required = true) String startDate,
                               @ApiParam(value = "统计结束时间,格式：yyyy-MM-dd", required = true) String endDate,
                               @ApiParam(value = "app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.strToDate(startDate, "yyyy-MM-dd"));
        Date endTime = DateUtil.strToDate(endDate, "yyyy-MM-dd");
        while (endTime.getTime() >= calendar.getTimeInMillis()) {
            shenceDataService.saveShenceData(DateUtil.dateToStr(calendar.getTime(), "yyyy-MM-dd"), appType);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    @ApiOperation(value = "同步错误数和请求数到banner_data_ext表中")
    @PostMapping("/syncBannerDataExt")
    public void syncBannerDataExt(@ApiParam(value = "统计开始时间,格式：yyyy-MM-dd", required = true) String startDate,
                                  @ApiParam(value = "统计结束时间,格式：yyyy-MM-dd", required = true) String endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.strToDate(startDate, "yyyy-MM-dd"));
        Date endTime = DateUtil.strToDate(endDate, "yyyy-MM-dd");
        while (endTime.getTime() >= calendar.getTimeInMillis()) {
            //同步前先删除,防止重复数据
            syncDataService.deleteBannerDataExt(DateUtil.dateToStr(calendar.getTime(), "yyyy-MM-dd"));
            syncDataService.saveBannerDataExt(calendar.getTime(), CommonConstant.VIDEO_APP_TYPE);   //视频
            syncDataService.saveBannerDataExt(calendar.getTime(), CommonConstant.CALL_APP_TYPE);    //来电
            syncDataService.updateBannerExtErrRate(DateUtil.dateToStr(calendar.getTime(), "yyyy-MM-dd"));  //计算错误率
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    @ApiOperation(value = "同步点击数，展示数到hour_data中")
    @PostMapping("/syncHourData")
    public void syncHourData(@ApiParam(value = "统计开始时间,格式：yyyy-MM-dd", required = true) String startDate,
                             @ApiParam(value = "统计结束时间,格式：yyyy-MM-dd", required = true) String endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.strToDate(startDate, "yyyy-MM-dd"));
        Date endTime = DateUtil.strToDate(endDate, "yyyy-MM-dd");
        while (endTime.getTime() >= calendar.getTimeInMillis()) {
            //同步前先删除,防止重复数据
            syncDataService.deleteHourDataByDate(DateUtil.dateToStr(calendar.getTime(), "yyyy-MM-dd"));
            syncDataService.saveHourDataExt(calendar.getTime());   //视频
            syncDataService.updateClickRate(DateUtil.dateToStr(calendar.getTime(), "yyyy-MM-dd"));  //计算错误率
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    @ApiOperation(value = "同步用户行为统计分析")
    @PostMapping("/syncUserBeHavior")
    public void syncUserBeHavior(@ApiParam(value = "统计开始时间,格式：yyyy-MM-dd", required = true) String startDate,
                                 @ApiParam(value = "统计结束时间,格式：yyyy-MM-dd", required = true) String endDate) {
        LocalDate sdate = DateUtil.yyyy_MM_dd(startDate);
        LocalDate eDate = DateUtil.yyyy_MM_dd(endDate);
        if (sdate.isAfter(eDate)) {
            throw new RuntimeException("无效的日期");
        }
        while (!sdate.isAfter(eDate)) {
            userBehaviorTask.doWork(sdate.format(DateTimeFormatter.BASIC_ISO_DATE));
            sdate = sdate.plusDays(1);
        }
    }

    @ApiOperation(value = "同步广告第三方的数据数据")
    @PostMapping("/syncThirdAdvertData")
    public void syncThirdAdvertData(@ApiParam(value = "统计时间,格式：yyyy-MM-dd", required = true) String date,
                                    @ApiParam(value = "第三方广告平台：2广点通 3快手", required = true) Integer platForm) {
        Map<String, AdIdAndNameDto> videoAdnameMap = advertCodeJoinService.queryAdIdAndName(CommonConstant.VIDEO_APP_TYPE);
        Map<String, AdIdAndNameDto> callAdnameMap = advertCodeJoinService.queryAdIdAndName(CommonConstant.CALL_APP_TYPE);
        Map<String, BannerRule> bannerRuleMap = bannerRuleService.findDistinctByTodal();

        Date statDate = tool.util.DateUtil.valueOf(date);
        if (PlatFormEnum.kuai_shou.getId() == platForm.intValue()) {
            syncDataService.getDataFromKs(statDate, videoAdnameMap, callAdnameMap, bannerRuleMap);
        } else if (PlatFormEnum.guang_dian_tong.getId() == platForm.intValue()) {
            syncDataService.getDataFromGdt(statDate, videoAdnameMap, callAdnameMap, bannerRuleMap);
        }
    }
}
