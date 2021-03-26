package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("视频曝光数")
public class VideoExposureVo {

    @ApiModelProperty("分类id")
    private String catId;

    @ApiModelProperty("曝光数")
    private String count;

}
