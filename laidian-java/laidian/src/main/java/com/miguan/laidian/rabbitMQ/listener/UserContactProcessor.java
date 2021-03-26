package com.miguan.laidian.rabbitMQ.listener;

import com.alibaba.fastjson.JSON;
import com.miguan.laidian.common.util.StringUtil;
import com.miguan.laidian.rabbitMQ.util.RabbitMQConstant;
import com.miguan.laidian.service.UserContactService;
import com.miguan.laidian.vo.UserContactVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 异步处理埋点
 *
 * @author xujinbang
 * @date 2019-11-7
 */
@Slf4j
@EnableRabbit
@Component
public class UserContactProcessor {


    @Autowired
    private UserContactService userContactService;

    @RabbitListener(autoStartup = "#{environment['spring.rabbitmq.open']}", bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.USERCONTACT_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.USERCONTACT_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.USERCONTACT_RUTE_KEY)
    })
    public void processor(String jsonMsg, @Header(AmqpHeaders.CONSUMER_QUEUE) String queue) {
        //log.info("从队列【{}】收到消息：{}", queue, jsonMsg);
        if (StringUtils.isNotBlank(jsonMsg)) {
            try {
                List<UserContactVo> list = JSON.parseArray(jsonMsg, UserContactVo.class);
                list = filter(list);
                //插入操作
                if (CollectionUtils.isNotEmpty(list)) {
                    userContactService.batchSaveUserContact(list);
                }
            } catch (Exception e) {
                //报错再输出日志
                log.error("从队列【{}】收到消息：{}", queue, jsonMsg);
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 过滤无效手机（中文，字段长度超过20），过滤用户名太长超过数据库字段
     *
     * @param datas
     * @return
     */
    public List<UserContactVo> filter(List<UserContactVo> datas) {
        List<UserContactVo> filterDatas = new ArrayList<UserContactVo>();
        for (UserContactVo userVO : datas) {
            String phone = userVO.getPhone();
            String name = userVO.getName();
            if (StringUtils.isNotEmpty(phone) && StringUtils.isNotEmpty(name)
                    && !StringUtil.isContainChinese(phone) && phone.length() < 20
                    && name.length() < 100) {
                filterDatas.add(userVO);
            }
        }
        return filterDatas;
    }
}
