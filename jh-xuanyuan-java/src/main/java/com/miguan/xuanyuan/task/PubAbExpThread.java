package com.miguan.xuanyuan.task;

import com.miguan.xuanyuan.common.constant.RedisKeyConstant;
import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.service.AbExpService;
import com.miguan.xuanyuan.service.AbPlatFormService;
import com.miguan.xuanyuan.service.common.RedisService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Data
public class PubAbExpThread extends Thread{

    private AbExpService abExpService;
    private Map<String, Object> sendInfo;
    private Integer expId;
    private String pubTime;
    private RedisService redisService;

    public PubAbExpThread(AbExpService abExpService, RedisService redisService, Map<String, Object> sendInfo, Integer expId, String pubTime) {
        this.abExpService = abExpService;
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
            abExpService.sendEditState(sendInfo);
        } catch (ServiceException e) {
            log.info("定时发布失败,失败原因:"+e.getMessage());
        }
    }
}
