package com.miguan.reportview.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 视频明细dto
 */
@Setter
@Getter
@ApiModel
public class UserContentDetailDto {

    @ApiModelProperty(value = "日期", position = 10)
    @Excel(name = "日期" , orderNum = "10")
    private String dd;

    @ApiModelProperty(value = "上线日期", position = 20)
    @Excel(name = "上线日期" , orderNum = "20")
    private String onlineDate;

    @ApiModelProperty(value = "进文日期", position = 30)
    @Excel(name = "进文日期" , orderNum = "30")
    private String jwDate;

    @ApiModelProperty(value = "分类", position = 40)
    @Excel(name = "分类" , orderNum = "40")
    private String catName;

    @ApiModelProperty(value = "内容来源", position = 41)
    @Excel(name = "内容来源" , orderNum = "41")
    private String videosSource;

    @ApiModelProperty(value = "视频id", position = 42)
    @Excel(name = "视频id" , orderNum = "42")
    private Long videoId;

    @ApiModelProperty(value = "视频标题", position = 50)
    @Excel(name = "视频标题" , orderNum = "50")
    private String videoTitle;

    @ApiModelProperty(value = "是否激励视频", position = 60)
    @Excel(name = "是否激励视频" , orderNum = "60")
    private String isIncentive;

    @ApiModelProperty(value = "曝光次数", position = 70)
    @Excel(name = "曝光次数", orderNum = "70")
    private Long showNum;

    @ApiModelProperty(value = "曝光区间", position = 80)
    @Excel(name = "曝光区间", orderNum = "80")
    private String section;

    @ApiModelProperty(value = "播放次数", position = 90)
    @Excel(name = "播放次数", orderNum = "90")
    private Long playNum;

    @ApiModelProperty(value = "播放率", notes = "%", position = 100)
    @Excel(name = "播放率", orderNum = "100")
    private double playRate;

    @ApiModelProperty(value = "分类平均播放率", notes = "%", position = 110)
    @Excel(name = "分类平均播放率", orderNum = "110")
    private double catPlayRate;

    @ApiModelProperty(value = "总体平均播放率", notes = "%", position = 120)
    @Excel(name = "总体平均播放率", orderNum = "120")
    private double totalPlayRate;

    @ApiModelProperty(value = "曝光用户数", position = 130)
    @Excel(name = "曝光用户数", orderNum = "130")
    private Long showUser;

    @ApiModelProperty(value = "播放用户数", position = 140)
    @Excel(name = "播放用户数", orderNum = "140")
    private Long playUser;

    @ApiModelProperty(value = "播放时长", position = 150)
    @Excel(name = "播放时长", orderNum = "150")
    private double playTimeR;

    @ApiModelProperty(value = "每次曝光播放时长", position = 160)
    @Excel(name = "每次曝光播放时长", orderNum = "160")
    private double everyPlayTimeR;

    @ApiModelProperty(value = "人均曝光次数", position = 170)
    @Excel(name = "人均曝光次数", orderNum = "170")
    private double perShowNum;

    @ApiModelProperty(value = "人均播放次数", position = 180)
    @Excel(name = "人均播放次数", orderNum = "180")
    private double perPlayNum;

    @ApiModelProperty(value = "有效播放次数", position = 190)
    @Excel(name = "有效播放次数", orderNum = "190")
    private Long vplayNum;

    @ApiModelProperty(value = "有效播放率", position = 191)
    @Excel(name = "有效播放率", orderNum = "191")
    private double vplayRate;

    @ApiModelProperty(value = "有效播放用户", position = 200)
    @Excel(name = "有效播放用户", orderNum = "200")
    private Long vplayUser;

    @ApiModelProperty(value = "完播次数", position = 210)
    @Excel(name = "完播次数", orderNum = "210")
    private Long allPlayNum;

    @ApiModelProperty(value = "完播率", position = 211)
    @Excel(name = "完播率", orderNum = "211")
    private double allPlayRate;

    @ApiModelProperty(value = "完播用户数", position = 220)
    @Excel(name = "完播用户数", orderNum = "220")
    private Long allPlayUser;

    @ApiModelProperty(value = "人均完播次数", position = 230)
    @Excel(name = "人均完播次数", orderNum = "230")
    private double preEndPayCount;

    @ApiModelProperty(value = "点赞数", position = 240)
    @Excel(name = "点赞数", orderNum = "240")
    private Long likeNum;

    @ApiModelProperty(value = "点赞用户数", position = 250)
    @Excel(name = "点赞用户数", orderNum = "250")
    private Long likeUser;

    @ApiModelProperty(value = "分享数", position = 260)
    @Excel(name = "分享数", orderNum = "260")
    private Long shareNum;

    @ApiModelProperty(value = "分享用户数", position = 270)
    @Excel(name = "分享用户数", orderNum = "270")
    private Long shareUser;

    @ApiModelProperty(value = "收藏数", position = 280)
    @Excel(name = "收藏数", orderNum = "280")
    private Long favNum;

    @ApiModelProperty(value = "收藏用户数", position = 290)
    @Excel(name = "收藏用户数", orderNum = "280")
    private Long favUser;

    @ApiModelProperty(value = "分类曝光数", position = 300)
    private Long catShowNum;

    @ApiModelProperty(value = "天曝光数", position = 310)
    private Long ddShowNum;


    @ApiModelProperty(value = "分类播放数", position = 320)
    private Long catPlayNum;

    @ApiModelProperty(value = "天播放数", position = 330)
    private Long ddPlayNum;
}
