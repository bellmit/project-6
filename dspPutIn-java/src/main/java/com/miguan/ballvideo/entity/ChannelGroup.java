package com.miguan.ballvideo.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("渠道分组实体")
public class ChannelGroup {

    @ApiModelProperty("作用包")
    private String appPackage;

    @ApiModelProperty("短信签名")
    private String msgSign;

}
