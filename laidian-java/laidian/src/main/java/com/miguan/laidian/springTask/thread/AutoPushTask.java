package com.miguan.laidian.springTask.thread;

import com.alibaba.fastjson.JSON;
import com.miguan.laidian.common.constants.AutoPushConstant;
import com.miguan.laidian.redis.util.RedisKeyConstant;
import com.miguan.laidian.vo.queue.SystemQueueVo;
import lombok.extern.slf4j.Slf4j;
import org.cgcg.redis.core.entity.RedisLock;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description 每天执行一次。查询配置信息，定时推送任务。
 * @Date 2020/9/27 11:47
 **/
@Slf4j
@Component
public class AutoPushTask {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private FanoutExchange fanoutExchange;

    /**
     * 每天的凌晨10分执行
     */
    //@Scheduled(cron = "0 0/1 * * * ?")
    //@Scheduled(cron = "0 10 0 * * ?")
    public void autoPushFilter() {
        /*RedisLock redisLock = new RedisLock(RedisKeyConstant.AUTO_PUSH_TASK, RedisKeyConstant.defalut_seconds);
        if (redisLock.lock()) {
            log.info("###################################定时自动推送任务开始执行###################################");
            addTaskToMQ();
        }*/
    }

    //@PostConstruct
    public void initAutoPush() {
        /*log.info("###################################初始化定时自动推送任务###################################");
        //添加定时推送
        addTaskToMQ();*/
    }

    public void addTaskToMQ(){
        String msg = JSON.toJSONString(new SystemQueueVo(SystemQueueVo.AUTO_PUSH_CACHE + ":" +AutoPushConstant.ADD));
        rabbitTemplate.convertAndSend(fanoutExchange.getName(), "", msg);
    }

    public void stopTaskToMQ(Long autoPushId){
        String msg = JSON.toJSONString(new SystemQueueVo(SystemQueueVo.AUTO_PUSH_CACHE + ":" +AutoPushConstant.STOP,
                String.valueOf(autoPushId)));
        rabbitTemplate.convertAndSend(fanoutExchange.getName(), "", msg);
    }

    public void resetTaskToMQ(Long autoPushId){
        String msg = JSON.toJSONString(new SystemQueueVo(SystemQueueVo.AUTO_PUSH_CACHE + ":" +AutoPushConstant.RESET,
                String.valueOf(autoPushId)));
        rabbitTemplate.convertAndSend(fanoutExchange.getName(), "", msg);
    }

}
