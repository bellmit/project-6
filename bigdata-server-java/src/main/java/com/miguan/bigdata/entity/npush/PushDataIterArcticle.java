package com.miguan.bigdata.entity.npush;

import lombok.Data;

import java.util.Date;

@Data
public class PushDataIterArcticle {

    private Long id;
    private Date createdAt;
    private Date updatedAt;
    /**
     * 状态 1开启 2关闭
     */
    private Integer state;
    /**
     * 激活日
     */
    private Integer actDay;
    /**
     * 当日推送排序
     */
    private Integer sort;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 项目：来电-1，视频-2，百步赚-3
     */
    private Integer projectType;
    /**
     * 1.app启动 2.链接 3.短视频 4.小视频 5.金币任务 6.快手小视频
     */
    private Integer type;
    /**
     * 视频ID
     */
    private String videoId;
    /**
     * 视频标题
     */
    private String videoTitle;
    /**
     * 视频封面图URL
     */
    private String videoCoverUrl;
    /**
     * 跳转链接URL
     */
    private String jumpUrl;

}
