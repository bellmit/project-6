package com.miguan.bigdata.listener;

import com.alibaba.fastjson.JSONObject;
import com.miguan.bigdata.common.constant.RabbitMqConstants;
import com.miguan.bigdata.service.NpushIterationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@EnableRabbit
@Component
public class NpushInitListener {

    @Resource
    private NpushIterationService npushIterationService;

    @RabbitListener(autoStartup = "#{environment['spring.rabbitmq.open']}",
            group = "#{environment['spring.rabbitmq.groupId']}",
            bindings = {
                    @QueueBinding(value = @Queue(value = RabbitMqConstants.BIGDATA_POINT_NPUSH_INIT_QUEUE),
                            exchange = @Exchange(value = RabbitMqConstants.BIGDATA_POINT_NPUSH_INIT_EXCHANGE, autoDelete = "true"),
                            key = RabbitMqConstants.BIGDATA_POINT_NPUSH_INIT_RUTEKEY)
            }
    )
    public void listen(String msg) {
        try {
            log.info("NpushInitListener 监听到消息>>{}", msg);
            JSONObject json = JSONObject.parseObject(msg);
            String type = json.getString("type");
            switch (type) {
                case "npush_init_of_day":
                    npushIterationService.initDistinctNpushStateOfSomeActDay(json.getString("day"));
                    break;
                default:
                    log.warn("NpushInitListener 监听到未定义类型[{}]", type);
            }
        } catch (Exception e) {
            log.error("NpushInitListener 监听异常>>{}", msg);
            e.printStackTrace();
        }
    }
}
