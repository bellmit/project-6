package com.xiyou.speedvideo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description TODO
 * @Author zhangbinglin
 * @Date 2021/2/2 13:43
 **/
@Data
public class BaiduLabelParams {

    @ApiModelProperty("视频id")
    private Integer videoId;

    @ApiModelProperty("倍速处理后的视频url")
    private String bsyUrl;
}
