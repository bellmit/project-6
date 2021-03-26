package com.miguan.laidian.service.impl;

import com.cgcg.context.util.StringUtils;
import com.miguan.laidian.common.constants.AutoPushConstant;
import com.miguan.laidian.common.thread.AutoPushTaskThread;
import com.miguan.laidian.common.util.SpringTaskUtil;
import com.miguan.laidian.entity.AutoPushConfig;
import com.miguan.laidian.mapper.AutoPushConfigMapper;
import com.miguan.laidian.service.AutoPushConfigService;
import com.miguan.laidian.service.AutoPushService;
import com.miguan.laidian.service.ClDeviceService;
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

    @Override
    public List<AutoPushConfig> queryByEventType(Integer eventType) {
        Map<String,Object> params = new HashMap<>();
        params.put("eventType",eventType);
        return autoPushConfigMapper.queryByEventType(params);
    }

    /**
     * 执行任务
     * @param type
     * @param desc
     */
    @Override
    public void excecuteTask(String type, String desc){
        try {
            if(AutoPushConstant.ADD.equals(type)){
                //读取推送配置 (这边获得配置信息)
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
                if (AutoPushConstant.EVENT_TYPE_5003.equals(autoPushConfig.getEventType())) {
                    pushTime = "00:30:00";
                } else {
                    return;
                }
            }
            String[] pushTimes = pushTime.split(",");
            for (String pt:pushTimes) {
                //定时推送
                autoPushConfig.setTriggerTime(pt);
                AutoPushTaskThread taskThread = new AutoPushTaskThread(autoPushConfig,clDeviceService,mongoTemplate, autoPushService,scheduledFuture,autoPushUrl);
                scheduledFuture = taskScheduler.schedule(taskThread, new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        if (AutoPushConstant.EVENT_TYPE_5003.equals(autoPushConfig.getEventType())) {
                            String pushCron = " 0 30 * * * ?";
                            log.info("自动推送定时器启动，每小时30分时触发,ID:" + autoPushConfig.getId() );
                            return new CronTrigger(pushCron).nextExecutionTime(triggerContext);
                        } else {
                            String pushTime = autoPushConfig.getTriggerTime();
                            log.info("自动推送定时器启动，本次定时发送时间是" + pushTime + ",ID:" + autoPushConfig.getId() );
                            return new CronTrigger(SpringTaskUtil.getTimeCron(pushTime)).nextExecutionTime(triggerContext);
                        }
                    }
                });
                keys.add(scheduledFuture);
            }
            keyCatch.put(autoPushConfig.getId(),keys);
        }
    }

    public boolean stopTask(Long autoPushId) {
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

    public void resetTask(Long autoPushId){
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
        if(AutoPushConstant.TRIGGER_TYPE_TOMON.equals(autoPushConfig.getTriggerType())){
            return ;
        }
        //重置key
        keys = new ArrayList<>();
        String pushTime = autoPushConfig.getTriggerTime();
        if(StringUtils.isEmpty(pushTime)){
            if (AutoPushConstant.EVENT_TYPE_5003.equals(autoPushConfig.getEventType())) {
                pushTime = "00:30:00";
            } else {
                return;
            }
        }
        String[] pushTimes = pushTime.split(",");
        for (String pt:pushTimes) {
            //定时推送
            autoPushConfig.setTriggerTime(pt);
            AutoPushTaskThread taskThread = new AutoPushTaskThread(autoPushConfig,clDeviceService,mongoTemplate, autoPushService,scheduledFuture,autoPushUrl);
            scheduledFuture = taskScheduler.schedule(taskThread, new Trigger() {
                @Override
                public Date nextExecutionTime(TriggerContext triggerContext) {
                    if (AutoPushConstant.EVENT_TYPE_5003.equals(autoPushConfig.getEventType())) {
                        String pushCron = " 0 30 * * * ?";
                        log.info("自动推送定时器启动，每小时30分时触发,ID:" + autoPushConfig.getId() );
                        return new CronTrigger(pushCron).nextExecutionTime(triggerContext);
                    } else {
                        String pushTime = autoPushConfig.getTriggerTime();
                        log.info("自动推送定时器启动，本次定时发送时间是" + pushTime + ",ID:" + autoPushConfig.getId());
                        return new CronTrigger(SpringTaskUtil.getTimeCron(pushTime)).nextExecutionTime(triggerContext);
                    }
                }
            });
            keys.add(scheduledFuture);
        }
        keyCatch.put(autoPushConfig.getId(),keys);
    }
}