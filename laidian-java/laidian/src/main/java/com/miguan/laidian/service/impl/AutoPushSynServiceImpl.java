package com.miguan.laidian.service.impl;

import com.cgcg.context.util.StringUtils;
import com.miguan.laidian.common.constants.AutoPushConstant;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.mapper.ClDeviceMapper;
import com.miguan.laidian.redis.service.RedisService;
import com.miguan.laidian.redis.util.RedisKeyConstant;
import com.miguan.laidian.service.AutoPushSynService;
import com.miguan.laidian.vo.ClDeviceVo;
import com.miguan.laidian.vo.mongodb.AndroidDeviceInfo;
import com.miguan.laidian.vo.mongodb.IOSDeviceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 自动推送(数据同步)
 *
 * @author xy.chen
 * @date 2019-08-09
 **/
@Slf4j
@Service("autoPushSynServiceImpl")
public class AutoPushSynServiceImpl implements AutoPushSynService {

    @Resource
    private ClDeviceMapper clDeviceMapper;

    @Resource
    private RedisService redisService;

    @Resource(name = "idmappingMongoTemplate")
    private MongoTemplate mongoTemplate;

    /**
     * status : 0 | null  表示未启动 , 1：表示启动中
     * @return
     */
    @Override
    public String startSyn() {
        String status = redisService.get(RedisKeyConstant.START_SYN_DISTINCT_STATUS);
        synchronized (this){
            if(StringUtils.isEmpty(status) || AutoPushConstant.STOP_SYN_STATUS.equals(status)){
                redisService.set(RedisKeyConstant.START_SYN_DISTINCT_STATUS,1,30000 * 60);
                redisService.set(RedisKeyConstant.START_SYN_DISTINCT_FLAG,AutoPushConstant.START_SYN_FLAG,30000 * 60);
                //目前状态：未启动。
                //启动该功能。
                String str = synAndroid();
                //synIos();
                redisService.set(RedisKeyConstant.START_SYN_DISTINCT_STATUS,0,30000 * 60);
                redisService.set(RedisKeyConstant.START_SYN_DISTINCT_FLAG,AutoPushConstant.STOP_SYN_FLAG,30000 * 60);
                return str;
            } else if(AutoPushConstant.START_SYN_STATUS.equals(status)){
                return "正在进行同步操作.请不要重复操作";
            } else {
                return "未知状态码，请联系程序员查询redis的start_syn_distinct_status值";
            }
        }
    }

    @Override
    public String stopSyn() {
        redisService.set(RedisKeyConstant.START_SYN_DISTINCT_STATUS,0,30000 * 60);
        redisService.set(RedisKeyConstant.START_SYN_DISTINCT_FLAG,AutoPushConstant.STOP_SYN_FLAG,30000 * 60);
        //停止状态 --
        return "停止成功！";
    }

    private String synAndroid() {
        // 开始条数
        int index = 0 ;
        long count = clDeviceMapper.countAllDeviceId();
        int pageSize = 10;
        while (true) {
            if(index >= count){
                break;
            }
            String stopFlag = redisService.get(RedisKeyConstant.START_SYN_DISTINCT_FLAG);
            if(AutoPushConstant.STOP_SYN_FLAG.equals(stopFlag)){
                return "部分同步成功,停止成功！";
            }
            List<ClDeviceVo> clDevices = clDeviceMapper.findAllDeviceId(index);
            log.info("开始同步第【"+index+" , "+ (index + pageSize )+ ")条数据");
            banchUpdateDevices(clDevices);
            index += pageSize;
        }
        return "同步成功！";
    }


    private void banchUpdateDevices(List<ClDeviceVo> clDevices) {
        for (ClDeviceVo vo :clDevices) {
            String stopFlag = redisService.get(RedisKeyConstant.START_SYN_DISTINCT_FLAG);
            if(AutoPushConstant.STOP_SYN_FLAG.equals(stopFlag)){
                return ;
            }
            //判断是否是ios包
            if(Constant.IOSPACKAGELIST.contains(vo.getAppType())){
                iosQueryAndUpdate(vo.getDeviceId(),vo);
            } else {
                androidQueryAndUpdate(vo.getDeviceId(),vo);
            }

        }
    }

    private boolean androidQueryAndUpdate(String deviceId,ClDeviceVo vo) {
        if (StringUtils.isNotEmpty(deviceId)) {
            log.info("同步设备："+ deviceId);
            AndroidDeviceInfo androidDeviceInfo = getAndroidDeviceInfoByFilter("number1",deviceId);
            if(androidDeviceInfo == null){
                androidDeviceInfo = getAndroidDeviceInfoByFilter("number2",deviceId);
                if(androidDeviceInfo == null){
                    androidDeviceInfo = getAndroidDeviceInfoByFilter("number3",deviceId);
                    if (androidDeviceInfo == null){
                        androidDeviceInfo = getAndroidDeviceInfoByFilter("number4",deviceId);
                        if (androidDeviceInfo == null){
                            androidDeviceInfo = getAndroidDeviceInfoByFilter("number5",deviceId);
                        }
                    }
                }
            }
            if(androidDeviceInfo == null){
                return false;
            }
            Map<String,Object>  params = new HashMap<>();
            params.put("id",vo.getId());
            params.put("distinctId",androidDeviceInfo.getDistinct_id());
            clDeviceMapper.updateDistinctId(params);//todo
            return true;
        }
        return false;
    }

    private AndroidDeviceInfo getAndroidDeviceInfoByFilter(String collumn, String deviceId){
        Query query = new Query();
        query.addCriteria(Criteria.where(collumn).is(deviceId));
        query.limit(1);
        AndroidDeviceInfo androidDeviceInfo = mongoTemplate.findOne(query, AndroidDeviceInfo.class, "android_device_info");
        if(androidDeviceInfo != null){
            return androidDeviceInfo;
        }
        return null;
    }


    private boolean iosQueryAndUpdate(String deviceId,ClDeviceVo vo) {
        if (StringUtils.isNotEmpty(deviceId)) {
            IOSDeviceInfo IOSDeviceInfo = getIosDeviceInfoByFilter("number1",deviceId);
            if(IOSDeviceInfo == null){
                IOSDeviceInfo = getIosDeviceInfoByFilter("number2",deviceId);
                if(IOSDeviceInfo == null){
                    IOSDeviceInfo = getIosDeviceInfoByFilter("number3",deviceId);
                }
            }
            if(IOSDeviceInfo == null){
                return false;
            }
            Map<String,Object>  params = new HashMap<>();
            params.put("id",vo.getId());
            params.put("distinctId",IOSDeviceInfo.getDistinctId());
            clDeviceMapper.updateDistinctId(params);
            return true;
        }
        return false;
    }

    private IOSDeviceInfo getIosDeviceInfoByFilter(String collumn, String deviceId){
        Query query = new Query();
        query.addCriteria(Criteria.where(collumn).is(deviceId));
        query.limit(1);
        IOSDeviceInfo IOSDeviceInfo = mongoTemplate.findOne(query, IOSDeviceInfo.class, "ios_device_info");
        if(IOSDeviceInfo != null){
            return IOSDeviceInfo;
        }
        return null;
    }


}