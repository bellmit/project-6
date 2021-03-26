package com.miguan.ballvideo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class PushVideo {
    private Long id;
    private Long videoId; //视频id
    private Long playNum;//观看数(播放数)
    private Long vplayNum;//有效播放数
    private Long playEndNum;//完播数

    @JsonFormat
    private Date createdAt;

    @JsonFormat
    private Date updatedAt;
}
