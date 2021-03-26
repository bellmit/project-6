package com.miguan.flow.common.constant;



public interface RedisConstant {

    int DEFALUT_SECONDS = 180; //缓存默认3分钟过期

    String EMPTY_VALUE = "empty";//防止缓存击穿，设置value为empty

    String ADV_CODES = "adlist:";  //代码位列表

    String ABTEST_DATA = "abTestData:"; //AB测试数据

    String AD_POSITION = "adPosition:";  //广告位列表

    String PROFIT_AD = "adProfit"; //最近一天代码位收益

    String AD_MULTI_DATA = "adMultiData:"; //代码位多维度数据（代码位自动排序）

    String AD_TOTAL_DATA = "adTotalData:"; //代码位的数据（代码位自动排序）

    String AD_DATA = "adData:"; //代码位数据（代码位自动排序）

    String SORT_MULTI_DATE = "sortMultiDate:"; //代码位多维度数据日期

    String SHIELD_CHANNEL = "shieldChannel";  //屏蔽渠道

    //开关配置redis的可以前缀
    public static final String CONFIG_CODE_PRE = "SYS_CONFIG:";

    /**
     * 阶梯广告延迟请求毫秒key
     */
    public final String AD_LADDER_DELAY_MILLIS = "ad_ladder_delay_millis";

    /**
     * 通跑广告延迟请求毫秒数key
     */
    public final String AD_COMMON_DELAY_MILLIS = "ad_common_delay_millis";

    /**
     * 激励视频入库视频数量
     */
    String INCENTIVE_VIDEO_DAY_INCR = "incentive_video_day_incr";

    /**
     * 激励视频最大数量
     */
    String INCENTIVE_VIDEO_DELIVERY_NUM = "incentive_video_max_num";
}
