package com.miguan.laidian.rabbitMQ.listener;

import com.alibaba.fastjson.JSON;
import com.miguan.laidian.service.LdBuryingPointActivityService;
import com.miguan.laidian.vo.LdBuryingPointActivityVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 异步处理埋点
 * @author chenwf
 * @date 2020/5/27
 */
@Slf4j
@Component
public class BuryPointActivityMqProcessor {

    private static final String QUEUE = "ld.send.burypoint.activity.queue";
    private static final String EXCHANGE = "ld.send.burypoint.activity.exchange";
    private static final String RUTE_KEY = "ld.send.burypoint.activity.rutekey";

    private static final String LOG_KEY = "ldUserBuryingPointActivityVo_";//日志搜索关键字

    @Resource
    private LdBuryingPointActivityService ldBuryingPointActivityService;

    /**
     * 来电活动埋点消费端
     * @param jsonMsg
     * @param queue
     */
    @RabbitListener(autoStartup = "#{environment['spring.rabbitmq.open']}", bindings = {
            @QueueBinding(value = @Queue(value = QUEUE), exchange = @Exchange(value = EXCHANGE, autoDelete = "true"),
                    key = RUTE_KEY)
    })
    public void processor(String jsonMsg, @Header(AmqpHeaders.CONSUMER_QUEUE) String queue) {
        try{
            if (StringUtils.isNotBlank(jsonMsg)) {
                LdBuryingPointActivityVo ldBuryingPointActivityVo = JSON.parseObject(jsonMsg, LdBuryingPointActivityVo.class);
                String deviceId = ldBuryingPointActivityVo.getDeviceId();
                if(StringUtils.isBlank(deviceId)) {
                    log.error(LOG_KEY+"没有DeviceId"+jsonMsg);
                }
                ldBuryingPointActivityService.insert(ldBuryingPointActivityVo);
            } else {
                log.error(LOG_KEY+"数据异常:"+jsonMsg);
            }
        }catch (Exception e){
            log.error(LOG_KEY+"jsonMsg",jsonMsg);
            log.error(e.getMessage(),e);
        }
    }

}
