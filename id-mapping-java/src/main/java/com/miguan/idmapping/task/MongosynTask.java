package com.miguan.idmapping.task;

import com.miguan.idmapping.vo.UserRefInfo;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

/**
 * @author zhongli
 * @date 2020-09-02 
 *
 */
@Component
@Slf4j
public class MongosynTask {
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    private final int maxSize = 5000;

    public void fromSysnuuid(String uuid, String collName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("uuid").is(uuid));
        UserRefInfo templateOne = mongoTemplate.findOne(query, UserRefInfo.class, collName);
        if (templateOne == null) {
            log.warn("Task同步uuid失败，未找到from. uuid is '{}'", uuid);
            return;
        }
        sysnuuid(templateOne.getId(), collName);
    }

    public void sysnuuid(ObjectId id, String collName) {
        Query query = new Query();
        if (id != null) {
            query.addCriteria(Criteria.where("_id").gt(id));
        }
        query.limit(maxSize);
        query.fields().include("uuid");
        query.with(Sort.by(Sort.Order.asc("_id")));
        List<UserRefInfo> list = mongoTemplate.find(query, UserRefInfo.class, collName);
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(e -> {
                send2MQ(e.getUuid());
            });
            if (list.size() >= maxSize) {
                sysnuuid(list.get(list.size() - 1).getId(), collName);
            }
        }
    }

    private void send2MQ(String uuid) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Task生成uuid映射数字事件发送至kafka. uuid is '{}'", uuid);
            }
            ListenableFuture<SendResult<String, String>> f = kafkaTemplate.sendDefault(uuid, uuid);
            f.addCallback(s -> {
            }, ex -> {
                log.error(String.format("Task生成uuid映射数字事件发送至kafka回应失败,uuid is '%s'", trimToEmpty(uuid)), ex);
            });
        } catch (Exception e) {
            log.error(String.format("Task生成uuid映射数字事件发送至kafka失败,uuid is '%s'", trimToEmpty(uuid)), e);
        }
    }
}
