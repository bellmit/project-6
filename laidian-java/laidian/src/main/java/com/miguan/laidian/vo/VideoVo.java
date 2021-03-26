package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 视频源列表bean
 *
 * @author xy.chen
 * @date 2019-07-08
 **/
@Data
@ApiModel("安卓视频源VO")
public class VideoVo implements Serializable {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("源视频url")
    private String url;

    @ApiModelProperty("分类id")
    private Long catId;

    @ApiModelProperty("源音频url")
    private String urlAudio;

    @ApiModelProperty("源图片url")
    private String urlImg;

    @ApiModelProperty("本地视频地址")
    private String localUrl;

    @ApiModelProperty("白山云视频地址")
    private String bsyUrl;

    @ApiModelProperty("本地图片地址")
    private String localImgUrl;

    @ApiModelProperty("白山云图片地址")
    private String bsyImgUrl;

    @ApiModelProperty("收藏数量")
    private String likeCount;

    @ApiModelProperty("分享数量")
    private Long shareCount;

    @ApiModelProperty("点击数")
    private Long clickCount;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("创建时间")
    private String createdAt;

    @ApiModelProperty("更新时间")
    private String updatedAt;

    @ApiModelProperty("原始收藏数量")
    private String originalLikeCount;

    @ApiModelProperty("本地音频地址")
    private String localAudioUrl;

    @ApiModelProperty("白山云音频地址")
    private String bsyAudioUrl;

    @ApiModelProperty("用户名称")
    private String userName;

    @ApiModelProperty("强制广告：0否 1是")
    private String forceAdv;

    @ApiModelProperty("上传视频类型：1自己用 2大家用")
    private String videoType;

    @ApiModelProperty("是否历史设置过 1是 0否")
    private int historyTab;

}
