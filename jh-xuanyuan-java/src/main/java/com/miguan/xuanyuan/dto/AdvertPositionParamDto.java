package com.miguan.xuanyuan.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel("广告位请求参数")
@Data
public class AdvertPositionParamDto {

    @ApiModelProperty("广告位KEY")
    @NotBlank(message = "广告位KEY不能为空！")
    private String positionKey;

    @ApiModelProperty("应用key")
    @NotBlank(message = "应用KEY不能为空！")
    private String appKey;

    @ApiModelProperty("手机类型：1-安卓，2：ios")
    @NotBlank(message = "手机类型不能为空！")
    private String mobileType;

    @ApiModelProperty("渠道ID")
    private String channelId;

    @ApiModelProperty("app版本号")
    private String appVersion;
}
