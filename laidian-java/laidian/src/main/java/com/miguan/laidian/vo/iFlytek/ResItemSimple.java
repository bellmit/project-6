package com.miguan.laidian.vo.iFlytek;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * Created by 98du on 2020/8/13.
 */
@Data
@ApiModel("铃音信息")
public class ResItemSimple implements Serializable{

    @ApiModelProperty("铃音编号")
    private String id;

    @ApiModelProperty("铃音名称")
    private String title;

    @ApiModelProperty("试听url")
    private String audiourl;

    @ApiModelProperty("歌手")
    private String singer;

    @ApiModelProperty("播放时长")
    private String duration;

    @ApiModelProperty("试听次数")
    private String listencount;

    @ApiModelProperty("介绍语")
    private String aword;

    @ApiModelProperty("铃音的标签:0-无标签，1-new标签，2-hot标签，3-同时打上new和hot标签")
    private String icon;

    @ApiModelProperty("Mp3音频大小，单位bytes")
    private String mp3sz;

    @ApiModelProperty("歌曲封面图片url，注意:此属性不是所有铃音都有，建议准备多个默认图片")
    private String imgurl;

    @ApiModelProperty("彩铃H5链接")
    String ringurl;
}
