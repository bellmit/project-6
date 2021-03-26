package com.miguan.ballvideo.vo;

import com.miguan.ballvideo.vo.video.FirstVideosPublicationVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("用户中心实体")
public class UserCenterVo {

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("用户昵称")
    private String name;

    @ApiModelProperty("头像URL")
    private String imgUrl;

    @ApiModelProperty("用户签名")
    private String sign;

    @ApiModelProperty("用户发布的视频总数")
    private Long videosCount;

    @ApiModelProperty("用户发布的视频被点赞数总和")
    private Long videosLCount;

    @ApiModelProperty("用户发布的视频被收藏数总和")
    private Long videoCCount;

    @ApiModelProperty("用户发布的视频列表")
    List<FirstVideosPublicationVo> videosList;
}
