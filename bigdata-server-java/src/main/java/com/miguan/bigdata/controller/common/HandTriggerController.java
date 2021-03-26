package com.miguan.bigdata.controller.common;

import com.miguan.bigdata.common.enums.LdPushUserEnmu;
import com.miguan.bigdata.common.util.RedisClient;
import com.miguan.bigdata.mapper.DspPlanMapper;
import com.miguan.bigdata.service.AdDataService;
import com.miguan.bigdata.service.PushLdService;
import com.miguan.bigdata.service.PushVideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Api(value = "手动触发定时器任务", tags = {"手动触发定时器任务接口"})
@RestController
@Slf4j
public class HandTriggerController {

    @Resource
    private AdDataService adDataService;
    @Resource
    private DspPlanMapper dspPlanMapper;
    @Resource
    private PushLdService pushLdService;
    @Resource
    private PushVideoService pushVideoService;
    @Resource
    private RedisClient redisClient;

    @ApiOperation(value = "手动同步数据到dsp的idea_advert_report")
    @PostMapping("/api/hand/trigger/syncDspPlan")
    public void syncDspPlan(@ApiParam(value = "开始日期，格式：yyyyMMdd", required = true) Integer startDay,
                            @ApiParam(value = "结束日期，格式：yyyyMMdd", required = true) Integer endDay) {
        log.info("手动同步数据到dsp的idea_advert_report(start)");
        adDataService.syncDspPlan(startDay, endDay);
        log.info("手动同步数据到dsp的idea_advert_report(end)");
    }

    @ApiOperation(value = "手动统计近7天每个时间段的平均活跃用户数到time_slot_active_user")
    @PostMapping("/api/hand/trigger/syncTimeSlotActiveUser")
    public void syncTimeSlotActiveUser(@ApiParam(value = "日期，格式：yyyy-MM-dd", required = true) String date) {
        Date staDate = DateUtil.valueOf(date);
        String startDay = DateUtil.dateStr7(DateUtil.rollDay(staDate,-7));  //7天前日期
        String endDay = DateUtil.dateStr7(DateUtil.rollDay(staDate,-1));  //昨天日期

        Map<String, Object> params = new HashMap<>();
        params.put("startDay", Integer.parseInt(startDay));
        params.put("endDay", Integer.parseInt(endDay));
        params.put("dt", Integer.parseInt(date.replace("-", "")));
        dspPlanMapper.deleteTimeSlotActiveUser(params);
        dspPlanMapper.insertTimeSlotActiveUser(params);
    }

    @ApiOperation(value = "手动从ck统计数据同步到allvideoadv库ad_multi_dimensional_data的表")
    @PostMapping("/api/hand/trigger/syncAdMultiDimensionalData")
    public void syncAdMultiDimensionalData(@ApiParam(value = "日期，格式：yyyy-MM-dd", required = true) String date) {
        adDataService.syncAdMultiDimensionalData(date);
    }

    @ApiOperation(value = "统计出视频push推送的用户")
    @PostMapping("api/sync/batchSavePushUser")
    public void batchSavePushUser(@ApiParam(value = "用户类型") Integer userType,
                                  @ApiParam(value = "包名") String packageName,
                                  @ApiParam(value = "活跃度配置天数字段") String activityDay,
                                  @ApiParam(value = "触发类型  1：当天，2：明天") Integer triggerType) {
        pushVideoService.batchSavePushUser(userType, packageName, activityDay, triggerType);   //统计出push推送的用户
    }

    @ApiOperation(value = "统计出来电push推送的用户")
    @PostMapping("api/sync/batchSaveLdPushUser")
    public void batchSaveLdPushUser(@ApiParam(value = "用户大类") Integer type,
                                    @ApiParam(value = "用户类型") Integer userType,
                                    @ApiParam(value = "触发类型  1：当天，2：次日") Integer triggerType,
                                    @ApiParam(value = "包名") String packageName,
                                    @ApiParam(value = "活跃度配置天数字段(开始)") Integer activityDayStart,
                                    @ApiParam(value = "活跃度配置天数字段(结束)") Integer activityDayEnd) {
        if (type == 1) {
            //统计（内容推送-新增用户）的数据
            pushLdService.syncNewUser(packageName, triggerType);
        } else if (type == 2) {
            //统计活跃用户数据
            pushLdService.syncActiveUser(packageName, triggerType, activityDayStart, activityDayEnd);
        } else if (type == 3) {
            //统计不活跃用户数据
            pushLdService.syncNoActiveUser(packageName, triggerType, activityDayStart, activityDayEnd);
        } else if (type == 4) {
            //统计推送-新增用户
            pushLdService.syncSignNewUser(packageName, triggerType, userType);
        } else if (type == 5) {
            //统计推送-活跃用户
            if(userType == LdPushUserEnmu.SIGN_PUSH_NEWUSER_A.getCode()) {
                //统计（签到推送-新增用户）用户数据
                pushLdService.syncNewUserNoSign(packageName, triggerType, userType);
            } else if(userType == LdPushUserEnmu.SIGN_PUSH_ACTIVE_A.getCode() || userType == LdPushUserEnmu.SIGN_PUSH_ACTIVE_B.getCode()) {
                //统计（签到推送-活跃用户-连续签到）用户数据
                pushLdService.syncActiveContinueSign(packageName, triggerType, userType, activityDayStart, activityDayEnd);
            } else if(userType == LdPushUserEnmu.SIGN_PUSH_ACTIVE_C.getCode()) {
                //统计（签到推送-活跃用户-昨日已签到-当日（0-20点）未签到）用户数据
                pushLdService.syncYesSignTodayNoSign(packageName, triggerType, userType, activityDayStart, activityDayEnd);
            } else if(userType == LdPushUserEnmu.SIGN_PUSH_ACTIVE_D.getCode()) {
                //统计（签到推送-活跃用户-昨日未签到）用户数据
                pushLdService.syncYesNoSign(packageName, triggerType, userType, activityDayStart, activityDayEnd);
            }
        } else if (type == 6) {
            //统计推送-活跃用户
            if(userType == LdPushUserEnmu.ACTIVITY_PUSH_NEWUSER_A.getCode() || userType == LdPushUserEnmu.ACTIVITY_PUSH_NEWUSER_B.getCode()) {
                //统计（活动推送-新增用户）用户数据
                pushLdService.syncNewUserActivity(packageName, triggerType, userType);
            } else if(userType == LdPushUserEnmu.ACTIVITY_PUSH_ACTIVE_A.getCode() || userType == LdPushUserEnmu.ACTIVITY_PUSH_ACTIVE_B.getCode()) {
                //统计（活动推送-活跃用户）用户数据
                pushLdService.syncOldUser(packageName, triggerType, activityDayStart, activityDayEnd);
            } else if(userType == LdPushUserEnmu.ACTIVITY_PUSH_ACTIVE_C.getCode()) {
                //统计（活动推送-不活跃用户）用户数据
                pushLdService.syncSignNoActiveUser(packageName, triggerType,userType, activityDayStart, activityDayEnd);
            }
        }
    }

    @ApiOperation(value = "设置广告预警需要预警的参数")
    @PostMapping("/api/setEarlyWarnParam")
    public String setEarlyWarnParam(@ApiParam(value = "类型，0：app版本（格式例如：'3.2.1','3.3.0'），1：每小时预警的日活阈值，2：每日预警的日活预警阀值") Integer type,
                                    String value) {
        if(type == 0) {
            redisClient.set("early_warn_app_version", value);
        } else if(type == 1) {
            redisClient.set("hour_active_user", value);
        } else if(type == 2) {
            redisClient.set("day_active_user", value);
        }
        return value;
    }

    @ApiOperation(value = "手动同步视频信息数据到clickhouse的video_info表")
    @PostMapping("api/sync/syncVideoInfo")
    public void syncVideoInfo() {
        log.info("手动同步视频数据（start）");
        pushVideoService.syncVideoInfo();
        log.info("手动同步视频数据（end）");
    }

    @ApiOperation(value = "手动同步来电视频信息数据到clickhouse的ld_video_info表")
    @PostMapping("api/sync/syncLdVideoInfo")
    public void syncLdVideoInfo() {
        log.info("手动同步视频数据（start）");
        pushLdService.syncLdVideoInfo();
        log.info("手动同步视频数据（end）");
    }

    @ApiOperation(value = "手动同步视频明细数据到clickhouse的video_detail表")
    @PostMapping("api/sync/syncVideoDetail")
    public void syncVideoDetail(@ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true) String startDate,
                                @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true) String endDate) {
        log.info("手动同步视频明细（start）");
        long end = DateUtil.valueOf(endDate).getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.valueOf(startDate));

        while(calendar.getTimeInMillis() <= end) {
            String date = DateUtil.dateStr2(calendar.getTime());
            log.info("手动聚合视频明细数据,日期：{}", date);
            pushVideoService.syncVideoDetail(date);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        log.info("手动同步视频明细（end）");
    }
}
