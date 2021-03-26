package com.miguan.laidian.service.impl;

import com.miguan.laidian.common.util.Global;
import com.miguan.laidian.common.constants.VersionEnum;
import com.miguan.laidian.mapper.ClUserVersionMapper;
import com.miguan.laidian.service.VersionInfoService;
import com.miguan.laidian.vo.ClUserVersion;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class VersionInfoServiceImpl implements VersionInfoService {

    @Resource
    ClUserVersionMapper clUserVersionMapper;

    @Override
    public Map<String, Object> findSysVersionInfoAndUserId(String appType, ClUserVersion clUserVersion) {
        Map<String, Object> map = new HashMap<>();
        //如果后台开启强制更新，并且版本号相等
        String forceUpdate = Global.getValue("force_update", appType);
        if (VersionEnum.VERSION_ENUM_TYPE_OPEN.getCode().equals(forceUpdate)
                && !Global.getValue("android_version", appType).equals(clUserVersion.getVersionNumber())) {
            int android_forces = 0;
            if (StringUtils.isNotBlank(Global.getValue("android_forces", appType))) {
                android_forces = Integer.parseInt(Global.getValue("android_forces", appType));
            }
            //安卓默认更新数量为0时，默认更新所有的APP没有数量限制，查询当前APP更新数量，对比限制数量，如果当前更新数量小于设置更新数量为true
            if (android_forces == 0 || clUserVersionMapper.findUserVersionByVersionId(Global.getValue("android_version", appType), appType) < Integer.parseInt(Global.getValue("android_forces", appType))) {
                getUserVersion(appType, map, forceUpdate);
            } else {
                //灰度测试数量已满，返回非强制更新
                getUserVersion(appType, map, "10");
            }
        } else if (VersionEnum.VERSION_ENUM_TYPE_OFF.getCode().equals(forceUpdate)
                && !Global.getValue("android_version", appType).equals(clUserVersion.getVersionNumber())) {
            getUserVersion(appType, map, forceUpdate);
        } else {
            map.put("code", "500");
            map.put("message", "无须更新");
            map.put("state", "false");
        }
        return map;
    }

    /**
     * 灰度用户版本更新信息
     * @param appType
     * @param map
     * @param forceUpdate
     */
    private void getUserVersion(String appType, Map<String, Object> map, String forceUpdate) {
        map.put("code", "200");
        map.put("forceUpdate", forceUpdate);  //是否强制更新：10--否，20--是
        map.put("updateContent", Global.getValue("update_content", appType));  //手机app最新版本更新内容
        map.put("androidVersion", Global.getValue("android_version", appType));  //最新android版本
        map.put("androidAddress", Global.getValue("android_address", appType));   //最新android版本下载地址
        map.put("state", "true");
    }

    @Override
    public int addUserVersionInfo(String appType, ClUserVersion clUserVersion) {
        if (VersionEnum.VERSION_ENUM_TYPE_OPEN.getCode().equals(Global.getValue("force_update", appType))) {
            clUserVersion.setCreateTime(new Date());
            clUserVersion.setVersionNumber(Global.getValue("android_version", appType));
            return clUserVersionMapper.insertSelective(clUserVersion);
        }
        return 0;
    }
}
