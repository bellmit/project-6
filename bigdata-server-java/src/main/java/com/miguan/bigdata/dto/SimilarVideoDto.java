package com.miguan.bigdata.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SimilarVideoDto {

    @ApiModelProperty("视频id")
    private Integer videoId;

    @ApiModelProperty("视频标题")
    private String title;

    @ApiModelProperty("视频背景图片url")
    private String bsyImgUrl;

    @ApiModelProperty("标题相似度(取值范围：0至1)")
    private Double titleSimScore;

    @ApiModelProperty("背景图片相似度(取值范围：0至1)")
    private Double imgSimScore;
}
