package com.miguan.ballvideo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description TODO
 * @Author zhangbinglin
 * @Date 2020/8/5 11:41
 **/
@Data
@ApiModel("首页弹框dto")
public class PopConfDto {

    @ApiModelProperty(value = "弹窗位置：1--首页弹窗，2--首页悬浮窗")
    private int popPosition;

    @ApiModelProperty(value = "弹窗标题")
    private String popTitle;

    @ApiModelProperty(value = "跳转链接")
    private String link;

    @ApiModelProperty(value = "图片链接")
    private String img;
}
