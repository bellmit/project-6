package com.miguan.advert.common.task;

import com.cgcg.context.util.SpringTaskUtil;
import com.cgcg.context.util.StringUtils;
import com.google.common.collect.Maps;
import com.miguan.advert.common.exception.ServiceException;
import com.miguan.advert.common.thread.PubAbExpThread;
import com.miguan.advert.common.util.DateUtils;
import com.miguan.advert.common.util.redis.RedisService;
import com.miguan.advert.config.redis.util.RedisKeyConstant;
import com.miguan.advert.domain.mapper.FlowTestMapper;
import com.miguan.advert.domain.pojo.AdAdvertFlowConfig;
import com.miguan.advert.domain.service.AbFlowGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;


@Slf4j
@Component
public class AbExpTask {

    @Resource
    private ThreadPoolTaskScheduler taskScheduler;

    @Resource
    private AbFlowGroupService abflowGroupService;

    private ScheduledFuture<?> scheduledFuture;

    private Map<Integer, ScheduledFuture<?>> futuresMap = new HashMap<>();

    @Resource
    private RedisService redisService;

    @Resource
    FlowTestMapper flowTestMapper;


    public void initTask() {
        log.info("=====开始初始化定时器(start)=====");
        List<AdAdvertFlowConfig> adAdvertFlowConfigs = flowTestMapper.getAdvFlowConfLst(null,null,null, null);
        if(CollectionUtils.isEmpty(adAdvertFlowConfigs)){
            log.info("=====结束初始化定时器(end)=====");
            return;
        }
        List<AdAdvertFlowConfig> newAdAdvertFlows = adAdvertFlowConfigs.stream().filter(adAdvertFlowConfig -> StringUtils.isNotEmpty(adAdvertFlowConfig.getPub_time()) && DateUtils.strToDate(adAdvertFlowConfig.getPub_time(),"yyyy-MM-dd HH:mm:ss").after(new Date())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(newAdAdvertFlows)){
            return;
        }
        newAdAdvertFlows.forEach(adAdvertFlow -> {
            if(adAdvertFlow.getStatus() == 1 || StringUtils.isEmpty(adAdvertFlow.getAb_flow_id())){
                return;
            }
            List<String> appPackages = flowTestMapper.findAppPackageByPosId(String.valueOf(adAdvertFlow.getPosition_id()));
            if(CollectionUtils.isNotEmpty(appPackages)){
                String appType = appPackages.get(0);
                Map<String, Object> map = Maps.newHashMap();
                try {
                    map.put("app_id", abflowGroupService.searchAppId(appType));
                    map.put("experiment_id", adAdvertFlow.getAb_flow_id());
                    map.put("state", 1);
                    pubAbExp(map, Integer.valueOf(adAdvertFlow.getAb_flow_id()), adAdvertFlow.getPub_time());
                } catch (ServiceException e) {
                    log.error(e.getMessage());
                    return;
                }

            }
        });
    }

    public void pubAbExp(Map<String, Object> map, Integer expId, String pubTime) {
        //通过redis去控制定时器删除的功能
        ScheduledFuture<?> toBeRemovedFuture = futuresMap.remove(expId);
        // 存在则删除旧的任务
        if (toBeRemovedFuture != null) {
            toBeRemovedFuture.cancel(true);
        }
        PubAbExpThread pubThead = new PubAbExpThread(abflowGroupService,redisService,map,expId,pubTime);
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
