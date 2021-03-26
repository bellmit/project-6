package com.miguan.bigdata.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SimilarParamDto {
    @ApiModelProperty("视频标题")
    private String title;

    @ApiModelProperty("封面图片url")
    private String imgUrl;

    @ApiModelProperty("标题相似度阈值(取值范围：0至1)")
    private Double titleThreshold;

    @ApiModelProperty("图片相似度阈值(取值范围：0至1)")
    private Double imgThreshold;
}
