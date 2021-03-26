package com.miguan.ballvideo.common.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.miguan.ballvideo.common.util.adv.AdvGlobal;
import com.miguan.ballvideo.entity.AdPositionConfig;
import com.miguan.ballvideo.entity.BannerPriceLadderVo;
import com.miguan.ballvideo.vo.SysConfigVo;
import lombok.extern.slf4j.Slf4j;
import tool.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缓存帮助类
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-06-20 10:48:24
 */
@Slf4j
public class CacheUtil {

    /**
     * 初始化系统参数配置
     */
    public static void initSysConfig(List<SysConfigVo> sysConfigs) {
        Map<String, Object> configs = new HashMap<String, Object>();
        for (SysConfigVo sysConfig : sysConfigs) {
            if (null != sysConfig && StringUtil.isNotBlank(sysConfig.getCode())) {
                configs.put(sysConfig.getCode(), sysConfig.getValue());
            }
        }
        Global.configMap = new HashMap<String, Object>();
        Global.putConfigMapAll(configs);
    }

    public static void initBannerPriceLadders(List<BannerPriceLadderVo> bannerPriceLadders) {
        Multimap<String, BannerPriceLadderVo> priceLadderMap = ArrayListMultimap.create();
        for (BannerPriceLadderVo bannerPriceLadder : bannerPriceLadders) {
            priceLadderMap.put(bannerPriceLadder.getKeywordMobileType() + "_" + bannerPriceLadder.getAppPackage(), bannerPriceLadder);
        }
        if (!priceLadderMap.isEmpty()) {
            AdvGlobal.putPriceLadderMap(priceLadderMap);
        }
    }

    public static void initAdPositionConfigs(Map<String, AdPositionConfig> appleMap) {
        AdvGlobal.putPositionConfigMap(appleMap);
    }
}