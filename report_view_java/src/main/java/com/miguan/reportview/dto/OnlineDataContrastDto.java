package com.miguan.reportview.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 线上短视频-数据对比dto
 */
@Setter
@Getter
@ApiModel
public class OnlineDataContrastDto {

    @ApiModelProperty(value = "数据统计日期", position = 10)
    private String dd;

    @ApiModelProperty(value = "修改日期", position = 20)
    private String updatedAt;

    @ApiModelProperty(value = "曝光数", position = 30)
    private Long showCount;

    @ApiModelProperty(value = "播放数", position = 40)
    private Long playCount;

    @ApiModelProperty(value = "播放率", position = 50)
    private Double playRate;

}
