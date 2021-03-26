package com.miguan.ballvideo.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("视频类型")
public class VideoCatRes {
    @ApiModelProperty("视频类型id")
    private Long id;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("类型")
    private String type;

}
