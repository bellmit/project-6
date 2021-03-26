package com.miguan.ballvideo.rabbitMQ.listener.common;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.vo.push.MqParamsVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.UUID;

/**
 * 生产者MQ调用
 * @Author suhongju
 */
@Slf4j
@Service("producerMqCallers")
public class ProducerMqCallers {

    @Resource
    private RabbitTemplate rabbitTemplate;


    public void producerMqCallers(String type, String jsonParams){
        String dataStr = null;
        try {
            dataStr = buildParam(type,jsonParams);
            rabbitTemplate.convertAndSend(RabbitMQConstant.NPUSH_PRODUCER_POOL_EXCHANGE,
                    RabbitMQConstant.NPUSH_PRODUCER_POOL_RUTEKEY, dataStr);
            log.error("【MQ-Producer】推送成功,数据:{}",dataStr);
        } catch (AmqpException e) {
            e.printStackTrace();
            log.error(StringUtils.trimToEmpty(e.getMessage()).concat("【MQ-Producer】推送异常,数据:").concat(dataStr),e);
        }

    }

    private String buildParam(String type, String jsonParams){
        MqParamsVo mqParamsVo = new MqParamsVo();
        mqParamsVo.setType(type);
        mqParamsVo.setParams(jsonParams);
        //通过UUID保证唯一性
        mqParamsVo.setBusinessId(
                UUID.randomUUID().toString().replace("-", ""));
        String dataStr = JSON.toJSONString(mqParamsVo);
        return dataStr;
    }

}
