package com.miguan.xuanyuan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("轩辕应用")
@Data
public class XyAppVo {
    @ApiModelProperty("媒体账号名称")
    private String username;
    @ApiModelProperty("应用id")
    private Long id;
    @ApiModelProperty("应用KEY")
    private String appKey;
    @ApiModelProperty("应用密钥")
    private String appSecret;
    @ApiModelProperty("sha值")
    private String sha;
    @ApiModelProperty("应用名称")
    private String appName;
    @ApiModelProperty("产品名称")
    private String productName;
    @ApiModelProperty("app图标")
    private String icon;
    @ApiModelProperty("客户端类型，1安卓，2ios")
    private Integer clientType;
    @ApiModelProperty("操作系统logo")
    private String clientLogo;
    @ApiModelProperty("包名")
    private String packageName;
    @ApiModelProperty("包下载链接")
    private String downloadUrl;
    @ApiModelProperty("状态")
    private String status;

}
