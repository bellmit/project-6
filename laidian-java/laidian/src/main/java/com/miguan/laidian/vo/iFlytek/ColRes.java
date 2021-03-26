package com.miguan.laidian.vo.iFlytek;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * Created by 98du on 2020/8/13.
 */
@Data
@ApiModel("栏目资源信息")
public class ColRes implements Serializable{

    @ApiModelProperty("栏目编号")
    private String id;

    @ApiModelProperty("栏目名称")
    private String name;

    @ApiModelProperty("描述")
    private String desc;

    @ApiModelProperty("介绍")
    private String intro;


    /**
     * 20010001	栏目（存放20020002、20020003、20030001、20040001、及同类型资源20010001）
     20020002	无图合辑（targetid为对应铃音包（20020001）的编号）
     20020003	图片合辑（targetid为对应铃音包（20020001）的编号）
     20030001	轮播图，banner列表（存放20030002、20030003）
     20030002	广告之url（linkurl为该广告指向的链接地址）
     20030003	广告之合辑（targetid为对应合辑（20020002、20020003）的编号）
     20040001	分类（存放20020002、20020003）
     21020002	视频无图合辑（targetid为对应视频包（21020001）的编号）
     21020003	视频图片合辑（targetid为对应视频包（21020001）的编号）
     21030003	广告之视频合辑（targetid为对应视频合辑（21020002、21020003）的编号）
     21040001	视频分类（存放视频合辑（21020002、21020003））
     详细说明请见附件《栏目类型使用补充说明》
     */
    @ApiModelProperty("栏目类型")
    private String type;

    @ApiModelProperty("封面图片，例如：轮播图的图片路径")
    private String simg;

    @ApiModelProperty("详情图片，例如：轮播图二级页面的头部图片路径")
    private String detimg;

    @ApiModelProperty("栏目ID")
    private String targetid;

    @ApiModelProperty("子栏目列表")
    List<ColRes> cols;

    @ApiModelProperty("铃音列表")
    List<ResItemSimple> wks;

    @ApiModelProperty("视频列表")
    List<VideoRing> wksVr;

    @ApiModelProperty("轮播图")
    ColRes lunbo;

    @ApiModelProperty("分类")
    ColRes fenlei;

    @ApiModelProperty("最新")
    ColRes zuixin;

    @ApiModelProperty("最热")
    ColRes zuire;
}
