package com.miguan.bigdata.listener;

import com.alibaba.fastjson.JSONObject;
import com.miguan.bigdata.common.constant.RabbitMqConstants;
import com.miguan.bigdata.service.AppDeviceService;
import com.miguan.bigdata.service.ManualService;
import com.miguan.bigdata.service.NpushIterationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@EnableRabbit
@Component
public class NPushMqListener {

    @Resource
    private ManualService pushService;
    @Resource
    private NpushIterationService npushIterationService;
    @Resource
    private AppDeviceService appDeviceService;

    @RabbitListener(autoStartup = "#{environment['spring.rabbitmq.open']}",
            group = "#{environment['spring.rabbitmq.groupId']}",
            bindings = {
                    @QueueBinding(value = @Queue(value = RabbitMqConstants.NPUSH_POINT_BIGDATA_POOL_QUEUE),
                            exchange = @Exchange(value = RabbitMqConstants.NPUSH_POINT_BIGDATA_POOL_EXCHANGE, autoDelete = "true"),
                            key = RabbitMqConstants.NPUSH_POINT_BIGDATA_POOL_RUTEKEY)
            }
    )
    public void listen(String msg) {

        try {
            log.info("NPushMqListener 监听到信息>>{}", msg);
            JSONObject msgJson = JSONObject.parseObject(msg);
            String type = msgJson.getString("type");
            String businessId = msgJson.getString("businessId");
            log.debug("NPushMqListener type>>{}", type);
            String paramsString = msgJson.getString("params");
            String raplaceParams = replace(paramsString);
            log.debug("NPushMqListener paramsString>>{}", raplaceParams);
            JSONObject params = JSONObject.parseObject(replace(raplaceParams));
            switch (type) {
                case "interestCat":
                    // 手动推送
                    pushService.findAndSendToMq(type, businessId, params);
                    break;
                case "npush_stop":
                    // 迭代推,修改设备的推送状态to禁止
                    npushIterationService.updateDistinctPushStateToStop(params.getString("distinct_id"));
                    break;
                case "devChlUpd":
                    // 更新设备的推送渠道
                    appDeviceService.updateDistinctNPushChannel(params);
                    break;
                default:
                    log.warn("NPushMqListener 监听到未定义类型[{}]", type);
            }
        } catch (Exception e) {
            log.error("NPushMqListener 信息转换异常>>{}", msg);
            e.printStackTrace();
        }
    }

    private static String replace(String string) {
        String reg = "\\\\";
        Pattern pattern = Pattern.compile(reg);
        Matcher mc = pattern.matcher(string);
        return mc.replaceAll("");
    }

}
