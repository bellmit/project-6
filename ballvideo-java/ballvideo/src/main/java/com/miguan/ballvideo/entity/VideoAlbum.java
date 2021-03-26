package com.miguan.ballvideo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 视频专辑表
 **/
@Entity(name="video_album")
@Data
public class VideoAlbum{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @ApiModelProperty("标题")
    @Column(name = "title")
    private String title;
    @ApiModelProperty("简介")
    @Column(name = "intro")
    private String intro;
    @ApiModelProperty("封面图片地址")
    @Column(name = "cover_image_url")
    private String coverImageUrl;
    @ApiModelProperty("排序")
    @Column(name = "sort")
    private int sort;
    @ApiModelProperty("是否需要解锁:1=是,-1=否")
    @Column(name = "need_unlock")
    private int needUnlock;
    @JsonFormat
    @ApiModelProperty("新增时间")
    @Column(name = "created_time")
    private Date createdTime;
    @JsonFormat
    @ApiModelProperty("更新时间")
    @Column(name = "updated_time")
    private Date updatedTime;
}
