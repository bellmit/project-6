package com.miguan.ballvideo.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "pop_conf_video")
public class PopConfVideo {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    /**
     * 项目：来电-1，视频-2，百步赚-3，清理大师-4
     */
    @Column(name = "project_type")
    private Integer projectType;

    /**
     * 用户激活日
     */
    @Column(name = "act_day")
    private Integer actDay;

    /**
     * 视频id
     */
    @Column(name = "video_id")
    private Long videoId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 落地页类型:1-短视频,2-启动APP,3-链接
     */
    private Integer type;

    /**
     * 封面图
     */
    @Column(name = "img_url")
    private String imgUrl;

    /**
     * 跳转链接
     */
    @Column(name = "link_url")
    private String linkUrl;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private Date updatedAt;
}