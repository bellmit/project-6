package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GarbageBuryingPointVo {

    @ApiModelProperty("设备ID")
    private String deviceId;

    @ApiModelProperty("app包名")
    private String appPackage;

    @ApiModelProperty("埋点类型  0：未清理  1：清理")
    private String type;
}
