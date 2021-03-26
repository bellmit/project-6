package com.miguan.ballvideo.service.impl;


import com.miguan.ballvideo.common.task.CleanPushRunTask;
import com.miguan.ballvideo.common.task.PushArticleRunTask;
import com.miguan.ballvideo.common.task.VideoRealWeightRunTask;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.SpringTaskUtil;
import com.miguan.ballvideo.entity.PushArticle;
import com.miguan.ballvideo.service.*;
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
 * @Author shixh
 * @Date 2019/9/20
 **/
@Slf4j
@Service
public class SpringTaskServiceImpl implements SpringTaskService {

    @Resource
    private ThreadPoolTaskScheduler taskScheduler;

    private ScheduledFuture<?> scheduledFuture;

    private Map<String, ScheduledFuture<?>> futuresMap = new HashMap<>();

    @Resource
    private ClUserService userService;

    @Resource
    private PushArticleService pushArticleService;

    @Resource
    private PushArticleConfigService pushArticleConfigService;

    @Resource
    private PushArticleSendResultService pushArticleSendResultService;

    @Resource
    private FirstVideosOldService firstVideosOldService;


    @Resource
    private PushSevice pushSevice;

    @Resource
    private ClUserOpinionService clUserOpinionService;

    /**
     * 定时器初始化
     */
    @Override
    public void initTask() {
        //消息推送定时器
        this.initPushTask();
        //视频权重更新定时器
        this.initVideo();
        //垃圾清理推送定时器
        this.initCleanPushTask();
    }


    @Override
    public void initPushTask() {
        List<PushArticle> pushArticles = pushArticleService.findFixedTimeListToPush();
        for (PushArticle pushArticle : pushArticles) {
            String taskKey = String.valueOf(pushArticle.getId());
            ScheduledFuture<?> toBeRemovedFuture = futuresMap.remove(taskKey);
            // 存在则删除旧的任务
            if (toBeRemovedFuture != null) {
                toBeRemovedFuture.cancel(true);
            }
            PushArticleRunTask task = new PushArticleRunTask();
            task.setFuturesMap(futuresMap);
            task.setPushArticle(pushArticle);
            task.setPushArticleConfigService(pushArticleConfigService);
            task.setPushArticleSendResultService(pushArticleSendResultService);
            task.setUserService(userService);
            task.setPushArticleService(pushArticleService);
            task.setPushSevice(pushSevice);
            task.setClUserOpinionService(clUserOpinionService);
            scheduledFuture = taskScheduler.schedule(task, new Trigger() {
                @Override
                public Date nextExecutionTime(TriggerContext triggerContext) {
                    String pushTime = pushArticle.getPushTime();
                    log.info("推送定时器启动，本次定时发送时间是" + pushTime + ",ID:" + taskKey + ",title:" + pushArticle.getTitle());
                    return new CronTrigger(SpringTaskUtil.getCron(pushTime)).nextExecutionTime(triggerContext);
                }
            });
            futuresMap.put(String.valueOf(pushArticle.getId()), scheduledFuture);
        }
    }

    private void initVideo() {
        VideoRealWeightRunTask task= new VideoRealWeightRunTask();
        task.setFirstVideosOldService(firstVideosOldService);
        scheduledFuture = taskScheduler.schedule(task, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                int time = Global.getInt("update_real_weight");
                String con = SpringTaskUtil.getCronExpression(time);
                log.info("【视频权重更新】定时器启动，间隔时间"+time+"分钟一次"+con);
                return new CronTrigger(con).nextExecutionTime(triggerContext);
            }
        });
        futuresMap.put("videoTask", scheduledFuture);
    }

    @Override
    public void stopPushTask(Long id) {
        stopTask(String.valueOf(id));
    }

    @Override
    public void stopTask(String key) {
        ScheduledFuture<?> toBeRemovedFuture = futuresMap.remove(key);
        if (toBeRemovedFuture != null) {
            toBeRemovedFuture.cancel(true);
        }
    }

    @Override
    public void initCleanPushTask() {
        List<PushArticle> pushArticles = pushArticleService.cleanPushInfo();
        if (pushArticles != null) {
            for (PushArticle pushArticle : pushArticles) {
                String taskKey = String.valueOf(pushArticle.getId());
                ScheduledFuture<?> toBeRemovedFuture = futuresMap.remove(taskKey);
                // 存在则删除旧的任务
                if (toBeRemovedFuture != null) {
                    toBeRemovedFuture.cancel(true);
                }
                CleanPushRunTask task = new CleanPushRunTask();
                task.setFuturesMap(futuresMap);
                task.setPushArticle(pushArticle);
                task.setPushArticleConfigService(pushArticleConfigService);
                task.setPushArticleSendResultService(pushArticleSendResultService);
                task.setUserService(userService);
                task.setPushArticleService(pushArticleService);
                task.setPushSevice(pushSevice);
                task.setClUserOpinionService(clUserOpinionService);
                scheduledFuture = taskScheduler.schedule(task, new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        String pushTime = "18:00:00";
                        log.info("推送定时器启动，本次定时发送时间是" + pushTime + ",ID:" + taskKey + ",title:" + pushArticle.getTitle());
                        return new CronTrigger(SpringTaskUtil.getTimeCron(pushTime)).nextExecutionTime(triggerContext);
                    }
                });
                futuresMap.put(String.valueOf(pushArticle.getId()), scheduledFuture);
            }
        }
    }

}
