package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.constants.PushArticleConstant;
import com.miguan.ballvideo.common.enums.PushChannel;
import com.miguan.ballvideo.common.util.StringUtil;
import com.miguan.ballvideo.entity.PushArticle;
import com.miguan.ballvideo.entity.PushArticleConfig;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.PushArticleMobileService;
import com.miguan.ballvideo.service.RedisService;
import com.miguan.message.push.model.PushMessageBody;
import com.miguan.message.push.utils.UPushSendMessageUtil;
import com.miguan.message.push.utils.huawei.HuaweiPush;
import com.miguan.message.push.utils.huawei.MessageBody;
import com.miguan.message.push.utils.huawei.messaging.SendResponce;
import com.miguan.message.push.utils.oppo.OppoPush;
import com.miguan.message.push.utils.vivo.VivoPush;
import com.miguan.message.push.utils.xiaomi.XiaomiPush;
import com.vivo.push.sdk.notofication.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author laiyd
 * @Date 2020/4/14
 **/
@Slf4j
@Service
public class PushArticleMobileServiceImpl implements PushArticleMobileService {

    @Resource
    private RedisService redisService;

    @Override
    public List<Map> youMengIosPushInfo(Map<String, Object> pushParams, Map<String, String> params, String deviceTokens){
        List<Map> resultMap = new ArrayList<>();
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushArticleConfig config = (PushArticleConfig) pushParams.get(PushChannel.YouMeng.name());
        Long expireTime = Long.valueOf(pushParams.get("expireTime").toString());
        if (StringUtils.isNotEmpty(deviceTokens)) {
            //指定/定向用户推送
            deviceTokens = pushArticle.getDeviceTokens();
            resultMap = UPushSendMessageUtil.sendToIOSListcastInTurn(pushArticle.getTitle(), pushArticle.getNoteContent(),
                    params, config.getAppKey(), config.getAppSecret(), config.getPushModel(), expireTime, deviceTokens);
        } else{
            //全部用户推送
            Map result = UPushSendMessageUtil.sendToIOS(pushArticle.getTitle(), pushArticle.getNoteContent(), params,
                    config.getAppKey(), config.getAppSecret(), config.getPushModel(), expireTime);
            if (result != null) {
                resultMap.add(result);
            }
        }

        return resultMap;
    }

    @Override
    public List<Map> youMengAndroidPushInfo(Map<String, Object> pushParams, Map<String, String> params, String deviceTokens) {
        List<Map> resultMap = new ArrayList<>();
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushArticleConfig config = (PushArticleConfig) pushParams.get(PushChannel.YouMeng.name());
        Long expireTime = Long.valueOf(pushParams.get("expireTime").toString());
        String packageName = pushParams.get("packageName").toString();
        if (StringUtils.isNotEmpty(deviceTokens)) {
            //指定/定向用户推送
            resultMap = UPushSendMessageUtil.sendToAndriodListcastInTurn(pushArticle.getTitle(), pushArticle.getNoteContent(),
                    params, config.getAppKey(), config.getAppSecret(), config.getPushModel(), expireTime, packageName, deviceTokens);
        } else {
            //全部用户推送
            Map result = UPushSendMessageUtil.sendToAndriod(pushArticle.getTitle(), pushArticle.getNoteContent(), params,
                    config.getAppKey(), config.getAppSecret(), config.getPushModel(), expireTime, packageName);
            if (result != null) {
                resultMap.add(result);
            }
        }
        return resultMap;
    }

    @Override
    public List<Map> youMengIosPushInfoByToken(Map<String, Object> pushParams, Map<String, String> params, List<String> tokens){
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushArticleConfig config = (PushArticleConfig) pushParams.get(PushChannel.YouMeng.name());
        Long expireTime = Long.valueOf(pushParams.get("expireTime").toString());
        //指定token，列推
        List<Map> resultMap = UPushSendMessageUtil.sendToIOSListcastInTurn(pushArticle.getTitle(), pushArticle.getNoteContent(),
                params, config.getAppKey(), config.getAppSecret(), config.getPushModel(), expireTime, StringUtil.listToString(tokens));
        return resultMap;
    }



    @Override
    public List<Map> youMengAndroidPushInfoByToken(Map<String, Object> pushParams, Map<String, String> params, List<String> tokens) {
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushArticleConfig config = (PushArticleConfig) pushParams.get(PushChannel.YouMeng.name());
        Long expireTime = Long.valueOf(pushParams.get("expireTime").toString());
        String packageName = pushParams.get("packageName").toString();
        List<Map> resultMap = UPushSendMessageUtil.sendToAndriodListcastInTurn(pushArticle.getTitle(), pushArticle.getNoteContent(),
                params, config.getAppKey(), config.getAppSecret(), config.getPushModel(), expireTime, packageName, StringUtil.listToString(tokens));
        return resultMap;
    }

    @Override
    public List<SendResponce> huaweiPushInfo(Map<String, Object> pushParams, Map<String, String> params, List<String> regIds) {
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushArticleConfig huaWei = (PushArticleConfig) pushParams.get(PushChannel.HuaWei.name());
        List<SendResponce> push = HuaweiPush.push(new MessageBody(pushArticle.getTitle(), pushArticle.getNoteContent(), params), pushArticle.getExpireTime(), regIds, huaWei.getAppId(), huaWei.getAppSecret());
        return push;
    }

    @Override
    public List<Result> vivoPushByRegIds(Map<String, Object> pushParams, Map<String, String> params, List<String> regIds) {
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushMessageBody pushMessageBody = getVivoPushMessageBody(pushParams, pushArticle, params, regIds);
        List<Result> resultList = new ArrayList<>();
        if (regIds.size() < 2) {
            Result result = VivoPush.pushSingle(pushMessageBody);
            if (result != null) {
                resultList.add(result);
            }
        } else {

            Map<String,String> taskMap = VivoPush.saveListPayloadInTurn(pushMessageBody);
            if(taskMap == null){
                return null;
            }
            if(taskMap.get("taskId") == null){
                //鉴权出错
                String err_code = taskMap.get("err_code");
                if("10000".equals(err_code) || "10070".equals(err_code) || "10252".equals(err_code) || "10254".equals(err_code)){
                    PushArticleConfig pushArticleConfig = (PushArticleConfig) pushParams.get(PushChannel.VIVO.name());
                    redisService.set(RedisKeyConstant.VIVO_LIMIT_TOKEN + pushArticleConfig.getAppId(), err_code, 3600);
                }
                return null;
            } else {
                pushMessageBody.setTaskId(taskMap.get("taskId"));
                resultList = VivoPush.pushInTurn(pushMessageBody);
            }
        }
        return resultList;
    }

    @Override
    public List<Result> vivoPushAll(Map<String, Object> pushParams, Map<String, String> params, List<String> regIds) {
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushMessageBody pushMessageBody = getVivoPushMessageBody(pushParams, pushArticle, params, regIds);
        if (pushMessageBody == null) return null;
        String taskID = "";
        Map<String,String> taskMap = VivoPush.saveListPayloadInTurn(pushMessageBody);
        if(taskMap == null){
            return null;
        }
        if(taskMap.get("taskId") == null){
            //鉴权出错
            String err_code = taskMap.get("err_code");
            if("10000".equals(err_code) || "10070".equals(err_code) || "10252".equals(err_code) || "10254".equals(err_code)){
                PushArticleConfig pushArticleConfig = (PushArticleConfig) pushParams.get(PushChannel.VIVO.name());
                //根据不同的app进行限制
                redisService.set(RedisKeyConstant.VIVO_LIMIT_TOKEN + pushArticleConfig.getAppId(), err_code, 3600);
            }
            return null;
        } else {
            taskID = taskMap.get("taskId");
        }
        if (StringUtils.isEmpty(taskID)) {
            return null;
        }
        pushMessageBody.setTaskId(taskID);
        return VivoPush.pushInTurn(pushMessageBody);
    }

    /**
     * vivo组装PushMessageBody信息
     */
    private PushMessageBody getVivoPushMessageBody(Map<String, Object> pushParams, PushArticle pushArticle, Map<String, String> param, List<String> regIds) {
        try {
            int expireTime = Integer.valueOf(pushParams.get("expireTime").toString()) / 1000;
            PushArticleConfig pushArticleConfig = (PushArticleConfig) pushParams.get(PushChannel.VIVO.name());
            PushMessageBody pushMessageBody = new PushMessageBody();
            pushMessageBody.setMasterSecret(pushArticleConfig.getAppSecret());
            String vivoToken = redisService.get(RedisKeyConstant.VIVO_TOKEN + pushArticleConfig.getAppId(), String.class);
            if (StringUtils.isEmpty(vivoToken)) {
                pushMessageBody.setAppId(pushArticleConfig.getAppId());
                pushMessageBody.setAppKey(pushArticleConfig.getAppKey());
                vivoToken = VivoPush.getToken(pushMessageBody);
                //vivio鉴权有效期默认为1天，过期后无法使用,1小时重新请求存放redis
                redisService.set(RedisKeyConstant.VIVO_TOKEN + pushArticleConfig.getAppId(), vivoToken, 3600);
            }
            pushMessageBody.setToken(vivoToken);
            pushMessageBody.setTitle(pushArticle.getTitle());
            pushMessageBody.setMessagePayload(pushArticle.getNoteContent());
            pushMessageBody.setExpireTime(expireTime);
            pushMessageBody.setRegIds(regIds);
            pushMessageBody.setParams(param);
            return pushMessageBody;
        } catch (Exception e) {
            log.error("vivo推送鉴权失败：" + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<com.oppo.push.server.Result> oppoPushByRegIds(Map<String, Object> pushParams, Map<String, String> params, List<String> regIds) {
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushMessageBody pushMessageBody = getOppoPushMessageBody(pushParams, pushArticle, params, regIds);
        List<com.oppo.push.server.Result> resultList = new ArrayList<>();
        if (regIds.size() < 2) {
            com.oppo.push.server.Result result = OppoPush.pushSingle(pushMessageBody);
            if (result != null) {
                resultList.add(result);
            }
        } else {
            resultList = OppoPush.pushInTurn(pushMessageBody);
        }
        return resultList;
    }

    /**
     * oppo组装PushMessageBody信息
     */
    private PushMessageBody getOppoPushMessageBody(Map<String, Object> pushParams, PushArticle pushArticle, Map<String, String> param, List<String> regIds) {
        int expireTime = (int) (Long.valueOf(pushParams.get("expireTime").toString()) / 1000);
        PushArticleConfig pushArticleConfig = (PushArticleConfig) pushParams.get(PushChannel.OPPO.name());
        PushMessageBody pushMessageBody = new PushMessageBody();
        pushMessageBody.setMasterSecret(pushArticleConfig.getAppSecret());
        pushMessageBody.setAppId(pushArticleConfig.getAppId());
        pushMessageBody.setAppKey(pushArticleConfig.getAppKey());
        pushMessageBody.setTitle(pushArticle.getTitle());
        pushMessageBody.setMessagePayload(pushArticle.getNoteContent());
        pushMessageBody.setExpireTime(expireTime);
        pushMessageBody.setRegIds(regIds);
        pushMessageBody.setParams(param);
        pushMessageBody.setIntentUrl("com.mg.xyvideo.module.home.ActivityOppoPushEmpty");
        return pushMessageBody;
    }

    @Override
    public List<com.xiaomi.xmpush.server.Result> xiaomiPushInfo(Map<String, Object> pushParams, Map<String, String> params, List<String> regIds) {
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushMessageBody pushMessageBody = getXiaomiPushMessageBody(pushParams, pushArticle, params, regIds);
        if (pushMessageBody == null) return null;
        return XiaomiPush.pushInTurn(pushMessageBody);
    }

    /**
     * xiaomi组装PushMessageBody信息
     */
    private PushMessageBody getXiaomiPushMessageBody(Map<String, Object> pushParams, PushArticle pushArticle, Map<String, String> params, List<String> regIds) {
        int expireTime = Integer.parseInt(pushParams.get("expireTime").toString());
        String[] appNames = {pushArticle.getAppPackage()};
        PushArticleConfig pushArticleConfig = (PushArticleConfig) pushParams.get(PushChannel.XiaoMi.name());
        PushMessageBody pushMessageBody = new PushMessageBody();
        pushMessageBody.setAppNames(appNames);
        pushMessageBody.setMobileType(Constant.ANDROID);
        pushMessageBody.setAppId(pushArticleConfig.getAppId());
        pushMessageBody.setMasterSecret(pushArticleConfig.getAppSecret());
        pushMessageBody.setTitle(pushArticle.getTitle());
        pushMessageBody.setDescription(pushArticle.getNoteContent());
        pushMessageBody.setMessagePayload(JSON.toJSON(params).toString());
        pushMessageBody.setExpireTime(expireTime);
        pushMessageBody.setRegIds(regIds);
        return pushMessageBody;
    }
}
