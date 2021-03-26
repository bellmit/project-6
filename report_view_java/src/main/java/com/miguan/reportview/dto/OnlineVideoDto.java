package com.miguan.reportview.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 线上短视频dto
 */
@Setter
@Getter
@ApiModel
public class OnlineVideoDto {

    @ApiModelProperty(value = "视频ID", position = 10)
    private Long videoId;

    @ApiModelProperty(value = "内容源", position = 20)
    private String videosSource;

    @ApiModelProperty(value = "视频标题", position = 30)
    private String title;

    @ApiModelProperty(value = "分类", position = 40)
    private Long catId;

    @ApiModelProperty(value = "分类名称", position = 41)
    private String catName;

    @ApiModelProperty(value = "视频内容", position = 50)
    private String bsyUrl;

    @ApiModelProperty(value = "视频封面", position = 60)
    private String bsyImgUrl;

    @ApiModelProperty(value = "视频播放时长", position = 61)
    private String videoTime;

    @ApiModelProperty(value = "曝光数", position = 70)
    private Long showCount;

    @ApiModelProperty(value = "分类平均播放率", position = 80)
    private Double catPlayRate;

    @ApiModelProperty(value = "分类平均有效播放率", position = 90)
    private Double catVplayRate;

    @ApiModelProperty(value = "分类平均完播率", position = 100)
    private Double catAllPlayRate;

    @ApiModelProperty(value = "播放数", position = 110)
    private Long playCount;

    @ApiModelProperty(value = "播放率", position = 120)
    private Double playRate;

    @ApiModelProperty(value = "有效播放次数", position = 130)
    private Long vplayCount;

    @ApiModelProperty(value = "有效播放率", position = 140)
    private Double vplayRate;

    @ApiModelProperty(value = "完整播放数", position = 150)
    private Long allPlayCount;

    @ApiModelProperty(value = "完整播放率", position = 160)
    private Double allPlayRate;

    @ApiModelProperty(value = "平均每曝光播放时长", position = 170)
    private Double preShowTime;

    @ApiModelProperty(value = "上线时间", position = 180)
    private String onlineTime;

    @ApiModelProperty(value = "最新修改时间", position = 190)
    private String updatedAt;

    @ApiModelProperty(value = "敏感词 -1非敏感词，1:1级敏感词", position = 200)
    private Integer sensitive;
}
