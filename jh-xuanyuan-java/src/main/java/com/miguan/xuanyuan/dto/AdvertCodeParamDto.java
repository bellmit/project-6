package com.miguan.xuanyuan.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel("广告VO")
@Data
public class AdvertCodeParamDto {

    @ApiModelProperty("广告位KEY")
    @NotBlank(message = "广告位KEY不能为空！")
    private String positionKey;

    @ApiModelProperty("应用key")
    @NotBlank(message = "应用KEY不能为空！")
    private String appKey;

    @ApiModelProperty("手机类型：1-ios，2：安卓")
    @NotBlank(message = "手机类型不能为空！")
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
    private String city;

    @ApiModelProperty(value="ab测试id", hidden = true)
    private Long abTestId;
}
