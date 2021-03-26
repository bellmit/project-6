package com.miguan.ballvideo.service;

import com.miguan.ballvideo.entity.MarketAudit;

import java.util.Map;

public interface MarketAuditService {

    //版本2.2.0以上，市场审核开关开启，屏蔽所有广告和小游戏菜单栏
    boolean isShield(Map<String, Object> param);

    //逻辑一样，参数不同
    boolean isShield(String marketChannelId,String appVersion);

    //根据渠道号和版本号查询市场审核信息
    MarketAudit getCatIdsByChannelIdAndAppVersion(String channelId, String appVersion);

    //如果开启青少年模式，需要获取屏蔽分类ID
    String getCatIdsByChannelIdAndAppVersionFromTeenager(String channelId,String appVersion);
}
