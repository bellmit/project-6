package com.miguan.ballvideo.service;

import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.dto.PopConfDto;
import com.miguan.ballvideo.dto.WifiPopConfDto;

import java.util.List;

/**
 * @Author shixh
 * @Date 2020/3/23
 **/
public interface SysService {

    void delRedis(String phPkey);

    void updateAdConfigCache();

    void updateAdLadderCache();

    /**
     * 首页弹窗接口，返回值为空则不需要弹
     * @param popTime 之前出现弹窗的时间戳
     * @param popPosition 弹窗位置：1--首页弹窗，2--首页悬浮窗
     * @param channelId 渠道号
     * @param appVersion 版本号
     * @param appVersion 手机类型应用端:1-ios，2-安卓
     * @return
     */
    PopConfDto popConf(Long popTime, int popPosition, String channelId, String appVersion, String mobileType);

    /**
     * wifi通知通知栏弹窗接口，返回值为空则不需要弹
     * @param appPackage app包名，逗号隔开
     * @param channelId 渠道号，逗号隔开
     * @param version 作用版本
     * @param popPosition 弹窗位置：1--首页弹窗，2--首页悬浮窗，3-wifi
     * @return
     */
    List<WifiPopConfDto> wifiPopConf(String appPackage, String channelId, String version,String deviceId, int popPosition);

    ResultMap projectCondition();
}
