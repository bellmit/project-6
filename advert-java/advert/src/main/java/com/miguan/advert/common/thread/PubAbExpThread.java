package com.miguan.advert.common.thread;

import com.miguan.advert.common.exception.ServiceException;
import com.miguan.advert.common.util.redis.RedisService;
import com.miguan.advert.config.redis.util.RedisKeyConstant;
import com.miguan.advert.domain.service.AbFlowGroupService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Data
public class PubAbExpThread extends Thread{

    private AbFlowGroupService abflowGroupService;
    private Map<String, Object> sendInfo;
    private Integer expId;
    private String pubTime;
    private RedisService redisService;

    public PubAbExpThread(AbFlowGroupService abflowGroupService, RedisService redisService, Map<String, Object> sendInfo, Integer expId, String pubTime) {
        this.abflowGroupService = abflowGroupService;
        this.redisService = redisService;
        this.sendInfo = sendInfo;
        this.expId = expId;
        this.pubTime = pubTime;
    }

    @Override
    public void run() {
        try {
            //如果该定时器不存在了。则不推送。
            String redPubTime = redisService.get(RedisKeyConstant.TASK_PUB_AB_EXP + expId);
            if(redPubTime == null || !redPubTime.equals(pubTime)){
                return ;
            }
            abflowGroupService.sendEditState(sendInfo);
        } catch (ServiceException e) {
            log.info("定时发布失败,失败原因:"+e.getMessage());
        }
    }
}
