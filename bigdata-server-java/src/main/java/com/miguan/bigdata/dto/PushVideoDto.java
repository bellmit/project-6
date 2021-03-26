package com.miguan.bigdata.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PushVideoDto {

    @ApiModelProperty(value = "视频id", position = 10)
    private Integer videoId;

    @ApiModelProperty(value = "观看数(播放数)", position = 20)
    private Integer playNum;

    @ApiModelProperty(value = "完播数", position = 30)
    private Integer playEndNum;
}
