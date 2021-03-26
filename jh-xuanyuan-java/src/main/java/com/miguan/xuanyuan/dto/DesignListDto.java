package com.miguan.xuanyuan.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="创意列表dto", description="创意列表dto")
public class DesignListDto {

    @ApiModelProperty(value = "创意id", required = true, position = 5)
    private Integer id;

    @ApiModelProperty(value = "启用状态：1启用，0未启用", position = 10)
    private Integer status;

    @ApiModelProperty(value = "创意名称", position = 20)
    private String name;

    @ApiModelProperty(value = "创意类型,1:图片，2：视频", position = 21)
    private Integer materialType;

    @ApiModelProperty(value = "创意尺寸：1--(视频)9:16，2--(视频)16:9，3--(视频)3:2，4--(视频)2:3，5-(视频)-2:1，6--(视频)1:1，7--(图片)9:16，8--(图片)16:9", position = 30)
    private String materialShape;

    @ApiModelProperty(value = "曝光数", position = 40)
    private Integer show;

    @ApiModelProperty(value = "点击数", position = 60)
    private Integer click;

    @ApiModelProperty(value = "点击率（%）", position = 70)
    private Double clickRate;

    @ApiModelProperty(value = "创意权重", required = true, position = 80)
    private Integer weight;
}
