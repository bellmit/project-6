package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.util.DateUtil;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.adv.AdvSQLUtils;
import com.miguan.ballvideo.dynamicquery.DynamicQuery;
import com.miguan.ballvideo.entity.AdvertErrorLog;
import com.miguan.ballvideo.redis.util.IPUtils;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.repositories.AdvertErrorLogRepository;
import com.miguan.ballvideo.service.AdErrorService;
import com.miguan.ballvideo.service.RedisService;
import com.miguan.ballvideo.vo.mongodb.AdvertErrorLogMongodbVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.cgcg.redis.core.entity.RedisLock;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdErrorServiceImpl implements AdErrorService {

    @Resource
    RedisService redisService;

    @Resource
    JdbcTemplate jdbcTemplate;

    @Resource
    private AdvertErrorLogRepository advertErrorLogRepository;

    @Resource
    private DynamicQuery dynamicQuery;

    @Resource(name = "logMongoTemplate")
    private MongoTemplate mongoTemplate;

    @Override
    public void addError(String jsonMsg) {
        Integer errorLimit = Global.getInt("adv_error_limit");
        String ip = IPUtils.getHostAddress();
        String key = RedisKeyConstant.ADVERT_ERROR_LOG + ip;
        Long lpush = redisService.lpush(key, jsonMsg);
        if (lpush >= errorLimit){
            List<String> lrange = redisService.lrange(key, 0, -1);
            if(CollectionUtils.isEmpty(lrange))return;
            List<List<String>> groups = Lists.partition(lrange, 300);
            for (List<String> group : groups) {
                List<AdvertErrorLog> collect = group.stream().map(e -> JSON.parseObject(e, AdvertErrorLog.class)).collect(Collectors.toList());
                try {
                    String sql = AdvSQLUtils.splicingSQL(collect);
                    jdbcTemplate.update(sql);
                } catch (Exception e) {
                    String errorKey = RedisKeyConstant.ADVERT_ERROR_LOG + DateUtil.format(new SimpleDateFormat("yyyy-MM-dd"), new Date());
                    for (String s : group) {
                        redisService.lpush(errorKey, s);
                    }
                }
                saveToMongodbBatch(collect);
            }
            redisService.del(key);
        }
    }

    @Scheduled(cron = "0 0 */2 * * ?")
    public void searchAndClearWrongDatas() {
        String errorKey = RedisKeyConstant.ADVERT_ERROR_LOG + DateUtil.format(new SimpleDateFormat("yyyy-MM-dd"), new Date());
        RedisLock redisLock = new RedisLock(RedisKeyConstant.SEARCH_AND_CLEAR_WRONG_DATAS_LOCK, RedisKeyConstant.SEARCH_AND_CLEAR_WRONG_DATAS_SECONDS);
        if (redisLock.lock()) {
            List<String> datas = redisService.lrange(errorKey, 0, -1);
            if (CollectionUtils.isEmpty(datas)) return;
            List<AdvertErrorLog> advertErrorLogs = datas.stream().map(e -> JSON.parseObject(e, AdvertErrorLog.class)).collect(Collectors.toList());
            for (AdvertErrorLog advertErrorLog : advertErrorLogs) {
                try {
                    advertErrorLogRepository.save(advertErrorLog);
                } catch (Exception e) {
                    String dataJson = JSON.toJSONString(advertErrorLog);
                    log.error("searchAndClearWrongDatas_error:" + dataJson);
                    log.error("searchAndClearWrongDatas_error:" + e.getMessage(), e);
                    redisService.lpush(RedisKeyConstant.ADVERT_ERROR_LOG, dataJson);
                }
            }
            redisService.del(errorKey);
            mongodbWrongDatas();
            redisLock.unlock();
        }
    }

    /**
     * 数据保存到mongodb（批量）
     * @param advertErrorLogList
     */
    private void saveToMongodbBatch(List<AdvertErrorLog> advertErrorLogList) {
        for (AdvertErrorLog advertErrorLog : advertErrorLogList) {
            AdvertErrorLogMongodbVo mongodbVo = new AdvertErrorLogMongodbVo();
            BeanUtils.copyProperties(advertErrorLog, mongodbVo);
            mongodbVo.setCreatTime( DateUtil.parseDateToStr(advertErrorLog.getCreatTime(), DateUtil.DATEFORMAT_STR_010));
            if(mongodbVo.getAppTime()==null){
                mongodbVo.setAppTime(String.valueOf(advertErrorLog.getCreatTime().getTime()));
            }
            saveToMongodbSingle(mongodbVo);
        }
    }

    /**
     * 数据保存到mongodb（单个）
     * @param mongodbVo
     */
    private void saveToMongodbSingle(AdvertErrorLogMongodbVo mongodbVo) {
        try {
            Map<String, Object> datas = getStringObjectMap(mongodbVo);
            String today = mongodbVo.getCreatTime().replace("-","").substring(0, 8);
            mongoTemplate.insert(datas, Constant.ADVERT_ERROR_LOG_MONGODB+ today);
        } catch (Exception e) {
            String errorKey = RedisKeyConstant.ADVERT_ERROR_LOG_MONGODB;
            String jsonStr = JSONObject.toJSONString(mongodbVo);
            redisService.lpush(errorKey, jsonStr);
        }
    }

    /**
     * 数据转json格式
     * @param mongodbVo
     * @return
     */
    private Map<String, Object> getStringObjectMap(AdvertErrorLogMongodbVo mongodbVo) {
        Map<String, Object> datas = new ConcurrentHashMap<>(100);
        String jsonStr = JSONObject.toJSONString(mongodbVo);
        Map<String,Object> jsonMap = JSONObject.parseObject(jsonStr);
        jsonMap.keySet().forEach(e -> datas.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e),jsonMap.get(e)));
        return datas;
    }

    /**
     * 广告错误日志信息，保存到Mongodb，保存失败再次保存
     */
    public void mongodbWrongDatas() {
        String errorKey = RedisKeyConstant.ADVERT_ERROR_LOG_MONGODB;
        List<String> datas = redisService.lrange(errorKey, 0, -1);
        if (CollectionUtils.isEmpty(datas)) return;
        redisService.del(errorKey);
        List<AdvertErrorLogMongodbVo> voList = datas.stream().map(e -> JSON.parseObject(e, AdvertErrorLogMongodbVo.class)).collect(Collectors.toList());
        for (AdvertErrorLogMongodbVo vo : voList) {
            try {
                saveToMongodbSingle(vo);
            } catch (Exception e) {
                String dataJson = JSON.toJSONString(vo);
                log.error("advertErrorLogMongodb_error:" + dataJson);
                log.error("advertErrorLogMongodb_error:" + e.getMessage(), e);
                redisService.lpush(RedisKeyConstant.ADVERT_ERROR_LOG_MONGODB, dataJson);
            }
        }
    }

    /*@Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void deleteAdvertErrorLoglDatas() {
        RedisLock redisLock = new RedisLock(RedisKeyConstant.ADVERT_ERROR_LOG_DELETE, RedisKeyConstant.ADVERT_ERROR_LOG_SECONDS);
        if (redisLock.lock()) {
            log.info("广告错误日志数据删除开始！");
            String sql = "delete from ad_error_1 where TO_DAYS(creat_time) < TO_DAYS(NOW())-3 ";
            int result = dynamicQuery.nativeExecuteUpdate(sql);
            log.info("广告错误日志数据删除完成："+result+"条数据。");
        }
    }*/
}
