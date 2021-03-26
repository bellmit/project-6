package com.miguan.laidian.service.impl;

import com.miguan.laidian.common.util.SpringTaskUtil;
import com.miguan.laidian.entity.PushArticle;
import com.miguan.laidian.service.PushArticleService;
import com.miguan.laidian.service.PushSevice;
import com.miguan.laidian.service.SpringTaskService;
import com.miguan.laidian.springTask.thread.PushArticleRunTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * 消息推送动态定时器
 *
 * @Author shixh
 * @Date 2019/9/20
 **/
@Slf4j
@Service
public class SpringTaskServiceImpl implements SpringTaskService {

    @Resource
    private ThreadPoolTaskScheduler taskScheduler;

    @Resource
    private PushArticleService pushArticleService;

    @Resource
    private PushSevice uPushSevice;

    private ScheduledFuture<?> scheduledFuture;

    private Map<String, ScheduledFuture<?>> futuresMap = new HashMap<>();

    @Override
    public void initPushTask() {
        List<PushArticle> pushArticles = pushArticleService.findAllFixedTimeListToPush();
        for (PushArticle pushArticle : pushArticles) {
            String taskKey = String.valueOf(pushArticle.getId());
            ScheduledFuture<?> toBeRemovedFuture = futuresMap.remove(taskKey);
            // 存在则删除旧的任务
            if (toBeRemovedFuture != null) {
                toBeRemovedFuture.cancel(true);
            }
            PushArticleRunTask task = new PushArticleRunTask();
            task.setFuturesMap(futuresMap);
            task.setUPushSevice(uPushSevice);
            task.setPushArticle(pushArticle);
            task.setPushArticleService(pushArticleService);
            scheduledFuture = taskScheduler.schedule(task, new Trigger() {
                @Override
                public Date nextExecutionTime(TriggerContext triggerContext) {
                    String pushTime = pushArticle.getPushTime();
                    log.info("定时器启动，本次定时发送时间是" + pushTime + ",ID:" + taskKey + ",title:" + pushArticle.getTitle());
                    return new CronTrigger(SpringTaskUtil.getCron(pushTime)).nextExecutionTime(triggerContext);
                }
            });
            futuresMap.put(String.valueOf(pushArticle.getId()), scheduledFuture);
        }
        log.info("start timed task success ..");
    }

    @Override
    public void stopPushTask(Long id) {
        log.info("删除任务，ID：" + id);
        ScheduledFuture<?> toBeRemovedFuture = futuresMap.remove(String.valueOf(id));
        // 存在则删除旧的任务
        if (toBeRemovedFuture != null) {
            toBeRemovedFuture.cancel(true);
            log.info("定时器停止成功，ID：" + id);
        }
    }
}
