package com.miguan.ballvideo.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description 首页弹窗配置
 * @Author zhangbinglin
 * @Date 2020/8/5 10:08
 **/
@Data
@Entity
@Table(name = "pop_conf")
public class PopConf {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "状态 -1禁用，1启用")
    private int state;
    private int createTime;
    private int updateTime;

    @ApiModelProperty(value = "app包名，逗号隔开")
    private String appPackage;

    @ApiModelProperty(value = "渠道号，逗号隔开")
    private String channelId;

    @ApiModelProperty(value = "作用版本范围1")
    private String version1;

    @ApiModelProperty(value = "作用版本范围2")
    private String version2;

    @ApiModelProperty(value = "弹窗位置：1--首页弹窗，2--首页悬浮窗")
    private int popPosition;

    @ApiModelProperty(value = "弹窗标题")
    private String popTitle;

    @ApiModelProperty(value = "跳转链接")
    private String link;

    @ApiModelProperty(value = "图片链接")
    private String img;

    @ApiModelProperty(value = "出现时机：1每天出现1次。2期间仅出现1次。3每次重新进入出现1次。")
    private int timing;

    @ApiModelProperty(value = "上架时间")
    private Date sdate;

    @ApiModelProperty(value = "下架时间")
    private Date edate;

    @ApiModelProperty(value = "轮播时间")
    private String rotationTime;

    @ApiModelProperty(value = "弹窗类型：1--左图右边文，2--纯图")
    private String popType;

    @ApiModelProperty(value = "配置详情 json格式。jump_type ：1--h5,2--短视频，3--小视频，4--启动；apptitle：标题；jump_link：跳转连接；img：图片")
    private String confData;


}
