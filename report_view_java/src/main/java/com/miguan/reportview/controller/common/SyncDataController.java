package com.miguan.reportview.controller.common;

import com.miguan.reportview.common.enmus.LdPushUserEnmu;
import com.miguan.reportview.common.exception.NullParameterException;
import com.miguan.reportview.mapper.ChannelDetailMapper;
import com.miguan.reportview.mapper.PushVideoMapper;
import com.miguan.reportview.service.*;
import com.miguan.reportview.task.ClickhouseHourTask;
import com.miguan.reportview.task.PushVideoTask;
import com.miguan.reportview.task.RpUserKeepTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.Calendar;

/**
 * @author zhongli
 * @date 2020-07-30 
 *
 */
@Api(value = "同步数据", tags = {"同步数据"})
@RestController
@Slf4j
public class SyncDataController {

    @Resource
    private SyncRpTotalDataService syncRpTotalDataService;
    @Resource
    private ClickhouseHourTask clickhouseHourTask;
    @Resource
    private SyncUserContentService syncUserContentService;
    @Resource
    private IRealTimeStatisticsService realTimeStatisticsService;
    @Resource
    private IUserKeepService iUserKeepService;
    @Resource
    private ChannelDetailMapper channelDetailMapper;
    @Resource
    private IVideosService videosService;
    @Resource
    private PushVideoService pushVideoService;
    @Resource
    private PushLdService pushLdService;
    @Resource
    private RpUserKeepTask rpUserKeepTask;
    @Resource
    private PushVideoMapper pushVideoMapper;
    @Resource
    private PushVideoTask pushVideoTask;

    @ApiOperation(value = "从clickhouse同步rp_total_minute表数据")
    @PostMapping("api/sync/rp/total/minute")
    public void syncRpTotalMinute(@ApiParam(value = "开始分钟; 日期格式: yyyyMMddHHmm", required = true) Long startDm,
                                  @ApiParam(value = "开始分钟; 日期格式: yyyyMMddHHmm", required = true) Long endDm) {
        if (startDm == null || endDm == null) {
            throw new NullParameterException();
        }
        syncRpTotalDataService.syncRpTotalMinute(startDm, endDm);
    }

    @ApiOperation(value = "从clickhouse同步rp_total_hour表数据")
    @PostMapping("api/sync/rp/total/hour")
    public void syncRpTotalHour(@ApiParam(value = "开始小时; 日期格式: yyyyMMddHH", required = true) Integer startDh,
                                @ApiParam(value = "结束小时; 日期格式: yyyyMMddHH", required = true) Integer endDh) {
        if (startDh == null || endDh == null) {
            throw new NullParameterException();
        }
        syncRpTotalDataService.syncRpTotalHour(startDh, endDh);
    }

    @ApiOperation(value = "从clickhouse同步rp_total_day表数据")
    @PostMapping("api/sync/rp/total/day")
    public void syncRpTotalDay(@ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true) String startDate,
                               @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true) String endDate) {
        if (StringUtils.isAnyBlank(startDate, endDate)) {
            throw new NullParameterException();
        }
        syncRpTotalDataService.syncRpTotalDay(startDate, endDate);
    }

    @ApiOperation(value = "从clickhouse同步ld_rp_total_minute表数据")
    @PostMapping("api/sync/rp/ldtotal/minute")
    public void syncLdRpTotalMinute(@ApiParam(value = "开始分钟; 日期格式: yyyyMMddHHmm", required = true) Long startDm,
                                    @ApiParam(value = "开始分钟; 日期格式: yyyyMMddHHmm", required = true) Long endDm) {
        if (startDm == null || endDm == null) {
            throw new NullParameterException();
        }
        syncRpTotalDataService.syncLdRpTotalMinute(startDm, endDm);
    }

    @ApiOperation(value = "从clickhouse同步ld_rp_total_hour表数据")
    @PostMapping("api/sync/rp/ldtotal/hour")
    public void syncLdRpTotalHour(@ApiParam(value = "开始小时; 日期格式: yyyyMMddHH", required = true) Integer startDh,
                                @ApiParam(value = "结束小时; 日期格式: yyyyMMddHH", required = true) Integer endDh) {
        if (startDh == null || endDh == null) {
            throw new NullParameterException();
        }
        syncRpTotalDataService.syncLdRpTotalHour(startDh, endDh);
    }

    @ApiOperation(value = "从clickhouse同步ld_rp_total_day表数据")
    @PostMapping("api/sync/rp/ldtotal/day")
    public void syncLdRpTotalDay(@ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true) String startDate,
                                 @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true) String endDate) {
        if (StringUtils.isAnyBlank(startDate, endDate)) {
            throw new NullParameterException();
        }
        syncRpTotalDataService.syncLdRpTotalDay(startDate, endDate);
    }

    @ApiOperation(value = "手动触发同步rp_total_hour_accumulated到clickhouse")
    @PostMapping("api/sync/clickhouseHourTask")
    public void clickhouseHourTask() {
        clickhouseHourTask.hourStatistics();
    }

    @ApiOperation(value = "手动触发同步ld_rp_total_hour_accumulated到clickhouse")
    @PostMapping("api/sync/clickhouseLdHourTask")
    public void clickhouseLdHourTask(String startDh, String endDh, Integer type) {
        if(type == null) {
            while(Integer.parseInt(startDh) <= Integer.parseInt(endDh)) {
                String day = startDh.substring(0, 8);
                String oneStartDh = day + "00";
                String oneEndDhStr = startDh;
                log.info("手动汇总来电数据每日小时累计统计任务，startDh:{},endDh:{}", oneStartDh, oneEndDhStr);
                realTimeStatisticsService.staLdData(Integer.parseInt(oneStartDh), Integer.parseInt(oneEndDhStr), Integer.parseInt(oneEndDhStr));

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(DateUtil.valueOf(String.valueOf(startDh), "yyyyMMddHH"));
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                startDh = DateUtil.dateStr(calendar.getTime(), "yyyyMMddHH");
            }
        } else {
            clickhouseHourTask.hourLdStatistics();
        }

    }

    public static void main(String[] args) {
        int time = 2020090823;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.valueOf(String.valueOf(time), "yyyyMMddHH"));
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        System.out.println(DateUtil.dateStr(calendar.getTime(), "yyyyMMddHH").substring(8));
        System.out.println("2020090901".substring(0,8));
    }

    @ApiOperation(value = "从clickhouse初始化用户内容运营数据到user_content_operation")
    @PostMapping("api/sync/syncUserContent")
    public void syncUserContent(@ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true) String startDate,
                                @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true) String endDate) {
        log.info("初始化用户内容运营数据（start，from {} to {}）",startDate, endDate);
        long end = DateUtil.valueOf(endDate).getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.valueOf(startDate));

        while(calendar.getTimeInMillis() <= end) {
            String date = DateUtil.dateStr2(calendar.getTime());
            log.info("初始化用户内容运营数据,日期：{}", date);
            syncUserContentService.syncUserContent(date, date);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        log.info("同步用户内容运营数据（end，from {} to {}）",startDate, endDate);
    }

    @ApiOperation(value = "从clickhouse初始化来电用户内容运营数据到ld_user_content_operation")
    @PostMapping("api/sync/syncLdUserContent")
    public void syncLdUserContent(@ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true) String startDate,
                                  @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true) String endDate) {
        log.info("初始化用来电户内容运营数据（start，from {} to {}）",startDate, endDate);
        long end = DateUtil.valueOf(endDate).getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.valueOf(startDate));

        while(calendar.getTimeInMillis() <= end) {
            String date = DateUtil.dateStr2(calendar.getTime());
            log.info("初始化来电用户内容运营数据,日期：{}", date);
            syncUserContentService.syncLdUserContent(date);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        log.info("同步来电用户内容运营数据（end，from {} to {}）",startDate, endDate);
    }

    @ApiOperation(value = "同步视频实时留存数据到mysql的rp_user_keep")
    @PostMapping("api/sync/syncRealUserKeep")
    public void syncRealUserKeep(@ApiParam(value = "当日; 日期格式: yyyy-MM-dd", required = true) String today,
                                 @ApiParam(value = "昨日; 日期格式: yyyy-MM-dd", required = true) String yesterday) {
        rpUserKeepTask.syncUserKeepService(today, yesterday);
    }

    @ApiOperation(value = "同步来电留存数据到mysql的rp_user_keep")
    @PostMapping("api/sync/syncLdUserKeep")
    public void syncLdUserKeep(@ApiParam(value = "日期; 日期格式: yyyy-MM-dd", required = true) String date) {
        iUserKeepService.syncLdUserKeepData(DateUtil.valueOf(date));
    }

    @ApiOperation(value = "从clickhouse聚合渠道明细数据到channel_detail中")
    @PostMapping("api/sync/syncChannelDetail")
    public void syncChannelDetail(@ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true) String startDate,
                                  @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true) String endDate) {
        log.info("手动聚合渠道明细数据（start，from {} to {}）",startDate, endDate);
        long end = DateUtil.valueOf(endDate).getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.valueOf(startDate));

        while(calendar.getTimeInMillis() <= end) {
            String date = DateUtil.dateStr2(calendar.getTime());
            log.info("手动聚合渠道明细数据,日期：{}", date);
            channelDetailMapper.deleteChannelDetail(date);
            channelDetailMapper.batchSaveChannelDetail(date);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        log.info("手动聚合渠道明细数据（end，from {} to {}）",startDate, endDate);
    }

    @ApiOperation(value = "手动同步视频信息数据到clickhouse的video_info表")
    @PostMapping("api/sync/syncVideoInfo")
    public void syncVideoInfo() {
        log.info("手动同步视频数据（start）");
        videosService.syncVideoInfo();
        log.info("手动同步视频数据（end）");
    }


    @ApiOperation(value = "手动同步来电视频信息数据到clickhouse的ld_video_info表")
    @PostMapping("api/sync/syncLdVideoInfo")
    public void syncLdVideoInfo() {
        log.info("手动同步视频数据（start）");
        videosService.syncLdVideoInfo();
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
            videosService.syncVideoDetail(date);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        log.info("手动同步视频明细（end）");
    }

    @ApiOperation(value = "同步push视频库视频的播放数，有效播放数，完播数")
    @PostMapping("api/sync/syncPushVideos")
    public void syncPushVideos() {
        pushVideoService.syncPushVideos();
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

//    @ApiOperation(value = "生成用户推送结果表")
//    @PostMapping("api/sync/batchStaPushVidosResult")
//    public void batchStaPushVidosResult(@ApiParam(value = "用户类型，1:用户（激活当天）,20点前新增的用户，且（0-19：59）未产生播放行为;" +
//            "2:新用户（激活当天）,20点后新增的用户，且（20-23：59）未产生播放行为" +
//            "3:新用户（激活当天) 当日产生播放行为" +
//            "4:老用户（激活次日以后） 当日产生播放行为" +
//            "5:老用户（激活次日以后） 当日未产生播放行为" +
//            "6:不活跃用户（未启动天数>=30天）") Integer userType,
//                                        @ApiParam(value = "日期: yyyy-MM-dd", required = true) String dd) {
//        pushVideoService.batchStaPushVidosResult(userType, dd);
//    }

    @ApiOperation(value = "同步push来电秀库的播放数（同步到mysql库）")
    @PostMapping("api/sync/syncPushLdPlayCount")
    public void syncPushLdPlayCount() {
        pushLdService.syncPushLdPlayCount();
    }

    @ApiOperation(value = "同步来电秀分类")
    @PostMapping("api/sync/syncLdVideoCat")
    public void syncLdVideoCat() {
        pushLdService.syncLdVideoCat();
    }

    @ApiOperation(value = "手动优化mysql（dataserver）自动推送的表")
    @PostMapping("api/sync/optimizeTable")
    public void optimizeTable(@ApiParam(value = "优化的表名") String tableName) {
        pushVideoMapper.optimizeTable(tableName);
    }

    @PostMapping("api/sync/deleteOldPushData")
    public void deleteOldPushData() {
        pushVideoTask.deleteOldData();
    }
}
