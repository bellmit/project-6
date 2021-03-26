package com.miguan.recommend.service.mongo.impl;

import com.miguan.recommend.common.constants.MongoConstants;
import com.miguan.recommend.entity.mongo.UserOfflineLabel;
import com.miguan.recommend.service.mongo.UserOfflineLabelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class UserOfflineLabelServiceImpl implements UserOfflineLabelService {

    @Resource(name = "featureMongoTemplate")
    private MongoTemplate mongoTemplate;

    @Override
    //@Cacheable(cacheNames = "user_offline_label", cacheManager = "getCacheManager")
    public List<UserOfflineLabel> findByUUid(String uuid) {
        Query query = new Query();
        query.addCriteria(Criteria.where("uuid").is(uuid));
        List<UserOfflineLabel> offlineLabels = mongoTemplate.find(query, UserOfflineLabel.class, this.getCollectionName());
        log.info("{} 推荐获取用户离线特征返回条数：{}", uuid, offlineLabels.size());
        return offlineLabels;
    }

    private String getCollectionName(){
        LocalDate localDate = LocalDate.now();
        String collectionName = MongoConstants.userOffline_label + localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (!mongoTemplate.collectionExists(collectionName)) {
            collectionName = MongoConstants.userOffline_label + localDate.minusDays(1L).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        return collectionName;
    }
}
