package com.miguan.laidian.redis.mq;

import com.alibaba.fastjson.JSON;
import com.miguan.laidian.redis.util.RedisKeyConstant;
import com.miguan.laidian.redis.util.RedisTopicConstant;
import com.miguan.laidian.service.UserContactService;
import com.miguan.laidian.vo.UserContactVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author shixh
 * @Date 2019/9/3
 **/
@Component
public class ReceiverRedisMessage {

    public static Log log = LogFactory.getLog(ReceiverRedisMessage.class);

    @Resource
    private StringRedisTemplate template;

    @Autowired
    private UserContactService userContactService;

    /**
     * 通讯录队列
     */
    public void userContact(String jsonMsg) {
        log.info("[开始消费REDIS消息队列"+ RedisTopicConstant.TOPIC_USERCONTACT_MQ+"数据...]");
        try {
            log.info("jsonMsg="+jsonMsg);
            if (StringUtils.isNotBlank(jsonMsg) && jsonMsg.contains(RedisKeyConstant._MQ_)) {
                String[] msg_arrA = jsonMsg.split(RedisKeyConstant._MQ_);
                String uuid = msg_arrA[0];
                String status = template.opsForValue().get(RedisKeyConstant.UUID_MQ + uuid);
                if (RedisTopicConstant.open.equals(status)) {
                    List<UserContactVo> list = JSON.parseArray(msg_arrA[1], UserContactVo.class);
                    template.opsForValue().set(RedisKeyConstant.UUID_MQ + uuid, RedisTopicConstant.closed, 60, TimeUnit.SECONDS);
                    //保存通讯录信息
                    userContactService.batchSaveUserContact(list);
                    log.info("[消费REDIS消息队列" + RedisTopicConstant.TOPIC_USERCONTACT_MQ + "数据成功.]");
                } else {
                    log.info("[消费REDIS消息队列" + RedisTopicConstant.TOPIC_USERCONTACT_MQ + "数据失败.已被消费]");
                }
            }
        } catch (Exception e) {
            log.error("[消费REDIS消息队列"+RedisTopicConstant.TOPIC_USERCONTACT_MQ+"数据失败，失败信息:{}]", e);
        }
    }
}
