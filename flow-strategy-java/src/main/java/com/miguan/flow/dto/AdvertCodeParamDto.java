package com.miguan.flow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("广告VO")
@Data
public class AdvertCodeParamDto {

    @ApiModelProperty("广告位置类型(广告位关键字),空则查询全部")
    private String positionType;

    @ApiModelProperty("手机类型：1-ios，2：安卓")
    private String mobileType;

    @ApiModelProperty("渠道ID")
    private String channelId;

    @ApiModelProperty("权限是否开启(手机的存储和gps权限)：0没有开启，1开启")
    private String permission;

    @ApiModelProperty("app版本号")
    private String appVersion;

    @ApiModelProperty("作用包")
    private String appPackage;

    @ApiModelProperty(value="是否新用户(根据app维度)，1-新用户，0-老用户")
    private Integer isNewApp;

    @ApiModelProperty(value="城市名，例如：厦门市")
    private String gpscity;

    @ApiModelProperty(value="ab测试id", hidden = true)
    private Long abTestId;
}
