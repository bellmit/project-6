package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.CaseFormat;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.util.DateUtil;
import com.miguan.ballvideo.common.util.StringUtil;
import com.miguan.ballvideo.entity.AdvertErrorCountLog;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.repositories.AdvertErrorCountLogRepository;
import com.miguan.ballvideo.service.AdvertErrorCountLogService;
import com.miguan.ballvideo.vo.mongodb.AdvertErrorCountLogLowerUnderscoreVo;
import com.miguan.ballvideo.vo.mongodb.AdvertErrorCountLogMongodbVo;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.IndexModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cgcg.redis.core.entity.RedisLock;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AdvertErrorCountLogServiceImpl implements AdvertErrorCountLogService {

    @Resource
    private AdvertErrorCountLogRepository advertErrorCountLogRepository;

    @Resource(name = "logMongoTemplate")
    private MongoTemplate mongoTemplate;

    @Override
    public void save(List<AdvertErrorCountLog> datas) {
        for(AdvertErrorCountLog advertErrorCountLog:datas){
            AdvertErrorCountLog advertErrorCountLogDB = advertErrorCountLogRepository
                    .findFirstByAdIdAndCreatTimeAndDeviceIdAndAppPackageAndAppVersion(
                            advertErrorCountLog.getAdId(),
                            advertErrorCountLog.getCreatTime(),
                            advertErrorCountLog.getDeviceId(),
                            advertErrorCountLog.getAppPackage(),
                            advertErrorCountLog.getAppVersion());
            try {
                if (advertErrorCountLogDB == null) {
                    advertErrorCountLogRepository.save(advertErrorCountLog);
                } else {
                    advertErrorCountLogDB.setRenderFailed(advertErrorCountLogDB.getRenderFailed() + advertErrorCountLog.getRenderFailed());
                    advertErrorCountLogDB.setRenderSuccess(advertErrorCountLogDB.getRenderSuccess() + advertErrorCountLog.getRenderSuccess());
                    advertErrorCountLogDB.setRequestFailed(advertErrorCountLogDB.getRequestFailed() + advertErrorCountLog.getRequestFailed());
                    advertErrorCountLogDB.setRequestSuccess(advertErrorCountLogDB.getRequestSuccess() + advertErrorCountLog.getRequestSuccess());
                    advertErrorCountLogDB.setShowFailed(advertErrorCountLogDB.getShowFailed() + advertErrorCountLog.getShowFailed());
                    advertErrorCountLogDB.setShowSuccess(advertErrorCountLogDB.getShowSuccess() + advertErrorCountLog.getShowSuccess());
                    advertErrorCountLogDB.setTotalNum(advertErrorCountLogDB.getTotalNum() + advertErrorCountLog.getTotalNum());
                    advertErrorCountLogDB.setAppTime(advertErrorCountLog.getAppTime());
                    advertErrorCountLogRepository.save(advertErrorCountLogDB);
                }
            } catch (Exception e) {
                log.error("AdvertErrorCountLogSave_error："+ JSON.toJSONString(advertErrorCountLog));
            }
            saveToMongodb(advertErrorCountLog);
        }
    }

    /**
     * 数据保存到mongodb
     * @param advertErrorCountLog
     */
    private void saveToMongodb(AdvertErrorCountLog advertErrorCountLog) {
        Query query = new Query(Criteria.where("ad_id").is( advertErrorCountLog.getAdId())
                .and("creat_time").is(advertErrorCountLog.getCreatTime())
                .and("device_id").is(advertErrorCountLog.getDeviceId())
                .and("app_package").is(advertErrorCountLog.getAppPackage())
                .and("app_version").is(advertErrorCountLog.getAppVersion()));
        AdvertErrorCountLogLowerUnderscoreVo mongodbVo = new AdvertErrorCountLogLowerUnderscoreVo();
        List<String> fields = StringUtil.getFiledName(mongodbVo);
        if (!CollectionUtils.isEmpty(fields)) {
            for (String str : fields) {
                query.fields().include(str);
            }
        }
        String creatTime = getCreateDayString(advertErrorCountLog.getCreatTime());
        String collectionName = Constant.ADVERT_ERROR_COUNT_LOG_MONGODB + creatTime;
        mongodbVo = mongoTemplate.findOne(query, AdvertErrorCountLogLowerUnderscoreVo.class, collectionName);
        try {
            if (mongodbVo == null) {
                AdvertErrorCountLogMongodbVo saveMongodbVo = new AdvertErrorCountLogMongodbVo();
                BeanUtils.copyProperties(advertErrorCountLog, saveMongodbVo);
                Map<String, Object> datas = getStringObjectMap(saveMongodbVo);
                mongoTemplate.save(datas, collectionName);
            } else {
                query.addCriteria(Criteria.where("_id").is(mongodbVo.get_id()));
                Update update = new Update();
                update.set("device_id", mongodbVo.getDevice_id());
                update.set("plat_key", mongodbVo.getPlat_key());
                update.set("position_id", mongodbVo.getPosition_id());
                update.set("ad_id", mongodbVo.getAd_id());
                update.set("type_key", mongodbVo.getType_key());
                update.set("app_package", mongodbVo.getApp_package());
                update.set("app_version", mongodbVo.getApp_version());
                update.set("mobile_type", mongodbVo.getMobile_type());
                update.set("render", mongodbVo.getRender());
                update.set("channel_id", mongodbVo.getChannel_id());
                update.set("sdk", mongodbVo.getSdk());
                update.set("request_success", mongodbVo.getRequest_success() + advertErrorCountLog.getRequestSuccess());
                update.set("request_failed", mongodbVo.getRequest_failed() + advertErrorCountLog.getRequestFailed());
                update.set("render_success", mongodbVo.getRender_success() + advertErrorCountLog.getRenderSuccess());
                update.set("render_failed", mongodbVo.getRender_failed() + advertErrorCountLog.getRenderFailed());
                update.set("show_success", mongodbVo.getShow_success() + advertErrorCountLog.getShowSuccess());
                update.set("show_failed", mongodbVo.getShow_failed() + advertErrorCountLog.getShowFailed());
                update.set("total_num", mongodbVo.getTotal_num() + advertErrorCountLog.getTotalNum());
                update.set("creat_time", mongodbVo.getCreat_time());
                update.set("app_time",advertErrorCountLog.getAppTime());
                mongoTemplate.upsert(query, update,  collectionName);
            }
        } catch (Exception e) {
            log.error("advertErrorCountLogMongodb_error："+ JSON.toJSONString(mongodbVo));
        }
    }

    /**
     * 日期转换，格式：yyyyMMdd
     * @param creatTime
     * @return
     */
    private String getCreateDayString(String creatTime) {
        if (StringUtils.isNotEmpty(creatTime)) {
            creatTime = creatTime.replace("-","");
        } else {
            creatTime = DateUtil.format(new SimpleDateFormat("yyyyMMdd"),new Date()).replace("-","");
        }
        return creatTime;
    }

    /**
     * 数据转json格式
     * @param mongodbVo
     * @return
     */
    private Map<String, Object> getStringObjectMap(AdvertErrorCountLogMongodbVo mongodbVo) {
        Map<String, Object> datas = new ConcurrentHashMap<>(100);
        String jsonStr = JSONObject.toJSONString(mongodbVo);
        Map<String,Object> jsonMap = JSONObject.parseObject(jsonStr);
        jsonMap.keySet().forEach(e -> datas.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e),jsonMap.get(e)));
        return datas;
    }

    /**
     * 定时器任务:创建集合和索引,每天23点执行
     */
    @Scheduled(cron = "0 0 23 * * ?")
    private void createIndexMongo() {
        RedisLock redisLock = new RedisLock(RedisKeyConstant.ADVERT_ERROR_COUNT_LOG_MONGODB, RedisKeyConstant.ADVERT_ERROR_COUNT_LOG_MONGODB_SECONDS);
        if (redisLock.lock()) {
            String creatTime = DateUtil.getDateStrBefore(1, new Date(), "yyyyMMdd");
            String collectionName = Constant.ADVERT_ERROR_COUNT_LOG_MONGODB + creatTime;
            if (mongoTemplate.collectionExists(collectionName)) {
                return;
            } else {
                List<IndexModel> indexModels = new ArrayList<>();
                BasicDBObject index = new BasicDBObject();
                index.put("ad_id", 1);
                index.put("creat_time", 1);
                index.put("device_id", 1);
                index.put("app_package", 1);
                index.put("app_version", 1);
                indexModels.add(new IndexModel(index));
                mongoTemplate.createCollection(collectionName).createIndexes(indexModels);
            }
        }
    }
}
