package com.miguan.bigdata.task;

import com.miguan.bigdata.common.config.SpringProfiles;
import com.miguan.bigdata.common.enums.PushUserEnmu;
import com.miguan.bigdata.mapper.PushVideoMapper;
import com.miguan.bigdata.service.PushVideoService;
import com.miguan.bigdata.service.Redis2Service;
import com.miguan.bigdata.vo.PushConfigVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * 定时统计视频自动推送用户数据
 *
 * @author zhangbinglin
 */
@Component
@Slf4j
public class PushVideoTask {
    @Resource
    private PushVideoService pushVideoService;
    @Resource
    private PushVideoMapper pushVideoMapper;
    @Resource
    private Redis2Service redis2Service;
    @Resource
    private SpringProfiles springProfiles;

    private Long beforeTime = 60 * 20 * 1000L; //自动推送前15分钟，开始统计用户数据
    private static ExecutorService excutorService = newFixedThreadPool(10);


    /**
     * 统计自动推送的数据
     */
    @Scheduled(cron = "30 * * * * ?")
    public void staPushVideosResult() {
        log.info("统计视频自动推送的数据中 当前开启的策略为 {}", springProfiles.getActive());
        if (springProfiles.isLocal() || springProfiles.isLocalDev()) {
            return;
        }

        List<PushConfigVo> configs = pushVideoMapper.findPushConfig();
        if (configs == null) {
            log.debug("自动推送,视频配置为空");
            return;
        }
        for (PushConfigVo config : configs) {
            Integer userType = config.getType();  //用户属性
            String triggerTime = config.getTriggerTime();  //触发时间

            String[] packageNames = config.getPackageName().split(",");
            for (int i = 0; i < packageNames.length; i++) {
                String packageName = packageNames[i];  //包名
                String key = "VIDEO_AUTO_PUSH:" + packageName + userType;
                if (isTriggerSync(triggerTime, key)) {
                    if (PushUserEnmu.oldUserNoPlay.getCode() == userType || PushUserEnmu.oldUserNoActive.getCode() == userType) {
                        //不使用线程并行统计。防止出现内存不足，造成数据统计失败。（5,6类型的推送数据比较大）
                        pushVideoService.batchSavePushUser(userType, packageName, config.getActivityDays(), config.getTriggerType());   //统计出push推送的用户
                    } else {
                        excutorService.execute(() -> {
                            pushVideoService.batchSavePushUser(userType, packageName, config.getActivityDays(), config.getTriggerType());   //统计出push推送的用户
                        });
                    }
                }
            }
        }

    }

    /**
     * push_user和auto_push_user_video只保留3天的数据
     */
    @Scheduled(cron = "0 30 4 * * ?")
    public void deleteOldData() {
        log.info("push_user只保留3天的数据（start）");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -3);
        int dt = Integer.parseInt(DateUtil.dateStr7(calendar.getTime()));
        pushVideoMapper.deleteOldPushUser(dt);

        List<String> tableNames = pushVideoMapper.findOldAutoPushTable(DateUtil.dateStr7(calendar.getTime()));
        for (String tableName : tableNames) {
            pushVideoMapper.dropAutoPushTable(tableName);
        }
        log.info("push_user只保留3天的数据（end）");
    }

    /**
     * 当前时间是否在自动推送配置时间的前15分钟，如果是则开始统计用户类型数据
     *
     * @param triggerTime
     * @param redisKeyLock redis锁的key
     * @return
     */
    private boolean isTriggerSync(String triggerTime, String redisKeyLock) {
        String value = redis2Service.get(redisKeyLock);
        if (StringUtils.isNotBlank(value)) {
            return false;
        }
        String date = DateUtil.dateStr2(new Date());
        String[] triggerTimes = triggerTime.split(",");
        for (int i = 0; i < triggerTimes.length; i++) {
            String time = date + " " + triggerTimes[i];
            Long nowMillis = System.currentTimeMillis();
            Long timeMillis = DateUtil.valueOf(time, "yyyy-MM-dd HH:mm:ss").getTime();
            Long difference = timeMillis - nowMillis;
            if (difference > 0 && difference <= beforeTime) {
                difference = difference / 1000;  //转成秒
                redis2Service.set(redisKeyLock, "1", difference.intValue());  //加上redis锁，同一种类的用户数，一个时间只统计一次
                return true;
            }
        }
        return false;
    }
}
