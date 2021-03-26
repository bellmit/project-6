package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.common.constants.AutoPushConstant;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.constants.PushArticleConstant;
import com.miguan.ballvideo.common.enums.PushChannel;
import com.miguan.ballvideo.common.enums.PushChannelNum;
import com.miguan.ballvideo.common.exception.ServiceException;
import com.miguan.ballvideo.common.util.DateUtil;
import com.miguan.ballvideo.common.util.HttpUtil;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.common.util.StringUtil;
import com.miguan.ballvideo.common.util.push.PushUtil;
import com.miguan.ballvideo.entity.AutoPushConfig;
import com.miguan.ballvideo.entity.PushArticle;
import com.miguan.ballvideo.entity.PushArticleConfig;
import com.miguan.ballvideo.mapper.PushVideoMapper;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.*;
import com.miguan.ballvideo.vo.*;
import com.miguan.ballvideo.vo.mongodb.AutoPushLog;
import com.miguan.message.push.utils.huawei.messaging.SendResponce;
import com.vivo.push.sdk.notofication.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 自动推送
 *
 * @author xy.chen
 * @date 2019-08-09
 **/
@Slf4j
@Service("autoPushService")
public class AutoPushServiceImpl implements AutoPushService {

    @Resource(name = "xypushMongoTemplate")
    private MongoTemplate mongoTemplate;

    @Resource
    private PushArticleConfigService pushArticleConfigService;

    @Resource
    private PushArticleSendResultService pushArticleSendResultService;

    @Resource
    private PushArticleMobileService pushArticleMobileService;

    @Resource
    private RedisService redisService;

    @Resource
    private FirstVideosService firstVideosService;
    @Resource
    private PushVideoMapper pushVideoMapper;

    @Value("${push.auto-push.url}")
    private String autoPushUrl;

    @Override
    public void batchPush(List<AutoPushInfo> autoPushInfos, AutoPushConfig autoPushConfig){
        log.info("【自动推送】===batchPush.autoPushConfig : {}",JSON.toJSONString(autoPushConfig));
        for (AutoPushInfo autoPushInfo:autoPushInfos) {
            try {
                PushArticle pushArticle = fillArtice(autoPushInfo,autoPushConfig);
                autoPushInfo(pushArticle,autoPushInfo.getTokens(),autoPushInfo.getConnTokenDistinct());
            } catch (ServiceException e){
                log.error("推送业务失败："+e.getMessage());
            } catch (Exception e){
                log.error("推送失败："+e.getMessage());
            }
        }

    }

    public void autoPushInfo(PushArticle pushArticle, Map<String, List<String>> tokensMap, Map<String, List<String>> dstinctsMap){
        Map<String, String> param = PushUtil.getParaMap(pushArticle,1);
        Map<String, Object> pushParams = PushUtil.getExpireTime(pushArticle.getExpireTime());
        pushParams.put("pushArticle", pushArticle);
        //pushParams.put("packageName", "com.mg.xyvideo.module.main.MainActivity");
        puthAllPlatform(pushParams, param, tokensMap,dstinctsMap);
    }

    /**
     * 填充推送素材
     * @param autoPushInfo
     * @param config
     * @return
     */
    private PushArticle fillArtice(AutoPushInfo autoPushInfo, AutoPushConfig config) throws ServiceException {
        PushArticle pushArticle = new PushArticle();
        //等做了埋点后，再将该数据存到对应的表中
        pushArticle.setId(config.getId());
        pushArticle.setTitle(config.getTitle());
        FirstVideos firstVideos = firstVideosService.getFirstVideosById(autoPushInfo.getVideoId());
        if(firstVideos == null){
            throw new ServiceException("无效的视频id");
        }
        if(AutoPushConstant.ARTICLE_TYPE_CUSTOM.equals(config.getType())){
            pushArticle.setNoteContent(changeStr(config.getContent(),64));
        } else {
            pushArticle.setNoteContent(changeStr(firstVideos.getTitle(),64));
        }
        pushArticle.setThumbnailUrl(firstVideos.getBsyImgUrl());
        pushArticle.setAppPackage(autoPushInfo.getAppPackage());
        pushArticle.setExpireTime("4");//厂家给手机推的过期时间 4h
        pushArticle.setType(3);//现在仅有短视频
        pushArticle.setUserType(PushArticleConstant.USER_TYPE_ALL_USER);
        pushArticle.setTypeValue(autoPushInfo.getVideoId() + "");
        //pushArticle.setPushTime(DateUtil.parseDateToStr(new Date(),"yyyy-MM-dd HH:mm:ss"));
        String time = config.getTriggerTime();
        if (StringUtils.isEmpty(time)) {
            time = DateUtil.parseDateToStr(new Date(), "HH:mm:ss");
        }
        String today = DateUtil.parseDateToStr(new Date(), "yyyy-MM-dd") + " " + time;
        pushArticle.setPushTime(today);
//        firstVideosService.(videoId);
        return pushArticle;
    }

    private String changeStr(String str , int length){
        if(StringUtils.isEmpty(str)){
            return "";
        }
        if(str.length()>=length){
            return str.substring(0,length-5)+"....";
        } else {
            return str;
        }
    }


    public void puthAllPlatform(Map<String, Object> pushParams, Map<String, String> param, Map<String, List<String>> tokensMap, Map<String, List<String>> dstinctsMap) {
        try {
            //华为广播推送
            this.huaweiPush(pushParams, param, tokensMap,dstinctsMap);
            //vivo广播推送
            this.vivoPush(pushParams, param, tokensMap,dstinctsMap);
            //oppo广播推送
            this.oppoPush(pushParams, param, tokensMap,dstinctsMap);
            //小米广播推送
            this.xiaomiPush(pushParams, param, tokensMap,dstinctsMap);
            //友盟广播推送
            this.youMengPush(pushParams, param, tokensMap,dstinctsMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //友盟推送
    private ResultMap youMengPush(Map<String, Object> pushParams, Map<String, String> param, Map<String, List<String>> tokensMap, Map<String, List<String>> dstinctsMap) {

        pushParams.put("packageName", "com.mg.xyvideo.module.main.MainActivity");//仅友盟用到
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        List<String> youmenTokens = tokensMap.get(PushChannel.YouMeng.name());
        if (CollectionUtils.isEmpty(youmenTokens)) {
            log.error("youmenTokens为空，不调用推送接口");
            return null;
        }

        String mobileType = Constant.ANDROID;
        if (Constant.IOSPACKAGELIST.contains(pushArticle.getAppPackage())) {
            mobileType = Constant.IOS;
        }

        PushArticleConfig youMeng = pushArticleConfigService.findPushArticleConfig(PushChannel.YouMeng.name(), mobileType, pushArticle.getAppPackage());
        log.info("【自动推送】友盟推送 配置 ===PushArticleConfig===: {}",JSON.toJSONString(youMeng));
        if (youMeng == null) {
            log.error("友盟推送参数未配置！");
            return ResultMap.error("友盟推送参数未配置！");
        };

        pushParams.put(PushChannel.YouMeng.name(), youMeng);
        List<Map> resultList;
        if (Constant.IOSPACKAGELIST.contains(pushArticle.getAppPackage())) {

            resultList = pushArticleMobileService.youMengIosPushInfoByToken(pushParams, param,youmenTokens);
        } else {
            resultList = pushArticleMobileService.youMengAndroidPushInfoByToken(pushParams, param,youmenTokens);
        }
        log.info("【自动推送】友盟推送 结果 ===youMeng push===: {}",JSON.toJSONString(resultList));
        if (CollectionUtils.isEmpty(resultList)) {
            log.error("友盟推送失败！");
            if(CollectionUtils.isNotEmpty(youmenTokens)){
                saveSendInfo(dstinctsMap, pushArticle, 0,PushChannel.YouMeng.name(),"0");
            }
            return ResultMap.error("友盟推送失败！");
        }

        int count = 0;
        int index = 0;
        int countYoumen = PushChannelNum.val(PushChannelNum.YouMeng);
        for (Map mapData : resultList) {
            index ++;
            String taskId = "";
            if (mapData.get("task_id") != null) {
                taskId = mapData.get("task_id").toString();
            } else {
                if (mapData.get("msg_id") != null) {
                    taskId = mapData.get("msg_id").toString();
                }
            }
            //saveSendResultInfo(pushArticle, taskId, PushChannel.YouMeng.name());
            if(index==resultList.size()){
                count += youmenTokens.size() - ((resultList.size() - 1) * countYoumen) ;
            } else {
                count += countYoumen;
            }
        }
        if(CollectionUtils.isNotEmpty(youmenTokens) && count > 0){
            saveSendInfo(dstinctsMap, pushArticle, count,PushChannel.YouMeng.name(),"1");
        }
        return null;
    }

    //华为推送
    private ResultMap huaweiPush(Map<String, Object> pushParams, Map<String, String> param,
                                 Map<String, List<String>> tokensMap, Map<String, List<String>> dstinctsMap) {
        log.info("########HuaWei华为用户推送########");
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushArticleConfig huaWei = pushArticleConfigService.findPushArticleConfig(PushChannel.HuaWei.name(), Constant.ANDROID, pushArticle.getAppPackage());
        log.info("【自动推送】华为推送 配置 ===PushArticleConfig===: {}",JSON.toJSONString(huaWei));
        if (huaWei == null) {
            log.error("推送渠道："+ PushChannel.HuaWei.name());
            log.error("华为推送参数未配置！{}",pushArticle.getAppPackage());
            return ResultMap.error("华为推送参数未配置！");
        }
        pushParams.put(PushChannel.HuaWei.name(), huaWei);
        List<String> list = tokensMap.get(PushChannel.HuaWei.name());
        if (CollectionUtils.isEmpty(list)) {
            log.error("huaWeiTokens为空，不调用推送接口");
            return null;
        }
        List<SendResponce> sendResponceList = pushArticleMobileService.huaweiPushInfo(pushParams, param, list);
        log.info("【自动推送】华为推送 结果 ===huapush===: {}",JSON.toJSONString(sendResponceList));
        if (CollectionUtils.isEmpty(sendResponceList)) {
            if(CollectionUtils.isNotEmpty(list)){
                saveSendInfo(dstinctsMap, pushArticle, 0,PushChannel.HuaWei.name(),"0");
            }
            log.error("华为推送失败！");
            return ResultMap.error("华为推送失败！");
        }
        int count = 0;
        int index = 0;
        int countHuawei = PushChannelNum.val(PushChannelNum.HuaWei);
        for (SendResponce sendResponce : sendResponceList) {
            index ++;
            if (sendResponce != null) {
                //saveSendResultInfo(pushArticle, sendResponce.getRequestId(), PushChannel.HuaWei.name());
                if(index==sendResponceList.size()){
                    count += list.size() - ((sendResponceList.size() - 1) * countHuawei) ;
                } else {
                    count += countHuawei;
                }
            }
        }
        if(CollectionUtils.isNotEmpty(list) && count > 0){
            saveSendInfo(dstinctsMap, pushArticle, count,PushChannel.HuaWei.name(),"1");
        }
        return null;
    }

    private void updateSendNum(PushArticle pushArticle, int size) {
        try{
            int videoId = Integer.valueOf(pushArticle.getTypeValue());
            Map<String,Object> param = new HashMap<>();
            param.put("videoId",videoId);
            param.put("playEndNum",size);
            pushVideoMapper.updateSenNumByVideoId(param);
        } catch (Exception e ){
            log.error("更新推送量失败！updateSendNum");
        }
    }

    private void sycToBdrep(List<AutoPushLog> autoPushLogs) {
        if(CollectionUtils.isEmpty(autoPushLogs)){
            return ;
        }
        Map<String,String> param = new HashMap<>();
        param.put("arrayList",parseAutoPushLogs(autoPushLogs));
        HttpUtil.send(autoPushUrl + "/api/push/video/syncAutoPushLog",param,true,"UTF-8");
        log.info("【自动推送】保存推送日志到大数据，syncAutoLdPushLog：{}",JSON.toJSONString(param));

    }

    private String parseAutoPushLogs(List<AutoPushLog> autoPushLogs) {
        StringBuilder sb = new StringBuilder();
        int flag = 0 ;
        for (AutoPushLog autoPushLog:autoPushLogs) {
            if(flag == 0){
                flag ++ ;
                sb.append(autoPushLog.getDistinct_id() + ":" +autoPushLog.getVideo_id() + ":" + autoPushLog.getApp_package());
            } else {
                sb.append(";" + autoPushLog.getDistinct_id() + ":" +autoPushLog.getVideo_id() + ":" + autoPushLog.getApp_package());
            }
        }
        return sb.toString();
    }

    //vivo推送
    private ResultMap vivoPush(Map<String, Object> pushParams, Map<String, String> param,
                               Map<String, List<String>> tokensMap, Map<String, List<String>> dstinctsMap) {
        log.info("########vivo用户推送########");
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
//        if (PushArticleConstant.USER_TYPE_ASSIGN_USER.equals(pushArticle.getUserType()) && org.apache.commons.lang3.StringUtils.isBlank(pushArticle.getDeviceTokens())) {
//            return null;
//        }
        PushArticleConfig vivo = pushArticleConfigService.findPushArticleConfig(PushChannel.VIVO.name(), Constant.ANDROID, pushArticle.getAppPackage());
        log.info("【自动推送】vivo推送 配置 ===PushArticleConfig====: {}",JSON.toJSONString(vivo));
        if (vivo == null) {
            log.error("vivo推送参数未配置！{}",pushArticle.getAppPackage());
            return ResultMap.error("vivo推送参数未配置！");
        }
        String limitVal = redisService.get(RedisKeyConstant.VIVO_LIMIT_TOKEN + vivo.getAppId(),String.class);
        if(limitVal != null){
            log.error("vivo推送已被限制,不能推送！");
            return null;
        }

        pushParams.put(PushChannel.VIVO.name(), vivo);
        List<Result> resultList = new ArrayList<>();

        List<String> list = tokensMap.get(PushChannel.VIVO.name());
        if (CollectionUtils.isEmpty(list)) {
            log.error("vivoTokens为空，不调用推送接口");
            return null;
        }
        if (PushArticleConstant.USER_TYPE_ASSIGN_USER.equals(pushArticle.getUserType()) || list.size() < 2) {
            resultList = pushArticleMobileService.vivoPushByRegIds(pushParams, param, list);
        } else {
            resultList = pushArticleMobileService.vivoPushAll(pushParams, param, list);
        }
        log.info("【自动推送】vivo推送 结果 ===vivoPush===: {}",JSON.toJSONString(resultList));
        if (CollectionUtils.isEmpty(resultList)) {
            if(CollectionUtils.isNotEmpty(list)){
                saveSendInfo(dstinctsMap, pushArticle, 0,PushChannel.VIVO.name(),"0");
            }
            log.error("vivo推送失败");
            return ResultMap.error("vivo推送失败");
        }
        int count = 0;
        int index = 0;
        int countVivo = PushChannelNum.val(PushChannelNum.VIVO);
        for (Result result : resultList) {
            index ++;
            if (result != null && result.getResult() == 0) {
                //saveSendResultInfo(pushArticle, result.getTaskId(), PushChannel.VIVO.name());
                if(index==resultList.size()){
                    count += list.size() - ((resultList.size() - 1) * countVivo) ;
                } else {
                    count += countVivo;
                }
            }
        }
        if(CollectionUtils.isNotEmpty(list) && count > 0){
            saveSendInfo(dstinctsMap, pushArticle, count,PushChannel.VIVO.name(),"1");
        }
        return null;
    }

    //oppo推送
    private ResultMap oppoPush(Map<String, Object> pushParams, Map<String, String> param,
                               Map<String, List<String>> tokensMap, Map<String, List<String>> dstinctsMap) {
        log.info("########oppo用户推送########");
        List<String> list = tokensMap.get(PushChannel.OPPO.name());
        if (CollectionUtils.isEmpty(list)) {
            log.error("oppoTokens为空，不调用推送接口");
            return null;
        }
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushArticleConfig oppo = pushArticleConfigService.findPushArticleConfig(PushChannel.OPPO.name(), Constant.ANDROID, pushArticle.getAppPackage());
        log.info("【自动推送】oppo推送 配置 ===PushArticleConfig===: {}",JSON.toJSONString(oppo));
        if (oppo == null) {
            log.error("oppo推送参数未配置！{}",pushArticle.getAppPackage());
            return ResultMap.error("oppo推送参数未配置！");
        }
        pushParams.put(PushChannel.OPPO.name(), oppo);
        List<com.oppo.push.server.Result> resultList = pushArticleMobileService.oppoPushByRegIds(pushParams, param, list);
        log.info("【自动推送】oppo推送 结果 ===oppopush===: {}",JSON.toJSONString(resultList));
        if (CollectionUtils.isEmpty(resultList)) {
            log.error("OPPO推送失败");
            if(CollectionUtils.isNotEmpty(list)){
                saveSendInfo(dstinctsMap, pushArticle, 0,PushChannel.OPPO.name(),"0");
            }
            return ResultMap.error("OPPO推送失败");
        }
        int count = 0;
        int index = 0;
        int countOppo = PushChannelNum.val(PushChannelNum.OPPO);
        for (com.oppo.push.server.Result result : resultList) {
            index ++ ;
            if (result != null) {
                String taskId = result.getTaskId() == null ? result.getMessageId() : result.getTaskId();
                //saveSendResultInfo(pushArticle, taskId, PushChannel.OPPO.name());
                if(index==resultList.size()){
                    count += list.size() - ((resultList.size() - 1) * countOppo) ;
                } else {
                    count += countOppo;
                }
            }
        }
        if(CollectionUtils.isNotEmpty(list) && count > 0){
            saveSendInfo(dstinctsMap, pushArticle, count,PushChannel.OPPO.name(),"1");
        }
        return null;
    }

    private void saveSendInfo(Map<String, List<String>> dstinctsMap, PushArticle pushArticle, int count,String key, String isSucc) {
        List<AutoPushLog> autoPushLogs = listLog(pushArticle.getTitle(), pushArticle.getAppPackage(), pushArticle.getTypeValue(), dstinctsMap.get(key));
        //成功，才推送大数据
        if(Integer.parseInt(isSucc) == 1){
            sycToBdrep(autoPushLogs);
            //更新发送数
            updateSendNum(pushArticle, count);
        }
        //日志存储参数
        autoPushLogs.stream().forEach(log -> {
            log.setPush_id(pushArticle.getId());
            log.setContent(JSON.toJSONString(pushArticle));
            log.setType("1");
            log.setPush_channel(key);
            log.setIs_succ(isSucc);
        });

        saveAutoPushInfoToMongo(autoPushLogs);

    }

    //小米推送
    private ResultMap xiaomiPush(Map<String, Object> pushParams, Map<String, String> param,
                                 Map<String, List<String>> tokensMap, Map<String, List<String>> dstinctsMap) {
        log.info("########小米用户推送########");
        List<String> list = tokensMap.get(PushChannel.XiaoMi.name());
        if (CollectionUtils.isEmpty(list)) {
            log.info("xiaomiTokens为空，不调用推送接口");
            return null;
        }
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushArticleConfig xiaomi = pushArticleConfigService.findPushArticleConfig(PushChannel.XiaoMi.name(), Constant.ANDROID, pushArticle.getAppPackage());
        log.info("【自动推送】小米推送 配置 ===PushArticleConfig===: {}",JSON.toJSONString(xiaomi));
        if (xiaomi == null) {
            log.error("xiaomi推送参数未配置！{}",pushArticle.getAppPackage());
            return ResultMap.error("xiaomi推送参数未配置！");
        }
        pushParams.put(PushChannel.XiaoMi.name(), xiaomi);
        List<com.xiaomi.xmpush.server.Result> resultList = pushArticleMobileService.xiaomiPushInfo(pushParams, param, list);
        log.info("【自动推送】小米推送 结果 ===小米push===: {}",JSON.toJSONString(resultList));
        if (CollectionUtils.isEmpty(resultList)) {
            if(CollectionUtils.isNotEmpty(list)){
                saveSendInfo(dstinctsMap, pushArticle, 0,PushChannel.XiaoMi.name(),"0");
            }
            log.error("xiaomi推送参数未配置！");
            return ResultMap.error("小米推送失败");
        }
        int count = 0;
        int index = 0;
        int countXiaomi = PushChannelNum.val(PushChannelNum.XiaoMi);
        for (com.xiaomi.xmpush.server.Result result : resultList) {
            index ++;
            if (result != null) {
                //saveSendResultInfo(pushArticle, result.getMessageId(), PushChannel.XiaoMi.name());
                if(index==resultList.size()){
                    count += list.size() - ((resultList.size() - 1) * countXiaomi) ;
                } else {
                    count += countXiaomi;
                }
            }
        }
        if(CollectionUtils.isNotEmpty(list) && count > 0){
            saveSendInfo(dstinctsMap, pushArticle, count,PushChannel.XiaoMi.name(),"1");
        }
        return null;
    }

    /**
     * 保存推送信息结果
     *
     * @param pushArticle
     * @param businessId
     * @param pushChannel
     */
    private void saveSendResultInfo(PushArticle pushArticle, String businessId, String pushChannel) {
        try {
            pushArticleSendResultService.saveSendResult(pushArticle.getId(), pushChannel, businessId, pushArticle.getAppPackage());
        } catch (Exception e) {
            log.error("保存推送参数:" + JSON.toJSONString(pushArticle));
            log.error("保存推送参数:" + "businessId=" + businessId + ",pushChannel=" + pushChannel);
            log.error("保存推送息异常:" + e.getMessage(), e);
        }
    }

    private List<AutoPushLog> listLog(String title,String appPackage, String videoId, List<String> distinctIds){
        List<AutoPushLog> autoPushLogs = new ArrayList<>();
        for (String distinctId:distinctIds) {
            AutoPushLog autoPushLog = new AutoPushLog();
            autoPushLog.setApp_package(appPackage);
            autoPushLog.setDistinct_id(distinctId);
            autoPushLog.setVideo_id(videoId);
            autoPushLog.setTitle(title);
            autoPushLogs.add(autoPushLog);
        }
        return autoPushLogs;
    }

    private void saveAutoPushInfoToMongo(List<AutoPushLog> autoPushLogs) {
        mongoTemplate.insert(autoPushLogs,"auto_push_log");
    }


    @Override
    public void saveAutoPushRedis(String configId) {
        //设置5分钟过期
        redisService.set(RedisKeyConstant.AUTO_PUSH_SCHEDULE_ONE + ":" + configId, "1", 300);
    }

    @Override
    public boolean hasAutoPushRedis(String configId) {
        boolean flag = false;
        if(StringUtil.isNotEmpty(redisService.get(RedisKeyConstant.AUTO_PUSH_SCHEDULE_ONE + ":" + configId))){
            flag = true;
        }
        return flag;
    }
}