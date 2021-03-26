package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysAppChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("最新版本")
    private String appVersion;

    @ApiModelProperty("渠道值")
    private String channelCode;

    @ApiModelProperty("真实人数")
    private String realUser;

}
