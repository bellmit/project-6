package com.miguan.ballvideo.vo;

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

    @ApiModelProperty("作用包中文名")
    private String name;

}
