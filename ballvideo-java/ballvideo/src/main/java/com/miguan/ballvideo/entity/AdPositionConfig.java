package com.miguan.ballvideo.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("切片广告配置信息")
@Data
public class AdPositionConfig {

    @ApiModelProperty("广告位置key")
    private Long adPositionId;

    @ApiModelProperty("穿山甲概率")
    private Double csjRate;

    @ApiModelProperty("广点通概率")
    private Double gdtRate;

    @ApiModelProperty("98度广告概率")
    private Double jsbRate;

    @ApiModelProperty("广告位_手机类型")
    private String keywordMobileType;

    @ApiModelProperty("包名")
    private String appCode;
}
