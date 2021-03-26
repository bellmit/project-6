package com.miguan.laidian.vo.iFlytek;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by 98du on 2020/8/13.
 */
@ApiModel("高清格式的视频信息")
@Data
public class Video{

    @ApiModelProperty("视频url")
    private String url;

    @ApiModelProperty("视频大小，单位bytes")
    private String size;

    @ApiModelProperty("视频宽度，单位像素")
    private String width;

    @ApiModelProperty("视频高度，单位像素")
    private String height;

}