package com.miguan.flow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("激励视频Dto")
@Data
public class IncentiveVideoDto {

    @ApiModelProperty("视频id")
    private Integer videoId;

    @ApiModelProperty("视频标题")
    private String title;

    @ApiModelProperty("视频Url")
    private String bsyUrl;

    @ApiModelProperty("视频背景图片url")
    private String bsyImgUrl;

    @ApiModelProperty("视频总播放时长（分:秒，例如03:31）")
    private String videoTime;

    @ApiModelProperty("视频播放次数")
    private Integer watchCount;
}
