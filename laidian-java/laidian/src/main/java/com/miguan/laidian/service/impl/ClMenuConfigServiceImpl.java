package com.miguan.laidian.service.impl;

import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.VersionUtil;
import com.miguan.laidian.entity.ClMenuConfig;
import com.miguan.laidian.entity.ShieldMenuConfig;
import com.miguan.laidian.mapper.ClMenuConfigMapper;
import com.miguan.laidian.service.AdvertOldService;
import com.miguan.laidian.service.ClMenuConfigService;
import com.miguan.laidian.service.MoFangService;
import com.miguan.laidian.vo.Advert;
import com.miguan.laidian.vo.ClMenuConfigVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单栏配置表ServiceImpl
 *
 * @author xy.chen
 * @date 2019-08-23
 **/

@Service("clMenuConfigService")
public class ClMenuConfigServiceImpl implements ClMenuConfigService {

    @Resource
    private ClMenuConfigMapper clMenuConfigMapper;

    @Resource
    private AdvertOldService advertOldService;

    @Resource
    private MoFangService moFangService;

    public List<ClMenuConfig> findClMenuConfigList(Map<String, Object> params) {
        return clMenuConfigMapper.findClMenuConfigList(params);
    }

    @Override
    public List<ClMenuConfigVo> findClMenuConfigInfo(Map<String, Object> params) {
        String channelId = params.get("channelId") + "";
        String appType = params.get("appType") + "";
        String adPermission = params.get("adPermission") + "";
        String appVersion = params.get("appVersion") == null ? "1.9.0" : params.get("appVersion").toString();
        String mobileType = params.get("mobileType") + "";
        if (StringUtils.isNotBlank(mobileType)) {
            mobileType = "2";
        }
        List<ClMenuConfig> menuConfigList = findClMenuConfigList(params);
        //移除魔方后台屏蔽的菜单栏
        this.removeShieldMenuConfig(menuConfigList, channelId, appType, appVersion, mobileType);
        List<ClMenuConfigVo> menuConfigVos = new ArrayList<>();
        for (ClMenuConfig clMenuConfigVo : menuConfigList) {
            ClMenuConfigVo menuConfigVo = new ClMenuConfigVo();
            BeanUtils.copyProperties(clMenuConfigVo, menuConfigVo);
            if ("1".equals(clMenuConfigVo.getHasAdv()) && !VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_250)) {
                String channelIdParam = "";
                if (StringUtils.isNotBlank(channelId)) {
                    channelIdParam = channelId;
                }
                CommonParamsVo commomParams = new CommonParamsVo();
                commomParams.setAppVersion(appVersion);
                commomParams.setChannelId(channelIdParam);
                commomParams.setMobileType(mobileType);
                commomParams.setAppType(appType);
                List<Advert> list = advertOldService.queryAdertList(commomParams, clMenuConfigVo.getBannerId(), adPermission);
                if (list != null && list.size() > 0) {
                    Advert advert = list.get(0);
                    menuConfigVo.setTitle(advert.getTitle()); //标题
                    menuConfigVo.setLinkAddr(advert.getUrl()); //链接
                    menuConfigVo.setImgUrl(advert.getImgPath()); //选中该图标
                    menuConfigVo.setImgUrl2(advert.getImgPath2());  //未选中图标
                }
            }
            menuConfigVos.add(menuConfigVo);
        }
        return menuConfigVos;
    }

    /**
     * 移除魔方后台屏蔽的菜单栏
     */
    private void removeShieldMenuConfig(List<ClMenuConfig> menuConfigList, String channelId, String appType, String appVersion, String mobileType) {
        List<String> keysList = menuConfigList.stream().map(ClMenuConfig::getKey).collect(Collectors.toList());
        String keys = "'" + StringUtils.join(keysList.toArray(), "','") + "'";
        int tagType = "2".equals(mobileType) ? 1 : 2;
        Map<String, Integer> resultMap = new HashMap<>();
        for (String key : keysList) {
            resultMap.put(key, 0);
        }
        List<ShieldMenuConfig> versionMaps = moFangService.countVersionList(keys, appVersion, appType, tagType);
        for (ShieldMenuConfig versionMap : versionMaps) {
            String key = versionMap.getCks();
            if (key == null) {
                continue;
            }
            int value = versionMap.getCt();
            resultMap.put(key, value);
        }
        List<ShieldMenuConfig> channelMaps = moFangService.countChannelList(keys, channelId, appType, appVersion, tagType);
        for (ShieldMenuConfig channelMap : channelMaps) {
            String key = channelMap.getCks();
            if (key == null) {
                continue;
            }
            int value = channelMap.getCt();
            resultMap.put(key, value);
        }
        for (int i = 0; i < menuConfigList.size(); i++) {
            ClMenuConfig clMenuConfig = menuConfigList.get(i);
            String key = clMenuConfig.getKey();
            if (resultMap.get(key) == 1) {
                menuConfigList.remove(i);
            }
        }
    }

}