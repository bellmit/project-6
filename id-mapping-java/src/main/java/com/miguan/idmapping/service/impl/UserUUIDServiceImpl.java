package com.miguan.idmapping.service.impl;

import com.miguan.idmapping.common.enums.UserFromEnums;
import com.miguan.idmapping.dto.RegAnonymousDto;
import com.miguan.idmapping.vo.UserRefInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

/**
 * @author zhongli
 * @date 2020-08-26 
 *
 */
@Service
@Slf4j
public class UserUUIDServiceImpl {
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    public String add(UserRefInfo userRefInfo, String collectionName) {
        userRefInfo.setUuid(buildAnonymousId());
        mongoTemplate.insert(userRefInfo, collectionName);
        sendUUID2MQ(userRefInfo.getUuid());
        return userRefInfo.getUuid();
    }

    private void sendUUID2MQ(String uuid) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("生成uuid映射数字事件发送至kafka. uuid is '{}'", uuid);
            }
            ListenableFuture<SendResult<String, String>> f = kafkaTemplate.sendDefault(uuid, uuid);
            f.addCallback(s -> {
            }, ex -> {
                log.error(String.format("生成uuid映射数字事件发送至kafka回应失败,uuid is '%s'", trimToEmpty(uuid)), ex);
            });
        } catch (Exception e) {
            log.error(String.format("生成uuid映射数字事件发送至kafka失败,uuid is '%s'", trimToEmpty(uuid)), e);
        }
    }

    private String buildAnonymousId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public String getDeviceId(UserFromEnums typeEnum, RegAnonymousDto regDeviceInfo) {
        if (typeEnum == UserFromEnums.ANDROID) {
            //androidId serial imei oaid
            return StringUtils.firstNonBlank(regDeviceInfo.getNumber2(), regDeviceInfo.getNumber1(),
                    regDeviceInfo.getNumber3(), regDeviceInfo.getNumber4());
        }
        if (typeEnum == UserFromEnums.IOS) {
            return regDeviceInfo.getNumber1();
        }
        return null;
    }
}
