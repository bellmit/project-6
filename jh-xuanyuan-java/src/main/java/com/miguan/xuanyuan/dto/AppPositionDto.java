package com.miguan.xuanyuan.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="应用广告位dto", description="应用广告位dto")
public class AppPositionDto {

    @ApiModelProperty(value = "广告位Id", position = 10)
    private Integer positionId;

    @ApiModelProperty(value = "应用名称", position = 20)
    private String appName;

    @ApiModelProperty(value = "广告位名称", position = 30)
    private String positionName;
}
