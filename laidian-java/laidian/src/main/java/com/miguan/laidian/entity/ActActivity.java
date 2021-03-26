package com.miguan.laidian.entity;

import io.swagger.annotations.*;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Entity(name = "act_activity")
@ApiModel("活动表")
public class ActActivity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty(value = "活动名称")
    @Column(name = "name")
    private String name;

    @ApiModelProperty(value = "活动开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "start_time")
    private Date startTime;

    @ApiModelProperty(value = "活动结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "end_time")
    private Date endTime;

    @ApiModelProperty(value = "首页弹窗是否启用标识 0禁用 1开启")
    @Column(name = "pop_up_flag")
    private Integer popUpFlag;

    @ApiModelProperty(value = "首页悬浮窗是否启用标识 0禁用 1开启")
    @Column(name = "floating_window_flag")
    private Integer floatingWindowFlag;

    @ApiModelProperty(value = "我的banner是否启用标识 0禁用 1开启")
    @Column(name = "banner_flag")
    private Integer bannerFlag;

    @ApiModelProperty(value = "每日免费抽奖次数")
    @Column(name = "day_draw_num")
    private Integer dayDrawNum;

    @ApiModelProperty(value = "任务-成功设置来电秀是否启用启用 0禁用 1开启")
    @Column(name = "ldx_task_flag")
    private Integer ldxTaskFlag;

    @ApiModelProperty(value = "任务-成功设置铃声是否启用启用 0禁用 1开启")
    @Column(name = "ls_task_flag")
    private Integer lsTaskFlag;

    @ApiModelProperty(value = "任务-成功设置铃声是否启用启用 0禁用 1开启")
    @Column(name = "share_task_flag")
    private Integer shareTaskFlag;

    @ApiModelProperty(value = "任务-观看视频是否启用启用 0禁用 1开启")
    @Column(name = "video_task_flag")
    private Integer videoTaskFlag;

    @ApiModelProperty(value = "链接地址")
    @Column(name = "link_url")
    private String linkUrl;

    @ApiModelProperty(value = "活动状态 0禁用 1开启")
    @Column(name = "state")
    private Integer state;

    @ApiModelProperty(value = "参与抽奖次数")
    @Column(name = "draw_num")
    private Integer drawNum;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    private Date createdAt;

    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_at")
    private Date updatedAt;

    @ApiModelProperty(value = "首页弹窗入口图片")
    @Column(name = "home_jump_img")
    private String homeJumpImg;

    @ApiModelProperty(value = "首页悬浮窗图片")
    @Column(name = "home_float_img")
    private String homeFloatImg;

    @ApiModelProperty(value = "我的Banner图片")
    @Column(name = "home_banner_img")
    private String homeBannerImg;
}