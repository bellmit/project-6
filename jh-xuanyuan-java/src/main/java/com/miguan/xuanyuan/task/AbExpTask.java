package com.miguan.xuanyuan.task;

import com.cgcg.context.util.SpringTaskUtil;
import com.miguan.xuanyuan.common.constant.RedisKeyConstant;
import com.miguan.xuanyuan.service.AbExpService;
import com.miguan.xuanyuan.service.AbPlatFormService;
import com.miguan.xuanyuan.service.common.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;


@Slf4j
@Component
public class AbExpTask {

    @Resource
    private ThreadPoolTaskScheduler taskScheduler;

    @Resource
    private AbExpService abExpService;

    private ScheduledFuture<?> scheduledFuture;

    private Map<Integer, ScheduledFuture<?>> futuresMap = new HashMap<>();

    @Resource
    private RedisService redisService;

    public void pubAbExp(Map<String, Object> map, Integer expId, String pubTime) {
        //通过redis去控制定时器删除的功能
        ScheduledFuture<?> toBeRemovedFuture = futuresMap.remove(expId);
        // 存在则删除旧的任务
        if (toBeRemovedFuture != null) {
            toBeRemovedFuture.cancel(true);
        }
        PubAbExpThread pubThead = new PubAbExpThread(abExpService, redisService,map,expId,pubTime);
        scheduledFuture = taskScheduler.schedule(pubThead, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                String pushTime = pubTime;
                log.info("实验发布时间是" + pushTime + ",EXPID:" + expId);
                return new CronTrigger(SpringTaskUtil.getCron(pushTime)).nextExecutionTime(triggerContext);
            }
        });
        futuresMap.put(expId, scheduledFuture);
        redisService.set(RedisKeyConstant.TASK_PUB_AB_EXP + expId,pubTime,-1);
    }

    public void deleteTask(Integer expId) {
        //通过redis去控制定时器删除的功能
        ScheduledFuture<?> toBeRemovedFuture = futuresMap.remove(expId);
        // 存在则删除旧的任务
        if (toBeRemovedFuture != null) {
            toBeRemovedFuture.cancel(true);
        }
        redisService.del(RedisKeyConstant.TASK_PUB_AB_EXP + expId);
    }

}
