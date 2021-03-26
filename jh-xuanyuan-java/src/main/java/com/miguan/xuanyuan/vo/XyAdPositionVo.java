package com.miguan.xuanyuan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("轩辕广告位")
@Data
public class XyAdPositionVo {
    @ApiModelProperty("用户id")
    private Long userId;
    @ApiModelProperty("媒体账号名称")
    private String username;
    @ApiModelProperty("应用图标")
    private String icon;
    @ApiModelProperty("操作系统logo")
    private String clientLogo;
    @ApiModelProperty("应用id")
    private Long appId;
    @ApiModelProperty("应用KEY")
    private String appKey;
    @ApiModelProperty("应用密钥")
    private String appSecret;
    @ApiModelProperty("sha")
    private String sha;
    @ApiModelProperty("应用名称")
    private String appName;
    @ApiModelProperty("广告位id")
    private Long id;
    @ApiModelProperty("广告位key")
    private String positionKey;
    @ApiModelProperty("广告位名称")
    private String positionName;
    @ApiModelProperty("广告样式名称")
    private String adTypeName;
    @ApiModelProperty("状态")
    private String status;
    @ApiModelProperty("客户端类型，1安卓，2ios")
    private Integer clientType;
    @ApiModelProperty("广告样式")
    private String adType;

}
