package com.miguan.xuanyuan.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("ab实验")
@Data
public class AbExpDto {

    @ApiModelProperty("广告位id")
    private Long positionId;

    @ApiModelProperty("实验名称")
    private String expName;

    @ApiModelProperty("总流量")
    private Integer totalRate;

    @ApiModelProperty("对照组流量")
    private Integer conRate;

    @ApiModelProperty("测试组流量")
    private Integer testRate;

}
