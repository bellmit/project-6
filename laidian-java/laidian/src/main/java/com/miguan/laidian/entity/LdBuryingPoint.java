package com.miguan.laidian.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LdBuryingPoint {

    /**
     * 自增id
     */
    private Long id;

    /**
     * 埋点类型id
     */
    private String actionId;

    /**
     * 打开时间
     */
    private Date openTime;

    /**
     * 上次打开时间
     */
    private Date upOpenTime;

    /**
     * 渠道id
     */
    private String channelId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * app版本
     */
    private String appVersion;

    /**
     * 手机版本
     */
    private String osVersion;

    /**
     * 是否开启所有权限
     */
    private Boolean allPermission;

    /**
     * 是否进入详情
     */
    private Boolean intoFodderDetail;

    /**
     * 是否开启所有权限
     */
    private Boolean setPermission;

    /**
     * 是否登录激活
     */
    private Boolean appStart;

    /**
     * 点击设置来电秀按钮1
     */
    private Boolean clickSetFodderButton;

    /**
     * 点击设置来电秀按钮2
     */
    private Boolean clickSetFodderButton2;

    /**
     * 是否成功设置炫来电
     */

    private Boolean successSetFodder;

    private Date createTime;

    private Date createDay;

    /**
     * 设备型号
     */
    private String deviceInfo;

    /**
     * app类型  微来电-wld,炫来电-xld
     */
    private String appType;

    private String serialNumber;//一个设备ID有可能多个

}