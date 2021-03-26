package com.miguan.laidian.service;

import com.miguan.message.push.utils.huawei.messaging.SendResponce;
import com.vivo.push.sdk.notofication.Result;

import java.util.List;
import java.util.Map;

/**
 * Created by laiyd on 2020/4/14.
 */
public interface PushArticleMobileService {

    //huawei指定用户和广播推送
    List<SendResponce> huaweiPushInfo(Map<String, Object> pushParams, Map<String, String> params, List<String> regIds);

    //vivo指定用户和广播推送
    List<Result> vivoPushByRegIds(Map<String, Object> pushParams, Map<String, String> params, List<String> regIds);

    //vivo广播推送
    List<Result> vivoPushAll(Map<String, Object> pushParams, Map<String, String> params, List<String> regIds);

    //oppo指定用户和广播推送
    List<com.oppo.push.server.Result> oppoPushByRegIds(Map<String, Object> pushParams, Map<String, String> params, List<String> regIds);

    //xiaomi指定用户和广播推送
    List<com.xiaomi.xmpush.server.Result> xiaomiPushInfo(Map<String, Object> pushParams, Map<String, String> params, List<String> regIds);
}
