package com.miguan.ballvideo.common.thread;

import com.miguan.ballvideo.common.factory.PushStrategyFactory;
import com.miguan.ballvideo.common.strategy.PushStrategy;
import com.miguan.ballvideo.common.strategy.pushStrategy.DefaultStrategy;
import com.miguan.ballvideo.entity.AutoPushConfig;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.AutoPushService;
import com.miguan.ballvideo.service.ClDeviceService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.cgcg.redis.core.entity.RedisLock;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.concurrent.ScheduledFuture;


@Slf4j
@Data
public class AutoPushTaskThread implements Runnable {

    //推送配置
    private AutoPushConfig autoPushConfig;
    //推送配置
    private ClDeviceService clDeviceService;

    private ScheduledFuture scheduledFuture;

    private AutoPushService autoPushService;

    private MongoTemplate mongoTemplate;

    private String autoPushUrl;

    public AutoPushTaskThread(AutoPushConfig autoPushConfig, ClDeviceService clDeviceService,MongoTemplate mongoTemplate,AutoPushService autoPushService,ScheduledFuture scheduledFuture,String autoPushUrl){
        this.autoPushConfig = autoPushConfig;
        this.clDeviceService = clDeviceService;
        this.mongoTemplate = mongoTemplate;
        this.autoPushService = autoPushService;
        this.scheduledFuture = scheduledFuture;
        this.autoPushUrl = autoPushUrl;
    }

    @Override
    public void run() {

        RedisLock redisLock =null;
        try {
            redisLock = new RedisLock(RedisKeyConstant.PUSH_TASK+"auto_"+autoPushConfig.getId(), RedisKeyConstant.AUTO_PUSH_TASK_SECONDS);
            if (redisLock.lock()) {
                //如果存在，则不执行推送
                if(autoPushService.hasAutoPushRedis(String.valueOf(autoPushConfig.getId()))){
                    log.info("【自动推送】推送已经完成，过滤重复推送 ID:" + autoPushConfig.getId() );
                    return;
                }

                //1：根据推送配置 选择推送策略
                Class aClass = PushStrategyFactory.PUSH_STRATEGY.get(autoPushConfig.getActivityType());
                try {
                    log.info("====自动推送定时器开始====");
                    PushStrategy pushStrategy;
                    if (aClass == null) {
                        pushStrategy = new DefaultStrategy();
                    } else {
                        pushStrategy = (PushStrategy) aClass.newInstance();
                    }
                    pushStrategy.setAutoPushConfig(autoPushConfig);
                    pushStrategy.setClDeviceService(clDeviceService);
                    pushStrategy.setAutoPushService(autoPushService);
                    pushStrategy.setAutoPushUrl(autoPushUrl);
                    pushStrategy.doexecute();
                    log.info("====自动推送定时器结束====");
                } catch (Exception e) {
                    log.error("推送出现问题:" + e.getMessage() + ",ID:" + autoPushConfig.getId());
                    e.printStackTrace();
                }
                if (scheduledFuture != null) {
                    scheduledFuture.cancel(true);
                }
                //由于是集群，所有不能让内存内所有定时器去触发推送
                autoPushService.saveAutoPushRedis(String.valueOf(autoPushConfig.getId()));
            }
        } catch (Exception e){
            log.error("自动推送定时器错误：{}",e.getMessage(),e);
        } finally {
            if (redisLock!=null){
                redisLock.unlock();
            }
        }
    }
}