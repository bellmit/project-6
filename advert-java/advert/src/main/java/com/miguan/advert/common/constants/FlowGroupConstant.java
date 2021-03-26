package com.miguan.advert.common.constants;

public class FlowGroupConstant {
    //分组类型：1：默认分组
    public static final Integer DEFAULT_GROUP_TYPE = 1;
    //分组类型：2:手动分组
    public static final Integer HANDLER_GROUP_TYPE = 2;

    public static final String DEFAULT_FLOW_GROUP_NAME = "默认分组";
    public static final String AB_FLOW_GROUP_NAME = "AB默认分组";

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
    public static final Integer TEST_GROUP_TYPE_DUIZHAO = 1;
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
