package com.miguan.ballvideo.vo.userBuryingPoint;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class UserBuryingPointActivityVo {

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("新老用户:10-新用户，20老用户")
    private int isNew = 20;

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("来源渠道号")
    private String channelId;

    @ApiModelProperty("设备权限Imei")
    private String imei;

    @ApiModelProperty("设备id")
    private String deviceId;

    @ApiModelProperty("app版本")
    private String appVersion;

    @ApiModelProperty("手机版本")
    private String osVersion;

    @ApiModelProperty("手机系统类型：andriod/ios")
    private String systemVersion;

    @ApiModelProperty("App包名")
    private String appPackage = "com.mg.xyvideo";

    @ApiModelProperty("创建时间")
    private Date creatTime;

    @ApiModelProperty("后台统计用到")
    private String createDate;

    @ApiModelProperty("启动事件标识")
    private String actionId;

    /**
     * task_pageview事件：
     10：任务首页：21：每日签到；22：活动规则；23：明细查询；24：提现页面；30：砸金蛋；31：幸运转转转
     task_click事件：
     10：赚金币；21：规则；22立即提现1；23：去看视频；24：完善资料-领取金币；25：新人注册-领取金币；26：金币提现；27：每日签到-立即签到；
     28：看创意视频赚金币-立即观看；29：锁屏看视频；30：分享视频；31：玩小游戏；32:7日打卡挑战；33提现页面-立即提现2；40：砸金蛋；41幸运转转转
     */
    @ApiModelProperty("类型")
    private int source;

    @ApiModelProperty("浏览时长")
    private String videoPlayTime;

    @ApiModelProperty("应用类别：xysp-茜柚视频，ggsp-果果视频")
    private String appType;

}