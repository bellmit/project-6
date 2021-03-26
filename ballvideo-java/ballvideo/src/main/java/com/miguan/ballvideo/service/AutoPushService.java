package com.miguan.ballvideo.service;


import com.miguan.ballvideo.entity.AutoPushConfig;
import com.miguan.ballvideo.vo.AutoPushInfo;

import java.util.List;

/**
 * 自动配置Service
 * @author laiyudan
 * @date 2020-04-24
 */
public interface AutoPushService {

    void batchPush(List<AutoPushInfo> autoPushInfos, AutoPushConfig autoPushConfig);


    /**
     * 由于是集群，所有不能让内存内所有定时器去触发推送
     * @param configId
     */
    void saveAutoPushRedis(String configId);

    boolean hasAutoPushRedis(String configId);
}
