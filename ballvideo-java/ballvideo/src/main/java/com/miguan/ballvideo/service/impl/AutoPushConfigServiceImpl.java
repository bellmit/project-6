package com.miguan.ballvideo.service.impl;

import com.cgcg.context.util.StringUtils;
import com.miguan.ballvideo.common.constants.AutoPushConstant;
import com.miguan.ballvideo.common.thread.AutoPushTaskThread;
import com.miguan.ballvideo.common.util.SpringTaskUtil;
import com.miguan.ballvideo.entity.AutoPushConfig;
import com.miguan.ballvideo.mapper.AutoPushConfigMapper;
import com.miguan.ballvideo.service.AutoPushConfigService;
import com.miguan.ballvideo.service.AutoPushService;
import com.miguan.ballvideo.service.ClDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

/**
 * 用户表ServiceImpl
 *
 * @author xy.chen
 * @date 2019-08-09
 **/
@Slf4j
@Service("autoPushConfigService")
public class AutoPushConfigServiceImpl implements AutoPushConfigService {

    @Resource
    private AutoPushConfigMapper autoPushConfigMapper;

    @Resource
    private ClDeviceService clDeviceService;


    @Value("${push.auto-push.url}")
    private String autoPushUrl;

    @Resource
    private ThreadPoolTaskScheduler taskScheduler;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private AutoPushService autoPushService;

    @Resource
    private AutoPushConfigService autoPushConfigService;

    private ScheduledFuture<?> scheduledFuture;


    //缓存推送任务键值
    private static Map<Long,List<ScheduledFuture<?>>> keyCatch = new HashMap<>();

    @Override
    public List<AutoPushConfig> queryAllByStatus(Integer status) {
        Map<String,Object> params = new HashMap<>();
        params.put("status",status);
        return autoPushConfigMapper.queryAllByStatus(params);
    }

    @Override
    public AutoPushConfig queryById(Long id) {
        return autoPushConfigMapper.queryById(id);
    }


    /**
     * 执行任务
     * @param type
     * @param desc
     */
    public void excecuteTask(String type, String desc){
        try {
            if(AutoPushConstant.ADD.equals(type)){
                //1:读取推送配置 (这边获得配置信息)
                List<AutoPushConfig> autoPushConfigs = autoPushConfigService.queryAllByStatus(AutoPushConstant.STATUS_START);
                addTask(autoPushConfigs);
            }else if(AutoPushConstant.RESET.equals(type)){
                resetTask(Long.parseLong(desc));
            }else if(AutoPushConstant.STOP.equals(type)){
                stopTask(Long.parseLong(desc));
            }
        } catch (Exception e) {
            log.error("====执行任务出错【type={},desc={}】====",type,desc);
            e.printStackTrace();
        }
    }

    /**
     * params:推送配置 (推送时间为今天的某个时间点)
     * @param autoPushConfigs
     */
    private void addTask(List<AutoPushConfig> autoPushConfigs){
        //添加定时推送任务 （for循环添加）
        for (AutoPushConfig autoPushConfig:autoPushConfigs) {
            List<ScheduledFuture<?>> keys = keyCatch.get(autoPushConfig.getId());
            if(keys != null){
                //删除已存在的定时器
                for (ScheduledFuture<?> scheduledFuture :keys) {
                    if(scheduledFuture !=  null){
                        scheduledFuture.cancel(true);
                    }
                }
            }
            //重置key
            keys = new ArrayList<>();
            String pushTime = autoPushConfig.getTriggerTime();
            if(StringUtils.isEmpty(pushTime)){
                return;
            }
            String[] pushTimes = pushTime.split(",");
            for (String pt:pushTimes) {
                //定时推送
                autoPushConfig.setTriggerTime(pt);
                AutoPushTaskThread taskThread = new AutoPushTaskThread(autoPushConfig,clDeviceService,mongoTemplate, autoPushService,scheduledFuture,autoPushUrl);
                scheduledFuture = taskScheduler.schedule(taskThread, new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        String pushTime = autoPushConfig.getTriggerTime();
                        log.info("自动推送定时器启动，本次定时发送时间是" + pushTime + ",ID:" + autoPushConfig.getId() );
                        return new CronTrigger(SpringTaskUtil.getTimeCron(pushTime)).nextExecutionTime(triggerContext);
                    }
                });
                keys.add(scheduledFuture);
            }
            keyCatch.put(autoPushConfig.getId(),keys);
        }
    }

    private boolean stopTask(Long autoPushId) {
        List<ScheduledFuture<?>> keys = keyCatch.get(autoPushId);
        if(keys != null && keys.size() > 0){
            //删除已存在的定时器
            for (ScheduledFuture<?> scheduledFuture :keys) {
                if(scheduledFuture !=  null){
                    scheduledFuture.cancel(true);
                }
            }
            keyCatch.remove(autoPushId);
            //keyCatch.put(autoPushId,new ArrayList<>());
            return true;
        }
        return false;
    }

    private void resetTask(Long autoPushId) {
        AutoPushConfig autoPushConfig = queryById(autoPushId);
        if(autoPushConfig == null){
            return;
        }
        List<ScheduledFuture<?>> keys = keyCatch.get(autoPushConfig.getId());
        if(keys != null){
            //删除已存在的定时器
            for (ScheduledFuture scheduledFuture :keys) {
                // 存在则删除旧的任务
                if (scheduledFuture != null) {
                    scheduledFuture.cancel(true);
                }
            }
        }
        if(!AutoPushConstant.TRIGGER_TYPE_TODAY.equals(autoPushConfig.getTriggerType())){
            return ;
        }
        //重置key
        keys = new ArrayList<>();
        String pushTime = autoPushConfig.getTriggerTime();
        if(StringUtils.isEmpty(pushTime)){
            return;
        }
        String[] pushTimes = pushTime.split(",");
        for (String pt:pushTimes) {
            //定时推送
            autoPushConfig.setTriggerTime(pt);
            AutoPushTaskThread taskThread = new AutoPushTaskThread(autoPushConfig,clDeviceService,mongoTemplate, autoPushService,scheduledFuture,autoPushUrl);
            scheduledFuture = taskScheduler.schedule(taskThread, new Trigger() {
                @Override
                public Date nextExecutionTime(TriggerContext triggerContext) {
                    String pushTime = autoPushConfig.getTriggerTime();
                    log.info("自动推送定时器启动，本次定时发送时间是" + pushTime + ",ID:" + autoPushConfig.getId() );
                    return new CronTrigger(SpringTaskUtil.getTimeCron(pushTime)).nextExecutionTime(triggerContext);
                }
            });
            keys.add(scheduledFuture);
        }
        keyCatch.put(autoPushConfig.getId(),keys);
    }
}