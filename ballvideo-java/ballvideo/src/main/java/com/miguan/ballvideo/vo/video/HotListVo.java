package com.miguan.ballvideo.vo.video;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("热榜视频VO")
public class HotListVo extends Videos161Vo {
    @ApiModelProperty("热榜ID")
    private Long hotId;

    @ApiModelProperty("热榜热度")
    private String viewsNum;

    @ApiModelProperty("热榜排序")
    private Long hotSort;
}
