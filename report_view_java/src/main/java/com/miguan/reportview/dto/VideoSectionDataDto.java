package com.miguan.reportview.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 视频明细区间汇总dto
 */
@Setter
@Getter
@ApiModel
public class VideoSectionDataDto {

    public VideoSectionDataDto() {
        this.dd = "";
        this.section = "";
        this.allVideoCount = 0L;
        this.allShowNum = 0L;
        this.allPlayNum = 0L;
        this.allPlayRate = 0;
        this.allPlayTimeR = 0;
        this.allPrePlayTimeR = 0;
        this.videoCount = 0L;
        this.showNum = 0L;
        this.playNum = 0L;
        this.playRate = 0;
        this.playTimeR = 0;
        this.prePlayTimeR = 0;
    }

    @ApiModelProperty(value = "日期")
    @Excel(name = "日期" , orderNum = "1")
    private String dd;

    @ApiModelProperty(value = "区间")
    @Excel(name = "区间", orderNum = "2")
    private String section;

    @ApiModelProperty(value = "全部-视频个数")
    @Excel(name = "全部-视频个数", orderNum = "3")
    private Long allVideoCount;

    @ApiModelProperty(value = "全部-曝光次数")
    @Excel(name = "全部-曝光次数", orderNum = "4")
    private Long allShowNum;

    @ApiModelProperty(value = "全部-播放次数")
    @Excel(name = "全部-播放次数", orderNum = "5")
    private Long allPlayNum;

    @ApiModelProperty(value = "全部-播放率", notes = "%")
    @Excel(name = "全部-播放率(%)", orderNum = "6")
    private double allPlayRate;

    @ApiModelProperty(value = "全部-播放时长")
    @Excel(name = "全部-播放时长", orderNum = "7")
    private double allPlayTimeR;

    @ApiModelProperty(value = "全部-平均播放时长")
    @Excel(name = "全部-平均播放时长", orderNum = "8")
    private double allPrePlayTimeR;


    @ApiModelProperty(value = "当天-视频个数")
    @Excel(name = "当天-视频个数", orderNum = "10")
    private Long videoCount;

    @ApiModelProperty(value = "当天-曝光次数")
    @Excel(name = "当天-曝光次数", orderNum = "11")
    private Long showNum;

    @ApiModelProperty(value = "当天-播放次数")
    @Excel(name = "当天-播放次数", orderNum = "12")
    private Long playNum;

    @ApiModelProperty(value = "当天-播放率", notes = "%")
    @Excel(name = "当天-播放率(%)", orderNum = "13")
    private double playRate;

    @ApiModelProperty(value = "当天-播放时长")
    @Excel(name = "当天-播放时长", orderNum = "14")
    private double playTimeR;

    @ApiModelProperty(value = "当天-平均播放时长")
    @Excel(name = "当天-平均播放时长", orderNum = "15")
    private double prePlayTimeR;

}
