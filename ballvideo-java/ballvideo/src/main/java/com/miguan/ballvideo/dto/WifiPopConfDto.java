package com.miguan.ballvideo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description wifi首页弹框dto
 * @Author suhj
 * @Date 2020/08/10 11:41
 **/
@Data
@ApiModel("wifi首页弹框dto")
public class WifiPopConfDto {

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
