package com.miguan.laidian.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.miguan.laidian.common.util.DateUtil;
import com.miguan.laidian.common.util.Global;
import com.miguan.laidian.common.util.IPUtils;
import com.miguan.laidian.common.util.adv.AdvSQLUtils;
import com.miguan.laidian.entity.AdvertErrorLog;
import com.miguan.laidian.redis.service.RedisService;
import com.miguan.laidian.redis.util.RedisKeyConstant;
import com.miguan.laidian.repositories.AdvertErrorLogRepository;
import com.miguan.laidian.service.AdErrorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.cgcg.redis.core.entity.RedisLock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdErrorServiceImpl implements AdErrorService {

    @Resource
    private RedisService redisService;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private AdvertErrorLogRepository advertErrorLogRepository;

    @Override
    public void addError(String jsonMsg) {
        int bacthNum = Global.getInt("ad_error_bacth_num", "xld");//xld,wld共用
        String ip = IPUtils.getHostAddress();
        String key = RedisKeyConstant.ADVERT_ERROR_LOG + ip;
        Long lpush = redisService.lpush(key, jsonMsg);
        if (lpush >= bacthNum) {
            List<String> lrange = redisService.lrange(key, 0, -1);
            if (CollectionUtils.isEmpty(lrange)) return;
            List<List<String>> groups = Lists.partition(lrange, 300);
            for (List<String> group : groups) {
                try {
                    List<AdvertErrorLog> collect = group.stream().map(e -> JSON.parseObject(e, AdvertErrorLog.class)).collect(Collectors.toList());
                    String sql = AdvSQLUtils.splicingSQL(collect);
                    jdbcTemplate.update(sql);
                } catch (Exception e) {
                    //log.error(e.getMessage(), e);这里不用打印，在定时器里到具体条目再打印 addshixh 0516
                    String errorKey = RedisKeyConstant.ADVERT_ERROR_LOG + DateUtil.format(new SimpleDateFormat("yyyy-MM-dd"), new Date());
                    for (String s : group) {
                        redisService.lpush(errorKey, s);
                    }
                }
            }
            redisService.del(key);
        }
    }

    @Scheduled(cron = "0 0 */2 * * ?")
    public void searchAndClearWrongDatas() {
        String dateStr = DateUtil.format(new SimpleDateFormat("yyyy-MM-dd"), new Date());
        advWrongDatas(dateStr);
    }

    @Override
    public void advWrongDatas(String dateStr) {
        String errorKey = RedisKeyConstant.ADVERT_ERROR_LOG + dateStr;
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
                    redisService.lpush(RedisKeyConstant.ADVERT_ERROR_LOG_ALL + "", dataJson);
                }
            }
            redisService.del(errorKey);
            redisLock.unlock();
        }
    }

    /*@Scheduled(cron = "0 0 14 * * ?")
    @Transactional
    public void deleteAdvertErrorLoglDatas() {
        RedisLock redisLock = new RedisLock(RedisKeyConstant.ADVERT_ERROR_LOG_DELETE, RedisKeyConstant.ADVERT_ERROR_LOG_SECONDS);
        if (redisLock.lock()) {
            log.info("广告错误日志数据删除开始！");
            String sql = "delete from ad_error where TO_DAYS(creat_time) < TO_DAYS(NOW())-3 ";
            int result = dynamicQuery.nativeExecuteUpdate(sql);
            log.info("广告错误日志数据删除完成："+result+"条数据。");
        }
    }*/
}
