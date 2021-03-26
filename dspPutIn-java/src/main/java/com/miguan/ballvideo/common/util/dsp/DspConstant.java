package com.miguan.ballvideo.common.util.dsp;

import org.springframework.stereotype.Component;

/**
 * @program: dspPutIn-java
 * @description: Dsp自投平台常量定义
 * @author: suhj
 * @create: 2020-09-07 14:34
 **/

@Component
public class DspConstant {
    public static final String FREQUENCY_TIME = "frequencyTime";
    //频控次数（次）
    public static final String FREQUENCY_COUNT = "frequencyCount";
    //平滑阈值(元)
    public static final String SMOOTH_THRESHOLD_VALUE = "smoothThresholdValue";
    //预算平滑 区间个数
    public static final String SMOOTH_COUNT = "smoothCount";
    //预算平滑 每个区间时长（分钟）
    public static final String SMOOTH_DURATION = "smoothDuration";
    //不限地域
    public static final String AREA_TYPE_NO = "1";
    //指定地域
    public static final String AREA_TYPE_POINT = "2";
    //不限手机品牌
    public static final String PHONE_TYPE_NO = "1";
    //指定手机品牌
    public static final String PHONE_TYPE_POINT = "2";
    //不限兴趣
    public static final Integer CAT_TYPE_NO = 0;
    //指定兴趣
    public static final Integer CAT_TYPE_POINT = 1;
    //正式投放
    public static final String FORMAL_ISSUE = "1";
    //测试投放
    public static final String TEST_ISSUE = "2";
    //计划的时间配置 全天
    public static final String TIME_SETTING_ALL_TIME = "0";
    //计划的时间配置 指定时段
    public static final String TIME_SETTING_POINT_TIME = "1";
    //计划的时间配置 多个时段
    public static final String TIME_SETTING_MANY_TIME = "2";
    //广告平台，对应表ad_plat，98_adv：98广告
    public static final String ADVERT_PLAT_98 = "98_adv";
    //广告有效点击-ad_zone_valid_click
    public static final String ADVERT_ZONE_VALID_CLICK = "ad_zone_valid_click";
    //广告有效曝光-ad_zone_exposure
    public static final String ADVERT_ZONE_EXPOSURE = "ad_zone_exposure";
    //初始参竞率
    public static final String INIT_PART_RATE = "initPartRate";
    //冷启动曝光数
    public static final String COLD_BOOT_SHOW = "coldBootShow";
}
