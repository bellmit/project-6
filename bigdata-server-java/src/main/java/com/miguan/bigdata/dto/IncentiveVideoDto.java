package com.miguan.bigdata.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 激励视频出库、入库
 */
@Data
public class IncentiveVideoDto {

    @ApiModelProperty("激励视频id")
    private Long incentiveVideoId;

    @ApiModelProperty("分类id")
    private Long catId;
}
