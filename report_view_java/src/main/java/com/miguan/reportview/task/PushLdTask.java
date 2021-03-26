package com.miguan.reportview.task;

import com.cgcg.context.util.StringUtils;
import com.miguan.reportview.common.enmus.LdPushUserEnmu;
import com.miguan.reportview.entity.RpUserKeep;
import com.miguan.reportview.mapper.PushLdMapper;
import com.miguan.reportview.mapper.PushVideoMapper;
import com.miguan.reportview.service.PushLdService;
import com.miguan.reportview.service.PushVideoService;
import com.miguan.reportview.service.RedisService;
import com.miguan.reportview.vo.PushLdConfigVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * 定时统计来电自动推送用户数据
 * @author zhangbinglin
 *
 */
@Component
@Slf4j
public class PushLdTask {
    @Resource
    private PushLdService pushLdService;
    @Resource
    private PushLdMapper pushLdMapper;
    @Resource
    private PushVideoMapper pushVideoMapper;
    @Value("${spring.profiles.active}")
    private String activeProfiles;
    @Resource
    private RedisService redisService;
    private Long beforeTime = 60 * 13 *1000L; //自动推送前13分钟，开始统计用户数据
    private static ExecutorService excutorService = newFixedThreadPool(10);
    /**
     * 统计自动推送的数据
     */
//    @Scheduled(cron = "30 * * * * ?")   自动推送模块以及迁移到 大数据部的bigdata-server服务中
    public void staPushVideosResult() {
        log.info("统计来电自动推送的数据中 当前开启的策略为 {}", activeProfiles);
        if (!"prod".equals(activeProfiles) && !"localdev".equals(activeProfiles)) {
            return;
        }
        List<PushLdConfigVo> configs = pushLdMapper.queryLdAutoConfig();
        if(configs == null) {
            return;
        }
        for(PushLdConfigVo config : configs) {
            String[] packageNames = config.getPackageName().split(",");
            Integer userType = config.getUserType();  //用户类型
            Integer triggerType = config.getTriggerType();  //触发类型  1：当天，2：次日，3：每小时检查。4：事件触发立即推送 单选
            String triggerTime = config.getTriggerTime();  //触发时间 HH:mm:ss，逗号隔开
            String activityDays = config.getActivityDays();
            Integer startDay = StringUtils.isNotBlank(activityDays) ? Integer.parseInt(activityDays.split(",")[0]) : 0;
            Integer endDay = StringUtils.isNotBlank(activityDays) ? Integer.parseInt(activityDays.split(",")[1]) : 0;

            for(int i=0;i<packageNames.length;i++) {
                String packageName = packageNames[0];
                if(userType >= LdPushUserEnmu.CONTENT_PUSH_NEWUSER_A.getCode() && userType <= LdPushUserEnmu.CONTENT_PUSH_NEWUSER_D.getCode()) {
                    //内容推送-新增用户-未设置来电秀
                    String key = "CONTENT_PUSH_NEWUSER:" + packageName;
                    if(isTriggerSync(triggerType, triggerTime, key)) {
                        //当前时间是否在自动推送配置时间的前10分钟，如果是则开始统计用户类型数据
                        excutorService.execute(() -> {
                            pushLdService.syncNewUser(packageName, triggerType);
                        });
                    }
                }

                if(userType >= LdPushUserEnmu.CONTENT_PUSH_ACTIVE_A.getCode() && userType <= LdPushUserEnmu.CONTENT_PUSH_ACTIVE_E.getCode()) {
                    //统计活跃用户数据
                    String key = "CONTENT_PUSH_ACTIVE:" + packageName;
                    if(isTriggerSync(triggerType, triggerTime, key)) {
                        //当前时间是否在自动推送配置时间的前10分钟，如果是则开始统计用户类型数据
                        excutorService.execute(() -> {
                            pushLdService.syncActiveUser(packageName, triggerType, startDay, endDay);
                        });
                    }
                }

                if(userType >= LdPushUserEnmu.CONTENT_PUSH_NOT_ACTIVE_A.getCode() && userType <= LdPushUserEnmu.CONTENT_PUSH_NOT_ACTIVE_E.getCode()) {
                    //统计不活跃用户数据
                    String key = "CONTENT_PUSH_NOT_ACTIVE:" + packageName;
                    if(isTriggerSync(triggerType, triggerTime, key)) {
                        //当前时间是否在自动推送配置时间的前10分钟，如果是则开始统计用户类型数据
                        excutorService.execute(() -> {
                            pushLdService.syncNoActiveUser(packageName, triggerType, startDay, endDay);
                        });
                    }
                }

                if(userType == LdPushUserEnmu.SIGN_PUSH_NEWUSER_A.getCode()) {
                    //统计（签到推送-新增用户）用户数据
                    String key = "LD_NEW_USER_NO_SIGN:" + packageName;
                    if(isTriggerSync(triggerType, triggerTime, key)) {
                        //当前时间是否在自动推送配置时间的前10分钟，如果是则开始统计用户类型数据
                        excutorService.execute(() -> {
                            pushLdService.syncNewUserNoSign(packageName, triggerType, userType);
                        });
                    }
                }

                if(userType == LdPushUserEnmu.SIGN_PUSH_ACTIVE_A.getCode() || userType == LdPushUserEnmu.SIGN_PUSH_ACTIVE_B.getCode()) {
                    //统计（签到推送-活跃用户-连续签到）用户数据
                    String key = "LD_SIGN_PUSH_ACTIVE:" + packageName + userType;
                    if(isTriggerSync(triggerType, triggerTime, key)) {
                        //当前时间是否在自动推送配置时间的前10分钟，如果是则开始统计用户类型数据
                        excutorService.execute(() -> {
                            pushLdService.syncActiveContinueSign(packageName, triggerType, userType, startDay, endDay);
                        });
                    }
                }

                if(userType == LdPushUserEnmu.SIGN_PUSH_ACTIVE_C.getCode()) {
                    //统计（签到推送-活跃用户-昨日已签到-当日（0-20点）未签到）用户数据
                    String key = "LD_SIGN_PUSH_ACTIVE_C:" + packageName;
                    if(isTriggerSync(triggerType, triggerTime, key)) {
                        //当前时间是否在自动推送配置时间的前10分钟，如果是则开始统计用户类型数据
                        excutorService.execute(() -> {
                            pushLdService.syncYesSignTodayNoSign(packageName, triggerType, userType, startDay, endDay);
                        });
                    }
                }

                if(userType == LdPushUserEnmu.SIGN_PUSH_ACTIVE_D.getCode()) {
                    //统计（签到推送-活跃用户-昨日未签到）用户数据
                    String key = "LD_SIGN_PUSH_ACTIVE_D:" + packageName;
                    if(isTriggerSync(triggerType, triggerTime, key)) {
                        excutorService.execute(() -> {
                            pushLdService.syncYesNoSign(packageName, triggerType, userType, startDay, endDay);
                        });
                    }
                }

                if(userType == LdPushUserEnmu.ACTIVITY_PUSH_NEWUSER_A.getCode() || userType == LdPushUserEnmu.ACTIVITY_PUSH_NEWUSER_B.getCode()) {
                    //统计（活动推送-新增用户）用户数据
                    String key = "LD_ACTIVITY_PUSH_NEWUSER:" + packageName + userType;
                    if(isTriggerSync(triggerType, triggerTime, key)) {
                        excutorService.execute(() -> {
                            pushLdService.syncNewUserActivity(packageName, triggerType, userType);
                        });
                    }
                }

                if(userType == LdPushUserEnmu.ACTIVITY_PUSH_ACTIVE_A.getCode() || userType == LdPushUserEnmu.ACTIVITY_PUSH_ACTIVE_B.getCode()) {
                    //统计（活动推送-活跃用户）用户数据
                    String key = "LD_ACTIVITY_PUSH_ACTIVE:" + packageName;
                    if(isTriggerSync(triggerType, triggerTime, key)) {
                        excutorService.execute(() -> {
                            pushLdService.syncOldUser(packageName, triggerType, startDay, endDay);
                        });
                    }
                }
                if(userType == LdPushUserEnmu.ACTIVITY_PUSH_ACTIVE_C.getCode()) {
                    //统计（活动推送-不活跃用户）用户数据
                    String key = "LD_ACTIVITY_PUSH_ACTIVE_C:" + packageName;
                    if(isTriggerSync(triggerType, triggerTime, key)) {
                        excutorService.execute(() -> {
                            pushLdService.syncSignNoActiveUser(packageName, triggerType,userType, startDay, endDay);
                        });
                    }
                }

            }
        }
    }

    /**
     * ld_push_user和ld_auto_push_user_video只保留3天的数据
     */
//    @Scheduled(cron = "0 0 4 * * ?") 自动推送模块以及迁移到 大数据部的bigdata-server服务中
    public void deleteOldData() {
        log.info("ld_push_user只保留3天的数据（start）");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -2);
        String dd = DateUtil.dateStr2(calendar.getTime());
        pushLdMapper.deleteOldPushLdUser(dd);
        pushLdMapper.delOldLdAutoPushUserVideo(dd);
        pushVideoMapper.optimizeTable("ld_auto_push_user_video");  //数据全部删除后，要执行下优化表的脚本，否则delete后的硬盘空间无法释放
        log.info("ld_push_user只保留3天的数据（end）");
    }

    /**
     * 当前时间是否在自动推送配置时间的前10分钟，如果是则开始统计用户类型数据
     * @param triggerType  触发类型  1：当天，2：次日，3：每小时检查。4：事件触发立即推送 单选
     * @param triggerTime
     * @param redisKeyLock redis锁的key
     * @return
     */
    private boolean isTriggerSync(int triggerType, String triggerTime, String redisKeyLock) {
        if(triggerType == 3) {
            return isTriggerSync(redisKeyLock);
        } else {
            return isTriggerSync(triggerTime, redisKeyLock);
        }
    }

    /**
     * 当前时间是否在自动推送配置时间的前10分钟，如果是则开始统计用户类型数据
     * @param triggerTime
     * @param redisKeyLock redis锁的key
     * @return
     */
    private boolean isTriggerSync(String triggerTime, String redisKeyLock) {
        String value = redisService.get(redisKeyLock);
        if(StringUtils.isNotBlank(value)) {
            return false;
        }
        String date = DateUtil.dateStr2(new Date());
        String[] triggerTimes = triggerTime.split(",");
        for(int i=0;i<triggerTimes.length;i++) {
            String time = date + " " + triggerTimes[i];
            Long nowMillis = System.currentTimeMillis();
            Long timeMillis = DateUtil.valueOf(time, "yyyy-MM-dd HH:mm:ss").getTime();
            Long difference = timeMillis - nowMillis;
            if(difference > 0 && difference <= beforeTime) {
                difference = difference/1000;  //转成秒
                redisService.set(redisKeyLock, "1", difference.intValue());  //加上redis锁，同一种类的用户数，一个时间只统计一次
                return true;
            }
        }
        return false;
    }



    /**
     * 当前时间是否在自动推送配置时间的前10分钟，如果是则开始统计用户类型数据
     * @param redisKeyLock
     * @return
     */
    private boolean isTriggerSync(String redisKeyLock) {
        String value = redisService.get(redisKeyLock);
        if(StringUtils.isNotBlank(value)) {
            return false;
        }

        String date = DateUtil.dateStr(new Date(), "yyyy-MM-dd HH");
        String time = date + ":30:00";
        Long nowMillis = System.currentTimeMillis();
        Long timeMillis = DateUtil.valueOf(time, "yyyy-MM-dd HH:mm:ss").getTime();
        Long difference = timeMillis - nowMillis;
        if(difference > 0 && difference <= beforeTime) {
            difference = difference/1000;  //转成秒
            redisService.set(redisKeyLock, "1", difference.intValue());  //加上redis锁，同一种类的用户数，一个时间只统计一次
            return true;
        }
        return false;
    }
}
