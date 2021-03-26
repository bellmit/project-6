package com.miguan.laidian.service.impl;

import com.alibaba.fastjson.JSON;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.enums.PushChannel;
import com.miguan.laidian.entity.PushArticle;
import com.miguan.laidian.entity.PushArticleConfig;
import com.miguan.laidian.redis.service.RedisService;
import com.miguan.laidian.redis.util.RedisKeyConstant;
import com.miguan.laidian.service.PushArticleMobileService;
import com.miguan.message.push.model.PushMessageBody;
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
            pushMessageBody.setTaskId(VivoPush.saveListPayload(pushMessageBody));
            resultList = VivoPush.pushInTurn(pushMessageBody);
        }
        return resultList;
    }

    @Override
    public List<Result> vivoPushAll(Map<String, Object> pushParams, Map<String, String> params, List<String> regIds) {
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushMessageBody pushMessageBody = getVivoPushMessageBody(pushParams, pushArticle, params, regIds);
        List<Result> resultList = new ArrayList<>();
        if (regIds.size() < 2) {
            Result result = VivoPush.pushSingle(pushMessageBody);
            if (result != null) {
                resultList.add(result);
            }
        } else {
            pushMessageBody.setTaskId(VivoPush.saveListPayload(pushMessageBody));
            resultList = VivoPush.pushInTurn(pushMessageBody);
        }
        return resultList;
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
            String vivoToken = redisService.get(RedisKeyConstant.VIVO_TOKEN, String.class);
            if (StringUtils.isEmpty(vivoToken)) {
                pushMessageBody.setAppId(pushArticleConfig.getAppId());
                pushMessageBody.setAppKey(pushArticleConfig.getAppKey());
                vivoToken = VivoPush.getToken(pushMessageBody);
                //vivio鉴权有效期默认为1天，过期后无法使用,1小时重新请求存放redis
                redisService.set(RedisKeyConstant.VIVO_TOKEN, vivoToken, 3600);
            }
            pushMessageBody.setToken(vivoToken);
            pushMessageBody.setTitle(changeStr(pushArticle.getTitle(), 20));
            pushMessageBody.setMessagePayload(changeStr(pushArticle.getNoteContent(), 50));
            pushMessageBody.setExpireTime(expireTime);
            pushMessageBody.setRegIds(regIds);
            pushMessageBody.setParams(param);
            return pushMessageBody;
        } catch (Exception e) {
            log.error("vivo推送鉴权失败：" + e.getMessage(), e);
        }
        return null;
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
        pushMessageBody.setIntentUrl("com.mg.phonecall.push.ActivityOppoPushEmpty");
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
        String[] appNames = {pushArticle.getAppType()};
        PushArticleConfig pushArticleConfig = (PushArticleConfig) pushParams.get(PushChannel.XiaoMi.name());
        if (pushArticleConfig == null) {
            log.error("xiaomi推送失败，没有配置推送参数");
            return null;
        }
        PushMessageBody pushMessageBody = new PushMessageBody();
        pushMessageBody.setAppNames(appNames);
        pushMessageBody.setMobileType(Constant.Android);
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
