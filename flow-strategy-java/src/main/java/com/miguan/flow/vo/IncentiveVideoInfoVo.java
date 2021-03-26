package com.miguan.flow.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("获取激励视频的信息")
@Data
public class IncentiveVideoInfoVo {

    @ApiModelProperty("分类id")
    private Integer catId;

    @ApiModelProperty("激励视频的id")
    private Integer incentiveVideoId;
}
