package com.miguan.laidian.vo.iFlytek;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * Created by 98du on 2020/8/13.
 */
@Data
@ApiModel("视频信息")
public class VideoRing implements Serializable{

    @ApiModelProperty("视频编号")
    private String id;

    @ApiModelProperty("视频名称")
    private String nm;

    @ApiModelProperty("预览视频url，用于APP端播放")
    private String url;

    @ApiModelProperty("预览图url，用于APP端展现封面")
    private String pvurl;

    @ApiModelProperty("视频的配乐信息")
    private BackgroundMusic song;

    @ApiModelProperty("视频大小，单位bytes")
    private String size;

    @ApiModelProperty("视频宽度，单位像素")
    private String width;

    @ApiModelProperty("视频高度，单位像素")
    private String height;

    @ApiModelProperty("视频时长，单位秒")
    private String duration;

    @ApiModelProperty("更多高清格式的视频信息，推荐设置本地场景使用")
    private List<Video> videos;

    @ApiModelProperty("彩铃H5链接")
    String ringurl;

}
