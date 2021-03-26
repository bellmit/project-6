package com.miguan.ballvideo.vo.video;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 搜索词关联视频
 * @Author laiyd
 * @Date 2020/8/15
 **/
@Data
public class HotWordVideoVo {

    @ApiModelProperty("视频id")
    private Long videoId;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("排序方式:weight-权重倒叙，index-手动排序")
    private String videoSort;
}
