package com.miguan.laidian.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("视频标签实体类")
public class VideoLabelVo {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("视频分类ID")
    private Long catId;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("参数对应的值")
    private Integer weight;
}
