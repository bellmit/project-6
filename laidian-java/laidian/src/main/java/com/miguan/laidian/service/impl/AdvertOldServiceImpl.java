package com.miguan.laidian.service.impl;

import cn.jiguang.common.utils.StringUtils;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.Global;
import com.miguan.laidian.common.util.VersionUtil;
import com.miguan.laidian.mapper.AdvertMapper;
import com.miguan.laidian.service.AdvertOldService;
import com.miguan.laidian.service.MoFangService;
import com.miguan.laidian.vo.Advert;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 广告ServiceImpl
 *
 * @author xy.chen
 * @date 2019-06-24
 **/

@Service("AdvertOldService")
public class AdvertOldServiceImpl implements AdvertOldService {

    public static final String ADV_CHANNEL_SWITCH = "adv_channel_switch";//默认广告开关
    public static final String ADV_DEFAULT_CHANNEL = "adv_default_channel";//默认广告渠道

    @Resource
    private AdvertMapper advertMapper;

    @Resource
    private MoFangService moFangService;

    /**
     * 查询广告配置
     *
     * @param postitionType 广告位置类型，如果为空，则查询全部
     * @param adPermission  是否有IMEI权限：0否 1是
     * @return
     */
    @Override
    public List<Advert> queryAdertList(CommonParamsVo commomParams, String postitionType, String adPermission) {
        Map<String, Object> param = new HashMap<>();
        param.put("postitionType", postitionType);
        param.put("mobileType", commomParams.getMobileType());
        param.put("channelId", commomParams.getChannelId());
        param.put("appType", commomParams.getAppType());
        param.put("adPermission", adPermission);
        param.put("appVersion", commomParams.getAppVersion());
        List<Advert> resultList = new ArrayList<>();
        //查询魔方后台是否禁用该渠道的广告:1禁用，0非禁用
        if (checkForbiddenAdertByChannel(param) == 1) {
            return resultList;
        };
        //查询广告位
        List<Advert> list = getFinalAdverList(param);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(list)) {
            //同一个广告位如果有多个配置，则每次调用随机返回一个广告位置
            Map<String, List<Advert>> mapList = list.stream().collect(Collectors.groupingBy(Advert::getPostitionType));
            Random rand = new Random();
            for (Map.Entry<String, List<Advert>> map : mapList.entrySet()) {
                List<Advert> oneTypeAdvert = map.getValue();
                int index = rand.nextInt(oneTypeAdvert.size());
                Advert advert = oneTypeAdvert.get(index);
                resultList.add(advert);
            }
        }
        return resultList;
    }

    public List<Advert> getFinalAdverList(Map<String, Object> param) {
        List<Advert> resultList = new ArrayList<Advert>();
        List<Advert> list = advertMapper.queryAdertList(param);
        if (CollectionUtils.isNotEmpty(list)) {
            Map<Integer, List<Advert>> advertVoMap = list.stream().collect(Collectors.groupingBy(Advert::getState));
            return advertVoMap.get(0);
        } else {
            String mobileType = param.get("mobileType") + "";
            String appType = param.get("appType") + "";
            //V2.3.0 查询广告结果为空，安卓根据开关查询默认渠道广告 add byshixh
            if (Constant.Android.equals(mobileType)) {
                String channelId = getDefaultChannel(appType);
                if (StringUtils.isNotEmpty(channelId)) {
                    param.put("channelId", channelId);
                    param.put("state", 0);
                    return advertMapper.queryAdertList(param);
                }
            }
        }
        return resultList;
    }


    /**
     * 查询广告结果为空，安卓根据开关是否走默认渠道再查询广告
     *
     * @param appType 马甲包类型
     * @return
     */
    public String getDefaultChannel(String appType) {
        String switch_code = ADV_CHANNEL_SWITCH;//开关是否开启
        String channel_code = ADV_DEFAULT_CHANNEL;//默认渠道
        String channelSwitch = Global.getValue(switch_code, appType);//1-开启，0-关闭
        if ((Constant.open + "").equals(channelSwitch)) {
            return Global.getValue(channel_code, appType);
        }
        return null;
    }

    /**
     * 查询魔方后台是否禁用该渠道的广告:1禁用，0非禁用
     * @param param
     * @return
     */
    public int checkForbiddenAdertByChannel(Map<String, Object> param) {
        String appVersion = param.get("appVersion") + "";
        if (!"null".equals(appVersion) && VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_190)) {
            String mobileType = param.get("mobileType") + "";
            String postitionType = param.get("postitionType") + "";
            String appType = param.get("appType") + "";
            String channelId = param.get("channelId") + "";
            int tagType = Constant.IOS.equals(mobileType) ? 2 : 1;
            int count = moFangService.countVersion(postitionType, appVersion, appType, tagType);
            //根据版本判断是否屏蔽全部广告
            if (count > 0) {
                return 1;
            }
            //非全部的屏蔽根据渠道查询是否屏蔽广告
            int countChannel = moFangService.countChannel(postitionType, channelId, appType, appVersion, tagType);
            return countChannel > 0 ? 1 : 0;
        } else {
            return 0;
        }
    }
}