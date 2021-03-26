package com.miguan.idmapping.mq;

import com.alibaba.fastjson.JSON;
import com.miguan.idmapping.common.utils.DateUtil;
import com.miguan.idmapping.entity.AutoIncId;
import com.miguan.idmapping.entity.UuidMapper;
import com.miguan.idmapping.entity.UuidMapperCH;
import com.miguan.idmapping.service.clickhourse.ClickhourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author zhongli
 * @date 2020-09-02 
 *
 */
@Component
@Slf4j
public class KafkaConsumerListener {
    @Resource
    private MongoTemplate mongoTemplate;
    @Autowired
    private ClickhourseService clickhourseService;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(autoStartup = "${spring.kafka.consumer.auto-start-up:true}", topics = "${spring.kafka.template.default-topic}")
    public void consumer(String uuid) {
        if (log.isDebugEnabled()) {
            log.debug("消费生成uuid映射数字事件，uuid is '{}'", uuid);
        }
        Query query = new Query(Criteria.where("collectionName").is("uuid_mapper"));
        Update update = new Update();
        update.inc("incid");
        FindAndModifyOptions options = new FindAndModifyOptions().upsert(true).returnNew(true);
        AutoIncId autoIncId = mongoTemplate.findAndModify(query, update, options, AutoIncId.class);
        long mid = autoIncId.getIncid();
        UuidMapper mapper = new UuidMapper();
        mapper.setUuid(uuid);
        mapper.setMid(mid);
        mapper.setUpdate_at(DateUtil.yyyy_MM_ddBHHMMSS());
        try {
            mongoTemplate.insert(mapper);
            send2Clickhouse(mapper);
        } catch (Exception e) {
            log.error(String.format("添加uuid映射失败，uuid is '%s' AND mid is '%d'", uuid, mid), e);
        }

    }

    private void send2Clickhouse(UuidMapper mapper) {
        String msg = JSON.toJSONString(mapper);
        try {
            ListenableFuture<SendResult<String, String>> f = kafkaTemplate.send("uuid-digitizing-2clickhouse", mapper.getUuid(), msg);
            f.addCallback(s -> {
            }, ex -> {
                log.error("kafka发送uuid映射关系失败: ".concat(msg), ex);
            });
        } catch (Exception e) {
            log.error("uuid映射关系发送至kafka失败: ".concat(msg), e);
        }
    }


    @KafkaListener(autoStartup = "${spring.kafka.consumer.auto-start-up:true}",
            containerFactory = "kafkaListenerContainerFactory2Clickhouse",
            topics = "uuid-digitizing-2clickhouse")
    public void consumer1(List<String> records) {
        if (!isEmpty(records)) {
            List<UuidMapperCH> list = records.stream().map(e -> JSON.parseObject(e, UuidMapperCH.class)).collect(Collectors.toList());
            clickhourseService.saveBatch(list);
        }
    }

}
