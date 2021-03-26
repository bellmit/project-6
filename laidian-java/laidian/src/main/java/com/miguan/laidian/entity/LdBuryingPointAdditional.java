package com.miguan.laidian.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LdBuryingPointAdditional {

    public static final String ACTIVATION_BURYING = "activation_burying";        //激活埋点

    public static final String SET_THE_CALL = "set_the_call";        //设置来电
    public static final String SET_THE_CALL_1 = "set_the_call_1";        //设置来电1
    public static final String SET_THE_CALL_2 = "set_the_call_2";        //设置来电2
    public static final String SET_THE_CALL_3 = "set_the_call_3";        //设置来电3
    public static final String SELECTION_SORT = "selection_sort";        //选择分类


    public static final String A_SMALL_VIDEO_INITIALIZATION = "a_small_video_Initialization";          //小视频
    public static final String A_SMALL_VIDEO = "a_small_video";          //小视频
    public static final String A_SMALL_VIDEO_1 = "a_small_video_1";      //小视频1
    public static final String A_SMALL_VIDEO_2 = "a_small_video_2";      //小视频2
    public static final String A_SMALL_VIDEO_5 = "a_small_video_5";      //小视频5

    public static final String MAKE_MONEY = "make_money";                //赚钱
    public static final String HAS_LOGGED_ON = "has_logged_on";          //已登录
    public static final String THE_TASK_CENTER = "the_task_center";      //任务中心
    public static final String USERS_TO_UPLOAD = "users_to_upload";      //用户上传
    public static final String NEWS_CLICK = "news_click";                //资讯


    /**
     * 自增id
     */
    private Long id;

    /**
     * 启动事件标识
     */
    private String actionId;

    /**
     * 渠道id
     */
    private String channelId;

    /**
     * 用户id
     */
    private String userId;

    /**
     *  设备id
     */
    private String deviceId;

    /**
     *  app版本
     */
    private String appVersion;

    /**
     *  手机版本
     */
    private String osVersion;

    /**
     *  app类型
     */
    private String appType;

    /**
     *  手机型号
     */
    private String deviceInfo;

    /**
     *  设置来电秀1
     */
    private Boolean setTheCall1;

    /**
     *  设置来电秀2
     */
    private Boolean setTheCall2;

    /**
     *  设置来电秀3
     */
    private Boolean setTheCall3;

    /**
     *  选择分类
     */
    private Boolean selectionSort;

    /**
     *  小视频
     */
    private Boolean aSmallVideo;

    /**
     *  小视频1
     */
    private Boolean aSmallVideo1;

    /**
     *  小视频2
     */
    private Boolean aSmallVideo2;

    /**
     * 小视频5
     */
    private Boolean aSmallVideo5;

    /**
     * 资讯
     */
    private Boolean newsClick;

    /**
     * 赚钱
     */
    private Boolean makeMoney;

    /**
     * 已登录
     */
    private Boolean hasLoggedOn;

    /**
     * 任务中心
     */
    private Boolean theTaskCenter;

    /**
     * 用户上传
     */
    private Boolean usersToUpload;

    /**
     * 创建时间
     */
    private Date submitTime;

    /**
     * 视频id
     */
    private Integer videosId;

    /**
     * 是否新用户
     */
    private Boolean userState;

}