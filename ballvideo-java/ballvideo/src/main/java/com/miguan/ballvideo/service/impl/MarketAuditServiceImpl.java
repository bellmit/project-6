package com.miguan.ballvideo.service.impl;

import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.util.VersionUtil;
import com.miguan.ballvideo.entity.MarketAudit;
import com.miguan.ballvideo.repositories.MarketAuditDao;
import com.miguan.ballvideo.service.MarketAuditService;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MarketAuditServiceImpl implements MarketAuditService {


    @Autowired
    private MarketAuditDao marketAuditDao;

    @Override
    public boolean isShield(Map<String, Object> param) {
        boolean flag = false;
        String channelId = MapUtils.getString(param, "marketChannelId");
        String appVersion = MapUtils.getString(param, "appVersion");
        boolean high = VersionUtil.compareIsHigh(VersionUtil.getVersion(appVersion), Constant.APPVERSION_210);
        if (!high){
            return flag;
        }
        MarketAudit marketAudit = this.getCatIdsByChannelIdAndAppVersion(channelId, appVersion);
        if (marketAudit != null) {
            flag =true;
        }
        return flag;
    }

    @Override
    public boolean isShield(String marketChannelId,String appVersion) {
        boolean flag = false;
        boolean high = VersionUtil.compareIsHigh(VersionUtil.getVersion(appVersion), Constant.APPVERSION_210);
        if (!high){
            return flag;
        }
        MarketAudit marketAudit = this.getCatIdsByChannelIdAndAppVersion(marketChannelId, appVersion);
        if (marketAudit != null) {
            flag =true;
        }
        return flag;
    }

    @Override
    public MarketAudit getCatIdsByChannelIdAndAppVersion(String channelId,String appVersion) {
        return marketAuditDao.getCatIdsByChannelIdAndAppVersion(channelId, appVersion);
    }

    @Override
    public String getCatIdsByChannelIdAndAppVersionFromTeenager(String channelId,String appVersion) {
        return marketAuditDao.getCatIdsByChannelIdAndAppVersionFromTeenager(channelId, appVersion);
    }

}
