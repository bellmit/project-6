package com.miguan.xuanyuan.common.constant;

/**
 * 流量策略
 *
 */
public class StrategyGroupConstant {


    public static final int AB_STATUS_STOP = -1; //停止运行
    public static final int AB_STATUS_UNRUN = 0; //未运行
    public static final int AB_STATUS_RUN = 1; //立即运行
    public static final int AB_STATUS_DELAY_RUN = 2; //定时运行

    public static final String STRATEGY_TYPE_TOTAL = "total"; //实验总流量
    public static final String STRATEGY_TYPE_CONTRAST = "contrast"; //对照组
    public static final String STRATEGY_TYPE_TEST = "test"; //测试组

    public static final Integer STRATEGY_TYPE_CONTRAST_VAL = 1; //对照组
    public static final Integer STRATEGY_TYPE_TEST_VAL = 2; //测试组

    public static final Integer SORT_TYPE_MANUAL = 1; //手动排序
    public static final Integer SORT_TYPE_AUTO = 2; //自动排序


    public static final String DEFAULT_STRATEGY_GROUP_NAME = "默认分组";
    public static final String AB_STRATEGY_GROUP_NAME = "AB默认分组";

    //实验标识码
    public static final String AB_EXP_CODE_PREFIX = "ad_exp";


    //分组类型：1：默认分组
    public static final Integer DEFAULT_GROUP_TYPE = 1;
    //分组类型：2:手动分组
    public static final Integer HANDLER_GROUP_TYPE = 2;


    //渠道类型：1-全部 2-仅限 3-排除
    public static final Integer CHANNEL_TYPE_ALL = 1;
    public static final Integer CHANNEL_TYPE_ONLY = 2;
    public static final Integer CHANNEL_TYPE_EX = 3;

    //版本类型：1-全部 2-仅限 3-排除
    public static final Integer VERSION_TYPE_ALL = 1;
    public static final Integer VERSION_TYPE_ONLY = 2;
    public static final Integer VERSION_TYPE_EX = 3;

    //算法：1-手动配比 2-手动排序
    public static final Integer HANDLER_MATCHING = 1;
    public static final Integer HANDLER_ORDER = 2;


    //测试组状态：0-关闭，1开启
    public static final Integer TEST_GROUP_STATE_CLOSE = 0;
    public static final Integer TEST_GROUP_STATE_OPEN = 1;

    //实验分组类型：0:默认分组, 1：对照组, 2:测试组
    public static final Integer TEST_GROUP_TYPE_CONTRAST = 1;
    public static final Integer TEST_GROUP_TYPE_TEST = 2;

    //是否阶梯广告，1阶梯广告，2通跑广告
    public static final Integer AD_LADDER_TYPE = 1;
    public static final Integer AD_COMMON_TYPE = 2;

    //广告平台类型
    public static final String AD_PLAT_TYPE_98 = "98_adv";

    //省市区类型
    public static final int DISTRICT_PROVINCE = 1;
    public static final int DISTRICT_CITY = 2;
    public static final int DISTRICT_AREA = 3;

}
