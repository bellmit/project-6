package com.miguan.ballvideo.entity;

import com.miguan.ballvideo.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity(name = "videos_share")
@ApiModel("分享视频信息记录")
public class VideosShare extends BaseModel {

    @ApiModelProperty("视频Id")
    @Column(name = "video_id")
    private Long videoId;

    @ApiModelProperty("分享类型 0：小程序分享 1：H5分享")
    @Column(name = "share_type")
    private Integer shareType;

    @ApiModelProperty("视频分享数")
    @Column(name = "share_count")
    private Long shareCount;
}
