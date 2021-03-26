package com.miguan.ballvideo.rabbitMQ.listener.common;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.PushSevice;
import com.miguan.ballvideo.vo.push.MqParamsVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.cgcg.redis.core.entity.RedisLock;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 通用大数据MQ生产消费接口
 * @Author suhongju
 */
@Slf4j
@EnableRabbit
@Component
public class ConsumerMqProcessor {

    @Resource
    private PushSevice pushService;

/*    @RabbitListener(autoStartup="#{environment['spring.rabbitmq.open']}",bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.NPUSH_CONSUMER_POOL_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.NPUSH_CONSUMER_POOL_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.NPUSH_CONSUMER_POOL_RUTEKEY)
    })*/
    public void consumerMqProcessor(String jsonMsg) {
        log.error("【MQ-Consumer】消费参数打印,数据:{}",jsonMsg);
        try {
            MqParamsVo mqParamsVo = JSON.parseObject(jsonMsg, MqParamsVo.class);
            if(mqParamsVo == null){
                log.error("【MQ-Consumer】消费异常,mqParamsVo为空,数据:{}",jsonMsg);
                return;
            }
            RedisLock redisLock = new RedisLock(RedisKeyConstant.MQ_PROCESS + mqParamsVo.getBusinessId(), RedisKeyConstant.defalut_seconds);
            if (redisLock.lock()) {
                String type = mqParamsVo.getType();
                switch (type){
                    case RabbitMQConstant.INTEREST_CAT:
                        pushService.executeInterestCat(mqParamsVo.getParams());
                        //后续有需要加的MQ放这里
                        break;
                    default:
                        log.error("【MQ-Consumer】找不到消息类型,数据:{}",jsonMsg);
                }

                log.error("【MQ-Consumer】消费成功,数据:{}",jsonMsg);
            }

        } catch (Exception e) {
            log.error(StringUtils.trimToEmpty(e.getMessage()).concat("【MQ-Consumer】消费异常,数据:").concat(jsonMsg),e);
        }
    }
}
