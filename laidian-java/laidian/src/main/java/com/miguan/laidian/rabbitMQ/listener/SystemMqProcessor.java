package com.miguan.laidian.rabbitMQ.listener;

import com.alibaba.fastjson.JSON;
import com.miguan.laidian.service.AutoPushConfigService;
import com.miguan.laidian.service.SysConfigService;
import com.miguan.laidian.vo.queue.SystemQueueVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 广播模式系统操作
 *
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
    private AutoPushConfigService autoPushConfigService;

    @RabbitListener(autoStartup = "#{environment['spring.rabbitmq.open']}", queues = "#{systemQueue.name}")
    public void receiver1(String msg) {
        SystemQueueVo systemQueueVo = JSON.parseObject(msg, SystemQueueVo.class);
        //operation=flash_Cache,更新一级缓存
        if (SystemQueueVo.FLASH_CACHE.equals(systemQueueVo.getOperation())) {
            sysConfigService.initSysConfig();
            log.info("---------------初始化系统内存成功!-------------------");
        }else if(systemQueueVo.getOperation().startsWith(SystemQueueVo.AUTO_PUSH_CACHE)){
            /*String type = systemQueueVo.getOperation().split(":")[1];
            autoPushConfigService.excecuteTask(type,systemQueueVo.getDesc());
            log.info("---------------初始化自动推送定时任务配置内存成功!-------------------");*/
        }

    }
}
