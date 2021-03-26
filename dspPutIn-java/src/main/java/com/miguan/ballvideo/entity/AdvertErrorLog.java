package com.miguan.ballvideo.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel("广告错误日志展示表")
@Entity(name = "ad_error_1")
public class AdvertErrorLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty("设备id")
    private String deviceId;

    @ApiModelProperty("广告位置key")
    private String positionId;

    @ApiModelProperty("广告类型标识")
    private String typeKey;

    @ApiModelProperty("展示类型 0广点通 1穿山甲 2:98类型")
    private String platKey;

    @ApiModelProperty("广告代码位")
    private String adId;

    @ApiModelProperty("包名：ex(com.mg.xyvideo)")
    private String appPackage;

    @ApiModelProperty("版本")
    private String appVersion;

    @ApiModelProperty("手机类型 1ios 2Android")
    private String mobileType;

    @ApiModelProperty("错误信息")
    private String adError;

    @ApiModelProperty("创建时间")
    private Date creatTime;

    @ApiModelProperty("渲染方式")
    private String render;

    @ApiModelProperty("渠道ID")
    private String channelId;

    @ApiModelProperty("sdk版本号")
    private String sdk;

    @ApiModelProperty("创建时间(APP传递时间)")
    private Long appTime;

}
