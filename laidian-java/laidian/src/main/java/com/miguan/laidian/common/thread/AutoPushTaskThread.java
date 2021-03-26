package com.miguan.laidian.common.thread;

import com.alibaba.fastjson.JSON;
import com.miguan.laidian.common.factory.PushStrategyFactory;
import com.miguan.laidian.common.strategy.PushStrategy;
import com.miguan.laidian.common.strategy.pushStrategy.DefaultPushStrategy;
import com.miguan.laidian.common.util.LaidianUtils;
import com.miguan.laidian.entity.AutoPushConfig;
import com.miguan.laidian.redis.util.RedisKeyConstant;
import com.miguan.laidian.service.AutoPushService;
import com.miguan.laidian.service.ClDeviceService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.concurrent.ScheduledFuture;


@Slf4j
@Data
public class AutoPushTaskThread implements Runnable {

    //推送配置
    private AutoPushConfig autoPushConfig;

    private ClDeviceService clDeviceService;

    private ScheduledFuture scheduledFuture;

    private AutoPushService autoPushService;

    private MongoTemplate mongoTemplate;

    private String autoPushUrl;

    public AutoPushTaskThread(AutoPushConfig autoPushConfig, ClDeviceService clDeviceService, MongoTemplate mongoTemplate, AutoPushService autoPushService, ScheduledFuture scheduledFuture, String autoPushUrl){
        this.autoPushConfig = autoPushConfig;
        this.clDeviceService = clDeviceService;
        this.mongoTemplate = mongoTemplate;
        this.autoPushService = autoPushService;
        this.scheduledFuture = scheduledFuture;
        this.autoPushUrl = autoPushUrl;
    }

    @Override
    public void run() {
        try {
            //如果存在，则不执行推送
            if(autoPushService.hasAutoPushRedis(String.valueOf(autoPushConfig.getId()))){
                log.info("【自动推送】推送已经完成，过滤重复推送 ID:" + autoPushConfig.getId() );
                return;
            }
            //1：根据推送配置 选择推送策略
            Integer strategyType = LaidianUtils.getStrategyByEvent(autoPushConfig.getEventType());
            Class aClass = PushStrategyFactory.PUSH_STRATEGY.get(strategyType);
            PushStrategy pushStrategy;
            if(aClass == null){
                pushStrategy = new DefaultPushStrategy();
            } else {
                pushStrategy = (PushStrategy)aClass.newInstance();
            }
            pushStrategy.setAutoPushConfig(autoPushConfig);
            pushStrategy.setClDeviceService(clDeviceService);
            pushStrategy.setAutoPushService(autoPushService);
            pushStrategy.setAutoPushUrl(autoPushUrl);
            pushStrategy.doexecute();
            //由于是集群，所有不能让内存内所有定时器去触发推送
            autoPushService.saveAutoPushRedis(String.valueOf(autoPushConfig.getId()));
        } catch (Exception e){
            log.info("【自动推送】推送出现问题:" + e.getMessage() + ",ID:" + autoPushConfig.getId() );
            e.printStackTrace();
        }finally {
            if(scheduledFuture!=null){
                scheduledFuture.cancel(true);
            }
        }

    }
}