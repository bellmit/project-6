package com.miguan.xuanyuan.common.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XyConstant {
    //分页默认值
    public static final int INIT_PAGE = 1; //默认页码
    public static final int INIT_PAGE_SIZE = 10; // 一页默认数
    public static final int INIT_MIN_PAGE_SIZE = 1; //分页最小值
    public static final int INIT_MAX_PAGE_SIZE = 500; //分页最大值


    //应用--状态
    public static final int APP_STATUS_START = 0;   //已启用
    public static final int APP_STATUS_EXAMINE = 1; //待审核
    public static final int APP_STATUS_STOP = 1;    //已禁用


    //通用--状态
    public static final int STATUS_NORMAL = 1; //正常
    public static final int STATUS_CLOSE = 0; //禁用

    //通用-删除状态
    public static final int DEL_STATUS = 1; //删除
    public static final int UN_DEL_STATUS = 0; //未删除

    public static final String STRATEGY_GROUP_DEFAULT_NAME = "默认分组";


    public static final String ANDROID = "1";//手机类型：1：安卓
    public static final String IOS = "2";//手机类型：2-ios
    public static final String WeChat = "3";//手机类型:3：微信小程序



    public static final int IS_SHOW = 1; //显示
    public static final int UN_SHOW = 0; //隐藏

    //用户类型
    public static final int USER_TYPE_MEDIA = 1; //媒体用户
    public static final int USER_TYPE_98 = 2; //98用户

    //媒体用户类型
    public static final int MEDIA_TYPE_BUSINESS = 1; //企业
    public static final int MEDIA_TYPE_PERSON = 2; //个人

    //前后台
    public static final int FRONT_PLAT = 1; //前台
    public static final int BACK_PLAT = 2; //后台

    //操作日志 增删改
    public static final int LOG_ADD = 1; // 新增
    public static final int LOG_UPDATE = 2; // 修改
    public static final int LOG_DELETE = 3; // 删除
    public static final int LOG_SAVE = 4; // 新增或者修改
    //媒体账号初始化密码
    public static final String INIT_PASSWORD = "9D8uabc123";

    //计划是否是复制操作
    public static final int PLAN_COPY = 1;

    //广告计划分享开关
    public static final int PLAN_SHARE_SWITCH_OPEN = 1;
    public static final int PLAN_SHARE_SWITCH_CLOSE = 0;

    //广告计划分享报表有效时间
    public static final int PLAN_REPORT_SHARE_EXPIRED = 7;

    //广告报表 非创意
    public static final int REPORT_NO_DESIGN = -99;

    //广告投放时间
    public static final int PLAN_PUT_TIME_ALL = 0;
    public static final int PLAN_PUT_TIME_BETTWEN = 1;
    //广告时间设置
    public static final int PLAN_TIME_SETTING_ALL = 0;
    public static final int PLAN_TIME_SETTING_BETTWEN = 1;
    public static final int PLAN_TIME_SETTING_CONFIG = 2;

    //轩辕品牌广告source_plat_key
    public static final String XUANYUAN_BRAND_SOURCE_PLAT_KEY = "xy_brand";

    //轩辕品牌广告来源名称
    public static final String XUANYUAN_BRAND_SOURCE_NAME = "轩辕";
    //轩辕品牌广告appId
    public static final String XUANYUAN_DEFAULT_APP_ID = "xy12cad";


    public final static Map<String, String> CHANNEL_OPERATION_MAP = new HashMap(); //渠道操作
    public final static Map<String, String> VERSION_OPERATION_MAP = new HashMap(); //版本操作列表

    //投放时间--全部
    public static final Integer TIME_SETTING_ALL = 0;

    //投放时间--指定日期
    public static final Integer TIME_SETTING_APPOINT = 1;

    //投放时间--指定多个日期
    public static final Integer TIME_SETTING_APPOINT_MANY = 2;

    //操作日志 增删改
    public static final String OPERA_ALL = "-1"; // 全部
    public static final String OPERA_IN = "in"; //  in
    public static final String OPERA_NOT_IN = "not in"; // not in
    public static final String OPERA_EQUAL = "="; // 相等
    public static final String OPERA_GREATER = ">"; // 大于
    public static final String OPERA_GREATER_EQUAL = ">="; // 大于等于
    public static final String OPERA_LESS = "<"; // 小于
    public static final String OPERA_LESS_EQUAL = "<="; // 小于等于


    public final static List<String> PLAN_AD_TYPE_LIST = new ArrayList<>(); //广告计划支持的广告样式
    public final static Map<String, String> PLAN_AD_TYPE_CODE_PREFIX_MAP = new HashMap<>(); //广告计划支持的广告样式对应的代码位前缀


    static {
        //渠道操作符
        CHANNEL_OPERATION_MAP.put(OPERA_ALL, "全部");
        CHANNEL_OPERATION_MAP.put(OPERA_IN, "in");
        CHANNEL_OPERATION_MAP.put(OPERA_NOT_IN, "not in");

        //版本操作符
        VERSION_OPERATION_MAP.put(OPERA_ALL, "全部");
        VERSION_OPERATION_MAP.put(OPERA_IN, "in");
        VERSION_OPERATION_MAP.put(OPERA_NOT_IN, "not in");
        VERSION_OPERATION_MAP.put(OPERA_EQUAL, "=");
        VERSION_OPERATION_MAP.put(OPERA_GREATER, ">");
        VERSION_OPERATION_MAP.put(OPERA_GREATER_EQUAL, ">=");
        VERSION_OPERATION_MAP.put(OPERA_LESS, "<");
        VERSION_OPERATION_MAP.put(OPERA_LESS_EQUAL, "<=");

        //广告计划支持的广告样式
        PLAN_AD_TYPE_LIST.add("open_screen");
        PLAN_AD_TYPE_LIST.add("infoFlow");
        PLAN_AD_TYPE_LIST.add("interaction");
        PLAN_AD_TYPE_LIST.add("banner");

        //广告计划代码位前缀
        PLAN_AD_TYPE_CODE_PREFIX_MAP.put("open_screen", "xy1");
        PLAN_AD_TYPE_CODE_PREFIX_MAP.put("infoFlow", "xy2");
        PLAN_AD_TYPE_CODE_PREFIX_MAP.put("interaction", "xy3");
        PLAN_AD_TYPE_CODE_PREFIX_MAP.put("banner", "xy4");




    }


}

