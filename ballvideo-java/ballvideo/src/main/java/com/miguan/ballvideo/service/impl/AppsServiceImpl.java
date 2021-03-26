package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.entity.Apps;
import com.miguan.ballvideo.mapper.ClDeviceMapper;
import com.miguan.ballvideo.repositories.AppsJpaRepository;
import com.miguan.ballvideo.service.AppsService;
import com.miguan.ballvideo.vo.AppsVo;
import com.miguan.ballvideo.vo.ClDeviceVo;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.IndexModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
@Transactional
public class AppsServiceImpl implements AppsService {

    @Resource
    private ClDeviceMapper clDeviceMapper;

    @Resource
    private AppsJpaRepository appsJpaRepository;

    @Resource(name = "driveMongoTemplate")
    private MongoTemplate mongoTemplate;

    @Override
    public ResultMap uploadApps(AppsVo appsVo) {
        Map<String, Object> map = new HashMap<>();
        map.put("deviceId", appsVo.getDeviceId());
        map.put("appPackage", appsVo.getAppPackage());
        // 获取设备表对象
        ClDeviceVo deviceVo = clDeviceMapper.getDeviceByDeviceIdAppPackage(map);
        if (deviceVo == null) {
            log.error("########该设备无关联设备表Id########设备信息：" + JSONObject.toJSONString(map));
            return ResultMap.error("该设备无关联设备表Id");
        }
        // 待上传的APP列表
        List<Apps> uploadAppsList = appsVo.getAppsList();
        for (Apps apps : uploadAppsList) {
            apps.setDeviceId(deviceVo.getId());
            apps.setCreateDate(new Date());
        }
        // 查询该设备是否有上传过 APP列表   有则删除原有的 并新增加进去    无则直接新增加进去
        if (appsJpaRepository.countAppsByDeviceId(deviceVo.getId()) == 0) {
            // 批量插入
            appsJpaRepository.saveAll(uploadAppsList);
            return ResultMap.success();
        }
        appsJpaRepository.deleteAppsByDeviceId(deviceVo.getId());
        // 批量插入
        appsJpaRepository.saveAll(uploadAppsList);
        return ResultMap.success();
    }

    /**
     * 数据保存到mongodb
     * @param appsVo
     */
    @Override
    public void saveToMongodb(AppsVo appsVo) {
        Query query = new Query(Criteria.where("deviceId").is(appsVo.getDeviceId()));
        query.fields().include("deviceId");
        String collectionName = Constant.APPS_INFO_LIST_MONGODB;
        AppsVo queryVo = mongoTemplate.findOne(query, AppsVo.class, collectionName);
        try {
            if (queryVo == null) {
                String saveJsonStr = JSON.toJSONString(appsVo);
                mongoTemplate.save(saveJsonStr, collectionName);
            } else {
                Query query1 = new Query(Criteria.where("_id").is(queryVo.get_id()));
                Update update = new Update();
                update.set("deviceId", appsVo.getDeviceId());
                update.set("distinctId", appsVo.getDistinctId());
                update.set("appPackage", appsVo.getAppPackage());
                update.set("appsList", appsVo.getAppsList());
                mongoTemplate.upsert(query1, update, collectionName);
            }
        } catch (Exception e) {
            log.error("apps_info_list_Mongodb_error：{},{}",JSON.toJSONString(JSON.toJSONString(appsVo)),e);
        }
    }

    @Override
    public void createIndex() {
        String collectionName = Constant.APPS_INFO_LIST_MONGODB;
        if (mongoTemplate.collectionExists(collectionName)) {
            List<IndexModel> indexModels = new ArrayList<>();
            BasicDBObject index = new BasicDBObject();
            index.put("deviceId", 1);
            indexModels.add(new IndexModel(index));
            mongoTemplate.getCollection(collectionName).createIndexes(indexModels);
        } else {
            List<IndexModel> indexModels = new ArrayList<>();
            BasicDBObject index = new BasicDBObject();
            index.put("deviceId", 1);
            indexModels.add(new IndexModel(index));
            mongoTemplate.createCollection(collectionName).createIndexes(indexModels);
        }
    }
}
