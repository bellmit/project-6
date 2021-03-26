package com.miguan.ballvideo.entity;

import com.miguan.ballvideo.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@ApiModel("视频举报")
@Entity(name="videos_report")
@Data
public class VideosReport extends BaseModel {

    @ApiModelProperty("视频Id")
    @Column(name = "video_id")
    private Long videoId;

    @ApiModelProperty("视频类型 10 首页视频 20小视频")
    @Column(name = "video_type")
    private Integer videoType;

    @ApiModelProperty("视频举报数")
    @Column(name = "report_count")
    private Long reportCount;

    @ApiModelProperty("视频观看数")
    @Column(name = "watch_count")
    private Long watchCount;

}
