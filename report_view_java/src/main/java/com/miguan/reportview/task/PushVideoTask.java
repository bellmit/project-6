package com.miguan.reportview.task;

import com.cgcg.context.util.StringUtils;
import com.miguan.reportview.common.enmus.PushUserEnmu;
import com.miguan.reportview.mapper.PushVideoMapper;
import com.miguan.reportview.service.PushVideoService;
import com.miguan.reportview.service.RedisService;
import com.miguan.reportview.vo.PushConfigVo;
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
 * 定时统计视频自动推送用户数据
 * @author zhangbinglin
 *
 */
@Component
@Slf4j
public class PushVideoTask {
    @Resource
    private PushVideoService pushVideoService;
    @Resource
    private PushVideoMapper pushVideoMapper;
    private Long beforeTime = 60 * 20 *1000L; //自动推送前15分钟，开始统计用户数据
    @Resource
    private RedisService redisService;
    private static ExecutorService excutorService = newFixedThreadPool(10);
    @Value("${spring.profiles.active}")
    private String activeProfiles;


    /**
     * 统计自动推送的数据
     */
//    @Scheduled(cron = "30 * * * * ?") 自动推送模块以及迁移到 大数据部的bigdata-server服务中
    public void staPushVideosResult() {
        log.info("统计自动推送的数据中 当前开启的策略为 {}", activeProfiles);
        if (!"prod".equals(activeProfiles) && !"localdev".equals(activeProfiles)) {
            return;
        }
        List<PushConfigVo> configs = pushVideoMapper.findPushConfig();
        if(configs == null) {
            return;
        }
        for(PushConfigVo config : configs) {
            Integer userType = config.getType();  //用户属性
            String triggerTime = config.getTriggerTime();  //触发时间

            String[] packageNames = config.getPackageName().split(",");
            for(int i=0;i<packageNames.length;i++) {
                String packageName = packageNames[i];  //包名
                String key = "VIDEO_AUTO_PUSH:" + packageName + userType;
                if(isTriggerSync(triggerTime, key)) {
                    if(PushUserEnmu.oldUserNoPlay.getCode() == userType || PushUserEnmu.oldUserNoActive.getCode() == userType) {
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
//    @Scheduled(cron = "0 30 4 * * ?") 自动推送模块以及迁移到 大数据部的bigdata-server服务中
    public void deleteOldData() {
        log.info("push_user只保留3天的数据（start）");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -3);
        int dt = Integer.parseInt(DateUtil.dateStr7(calendar.getTime()));
        pushVideoMapper.deleteOldPushUser(dt);

        List<String> tableNames = pushVideoMapper.findOldAutoPushTable(DateUtil.dateStr7(calendar.getTime()));
        for(String tableName : tableNames) {
            pushVideoMapper.dropAutoPushTable(tableName);
        }
        log.info("push_user只保留3天的数据（end）");
    }

    /**
     * 当前时间是否在自动推送配置时间的前15分钟，如果是则开始统计用户类型数据
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
}
