package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class AdvertErrorLogVo{

    @ApiModelProperty("广告平台（例如：穿山甲-chuan_shan_jia，广点通-guang_dian_tong，广告-98_adv）")
    private String platKey;

    @ApiModelProperty("设备id")
    private String deviceId;

    @ApiModelProperty("广告位置Id")
    private String positionId;

    @ApiModelProperty("代码位ID：第三方或98广告后台生成的广告ID")
    private String adId;

    @ApiModelProperty("广告类型（例如：信息流-c_flow，插屏广告-c_table_screen，Draw信息流广告-c_draw_flow）")
    private String typeKey;

    @ApiModelProperty("包名：ex(xld,wld)")
    private String appPackage;

    @ApiModelProperty("版本")
    private String appVersion;

    @ApiModelProperty("手机类型 1ios 2Android")
    private String mobileType;

    @ApiModelProperty("错误信息")
    private String adError;

    @ApiModelProperty("创建时间(Java处理)")
    private Date creatTime;

    @ApiModelProperty("渲染方式")
    private String render;

    @ApiModelProperty("渠道ID")
    private String channelId;

    @ApiModelProperty("sdk版本号")
    private String sdk;

    @ApiModelProperty("创建时间(APP传递时间)")
    private String appTime;

}
