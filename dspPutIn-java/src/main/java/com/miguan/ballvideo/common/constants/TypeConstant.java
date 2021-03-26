package com.miguan.ballvideo.common.constants;

public class TypeConstant {

    //投放日期--全部日期
    public static final Integer DATE_TYPE_ALL = 1;

    //投放日期--指定日期
    public static final Integer DATE_TYPE_APPOINT = 2;


    //投放时间--全部
    public static final Integer TIME_SETTING_ALL = 0;

    //投放时间--指定日期
    public static final Integer TIME_SETTING_APPOINT = 1;

    //投放时间--指定多个日期
    public static final Integer TIME_SETTING_APPOINT_MANY = 2;

    //区域类型
    public static final Integer  TYPE_COUNTRY = 0;//国家
    public static final Integer  TYPE_PROVINCE = 1;//省份
    public static final Integer  TYPE_CITY = 2;//城市
    public static final Integer  TYPE_DISTRICT = 3;//区县

    //区域类型限制
    public static final Integer  AREA_TYPE_NO_LIMIT = 1;//不限
    public static final Integer  AREA_TYPE_LIMIT = 2;//限制地区

    //手机品牌类型限制
    public static final Integer  PHONE_TYPE_NO_LIMIT = 1;//不限
    public static final Integer  PHONE_TYPE_LIMIT = 2;//限制品牌


    //手机品牌类型限制
    public static final String  UPLOAD_FILE_TYPE_IMAGE = "image";//图片
    public static final String  UPLOAD_FILE_TYPE_VIDEO = "video";//视频
}
