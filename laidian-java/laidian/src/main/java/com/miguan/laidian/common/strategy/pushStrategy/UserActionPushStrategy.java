package com.miguan.laidian.common.strategy.pushStrategy;

import com.miguan.laidian.common.strategy.PushStrategy;
import com.miguan.laidian.entity.AutoPushConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户行为推送策略
 */
@Slf4j
public class UserActionPushStrategy extends PushStrategy {

    /**
     * 默认执行策略
     */
    @Override
    public void doexecute(){
        AutoPushConfig autoPushConfig = getAutoPushConfig();
        log.info("【自动推送】=====开始推送=====："+autoPushConfig.getId());
        log.info("配置id为："+autoPushConfig.getId());
        log.info("配置类型："+autoPushConfig.getEventType());
        log.info("推送时间类型："+autoPushConfig.getTriggerType());
        log.info("推送时间："+autoPushConfig.getTriggerTime());

        log.info("=====结束推送=====："+autoPushConfig.getId());
    }
}
