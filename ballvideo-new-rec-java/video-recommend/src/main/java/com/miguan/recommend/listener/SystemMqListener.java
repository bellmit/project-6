package com.miguan.recommend.listener;

import com.alibaba.fastjson.JSON;
import com.miguan.recommend.service.xy.SysConfigService;
import com.miguan.recommend.vo.SystemQueueVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
public class SystemMqListener {

    @Resource
    private SysConfigService sysConfigService;

    @RabbitListener(autoStartup = "#{environment['spring.rabbitmq.open']}", queues = "#{systemQueue.name}")
    public void receiver1(String msg) {
        SystemQueueVo systemQueueVo = JSON.parseObject(msg, SystemQueueVo.class);
        //operation=flash_Cache,更新一级缓存
        if (SystemQueueVo.FLASH_CACHE.equals(systemQueueVo.getOperation())) {
            sysConfigService.initSysConfig();
            log.info("---------------初始化系统内存成功!-------------------");
        }
    }
}
