package com.miguan.ballvideo.rabbitMQ.listener;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.service.SysConfigService;
import com.miguan.ballvideo.service.SysService;
import com.miguan.ballvideo.vo.queue.SystemQueueVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 广播模式系统操作
 * @author shixh
 * @date 2019-12-11
 */
@Slf4j
@EnableRabbit
@Component
public class SystemMqProcessor {

    @Resource
    private SysConfigService sysConfigService;

    @Resource
    private SysService sysService;

    @RabbitListener(autoStartup="#{environment['spring.rabbitmq.open']}",queues = "#{systemQueue.name}")
    public void receiver1(String msg) {
        SystemQueueVo systemQueueVo = JSON.parseObject(msg,SystemQueueVo.class);
        //operation=flash_Cache,更新一级缓存
        if(SystemQueueVo.FLASH_CACHE.equals(systemQueueVo.getOperation())){
            sysConfigService.initSysConfig();
            log.info("---------------初始化系统内存成功!-------------------");
        }else if(SystemQueueVo.AdConfig_Cache.equals(systemQueueVo.getOperation())){
            sysService.updateAdConfigCache();
            log.info("---------------初始化广告切片算法配置内存成功!-------------------");
        }else if(SystemQueueVo.AdLadderCache.equals(systemQueueVo.getOperation())){
            sysService.updateAdLadderCache();
            log.info("---------------初始化价格阶梯广告配置内存成功!-------------------");
        }

    }
}
