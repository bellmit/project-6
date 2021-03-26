package com.miguan.ballvideo.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "pop_conf_auto_search")
public class PopConfAutoSearch {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    /**
     * 项目：来电-1，视频-2，百步赚-3，清理大师-4
     */
    @Column(name = "project_type")
    private Integer projectType;

    /**
     * app包名，逗号隔开
     */
    @Column(name = "app_packages")
    private String appPackages;

    /**
     * 用户激活日总数
     */
    @Column(name = "day_count")
    private Integer dayCount;

    /**
     * 基准贡献率
     */
    @Column(name = "base_percentage")
    private Double basePercentage;

    /**
     * 轮播时间
     */
    @Column(name = "rotation_time")
    private Integer rotationTime;

    /**
     * 1左图右边文，2纯图
     */
    @Column(name = "pop_type")
    private Integer popType;

    /**
     * 状态 -1禁用，1启用
     */
    private Integer state;

    /**
     * 屏蔽版本
     */
    @Column(name = "hide_version")
    private String hideVersion;

    /**
     * 屏蔽渠道
     */
    @Column(name = "hide_channel")
    private String hideChannel;

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