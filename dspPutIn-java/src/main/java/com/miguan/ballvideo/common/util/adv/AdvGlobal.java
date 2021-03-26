package com.miguan.ballvideo.common.util.adv;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.miguan.ballvideo.entity.AdPositionConfig;
import com.miguan.ballvideo.entity.BannerPriceLadderVo;

import java.util.Map;

/**
 * 广告相关配置内存存储工具
 * @Author shixh
 * @Date 2020/4/2
 **/
public class AdvGlobal {

    public static Multimap<String,BannerPriceLadderVo> priceLadderMap = ArrayListMultimap.create();

    public static Map<String, AdPositionConfig> positionConfigMap = Maps.newHashMap();

    public static void putPriceLadderMap(Multimap<String,BannerPriceLadderVo> datas) {
        priceLadderMap.clear();
        priceLadderMap.putAll(datas);
    }

    public static void putPositionConfigMap(Map<String, AdPositionConfig>  datas) {
        positionConfigMap.clear();
        positionConfigMap.putAll(datas);
    }
}
