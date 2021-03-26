package com.miguan.recommend.listener;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.common.constants.RabbitMqConstants;
import com.miguan.recommend.common.util.StringUtils;
import com.miguan.recommend.service.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@EnableRabbit
@Component
public class NPushMqListener {

    @Resource
    private PushService pushService;

//    @RabbitListener(autoStartup = "#{environment['spring.rabbitmq.open']}",
//            group = "#{environment['spring.rabbitmq.groupId']}",
//            bindings = {
//                    @QueueBinding(value = @Queue(value = RabbitMqConstants.NPUSH_POINT_BIGDATA_POOL_QUEUE),
//                            exchange = @Exchange(value = RabbitMqConstants.NPUSH_POINT_BIGDATA_POOL_EXCHANGE, autoDelete = "true"),
//                            key = RabbitMqConstants.NPUSH_POINT_BIGDATA_POOL_RUTEKEY)
//            }
//    )
    public void listen(String msg) {

        try {
            log.info("NPushMQListener 监听到信息>>{}", msg);
            JSONObject msgJson = JSONObject.parseObject(msg);
            String type = msgJson.getString("type");
            log.info("type>>{}", type);
            String paramsString = msgJson.getString("params");
            log.info("paramsString>>{}", paramsString);
            String raplaceParams = StringUtils.removeBackslashCharacter(paramsString);
            log.info("paramsString>>{}", raplaceParams);
            JSONObject params = JSONObject.parseObject(StringUtils.removeBackslashCharacter(raplaceParams));
            switch (type) {
                case "interestCat":
                    pushService.findAndSendToMq(type, params);
                    break;
                default:
                    log.warn("NPushMQListener 监听到未定义类型[{}]", type);
            }
        } catch (Exception e) {
            log.error("NPushMQListener信息转换异常>>{}", msg);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        String msg = "{\"businessId\":\"9afd81f89478458ba1ba8e6a606150b7\",\"params\":\"{\\\"pushId\\\":\\\"10135\\\",\\\"appPackage\\\":\\\"com.mg.xyvideo,com.mg.ggvideo\\\",\\\"catIds\\\":\\\"1,2,3,4,38,48,49,50,51,52,54,100,194,195,196,197,198,235,242,251,266,284,287,293,1000,1001,1002,1003,1004,1005,1006,1007,1008,1010,1011,1012,1014,1015,1016,1018,1021,100000,100002\\\"}\",\"type\":\"interestCat\"}";

        JSONObject jsonObject = JSONObject.parseObject(msg);
    }
}
