package com.miguan.ballvideo.vo.video;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author laiyd
 */
@Data
@ApiModel("曝光视频信息")
public class VideoExposureCountVo {

    @ApiModelProperty("视频Id")
    private Long videoId;

    @ApiModelProperty("设备Id")
    private String deviceId;

    @ApiModelProperty("曝光数量")
    private Long count;

    @ApiModelProperty("曝光时间")
    private Long createTime;
}
