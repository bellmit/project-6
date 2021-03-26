package com.miguan.recommend.service.mongo.impl;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.bo.CatWeightDto;
import com.miguan.recommend.common.constants.MongoConstants;
import com.miguan.recommend.entity.mongo.UserRawTags;
import com.miguan.recommend.service.mongo.UserRawTagsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service
public class UserRawTagsServiceImpl implements UserRawTagsService {

    @Resource(name = "featureMongoTemplate")
    private MongoTemplate featureMongoTemplate;

    @Override
    @Cacheable(cacheNames = "user_raw_tags", cacheManager = "getCacheManager")
    public List<UserRawTags> findByUUid(String uuid) {
        Query query = new Query();
        query.addCriteria(Criteria.where("uuid").is(uuid));
        List<UserRawTags> userRawTags = featureMongoTemplate.find(query, UserRawTags.class, this.getCollectionName());
        log.info("{} 推荐获取用户离线特征返回条数：{}", uuid, userRawTags.size());
        return userRawTags;
    }

    @Override
    public List<UserRawTags> findChooseCatByUUid(String uuid) {
        Query query = new Query();
        query.addCriteria(Criteria.where("uuid").is(uuid));
        query.addCriteria(Criteria.where("tag_id").is(MongoConstants.USER_CAT_WEIGHT));
        query.with(Sort.by(Sort.Order.asc("weight")));
        List<UserRawTags> userRawTags = featureMongoTemplate.find(query, UserRawTags.class, this.getCollectionName());
        log.info("{} 推荐获取用户离线特征返回条数：{}", uuid, userRawTags.size());
        return userRawTags;
    }

    @Override
    public List<Integer> findOfflineCatsByUUid(String uuid) {
        Query query = new Query();
        query.addCriteria(Criteria.where("uuid").is(uuid));
        query.addCriteria(Criteria.where("tag_id").is(MongoConstants.cat_fav));
        query.with(Sort.by(Sort.Order.desc("weight")));
        List<UserRawTags> offlineLabels = featureMongoTemplate.find(query, UserRawTags.class, this.getCollectionName());
        if (isEmpty(offlineLabels)) {
            log.info("{} 推荐获取用户离线兴趣分类为空", uuid);
            return null;
        }

        List<Integer> userOfflineCats = offlineLabels.stream().map(UserRawTags::getTag_value).collect(Collectors.toList());
        log.info("{} 推荐获取用户离线兴趣分类：{}", uuid, JSONObject.toJSONString(userOfflineCats));
        return userOfflineCats;
    }

    /**
     * 根据UUID获取用户的离线兴趣分类，并获取用户最感兴趣的标签
     *
     * @param uuid
     * @return
     */
    @Override
    public Integer findTopType(String uuid) {
        Query query = new Query();
        query.addCriteria(Criteria.where("uuid").is(uuid));
        query.addCriteria(Criteria.where("tag_id").is(MongoConstants.cat_fav));
        query.with(Sort.by(Sort.Order.desc("weight")));

        UserRawTags userRawTag = featureMongoTemplate.findOne(query, UserRawTags.class, this.getCollectionName());
        if (userRawTag != null) {
            return userRawTag.getTag_value();
        } else {
            return 0;
        }
    }

    @Override
    public List<CatWeightDto> findUserCatWeightsAndLog10(String uuid, Integer tagId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("uuid").is(uuid));
        query.addCriteria(Criteria.where("tag_id").is(tagId));
        query.with(Sort.by(Sort.Order.desc("weight")));
        List<UserRawTags> userRawTags = featureMongoTemplate.find(query, UserRawTags.class, this.getCollectionName());
        if (isEmpty(userRawTags)) {
            return null;
        }

        List<CatWeightDto> catWeightDtoList = new ArrayList<CatWeightDto>();
        userRawTags = userRawTags.stream().sorted(Comparator.comparing(x -> x.getWeight())).collect(Collectors.toList());
        log.debug("{} 离线分类权重：{}", uuid, JSONObject.toJSONString(userRawTags));
        Double minWeight = userRawTags.get(0).getWeight();
        log.debug("{} 最小分类权重：{}", uuid, minWeight);
        userRawTags.forEach(u -> {
            Integer catid = u.getTag_value();
            Double weight = Math.log10(u.getWeight() / minWeight) + 1;
            log.debug("{} 用户分类{}，weight:{}, 初始化权重后：{}", uuid, catid, u.getWeight(), weight);
            catWeightDtoList.add(new CatWeightDto(catid, weight));
        });
        userRawTags.clear();
        return catWeightDtoList;
    }

    private String getCollectionName() {
        LocalDate localDate = LocalDate.now();
        String collectionName = MongoConstants.user_raw_tags + localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        if (!featureMongoTemplate.collectionExists(collectionName)) {
            collectionName = MongoConstants.user_raw_tags + localDate.minusDays(2L).format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        return collectionName;
    }
}
