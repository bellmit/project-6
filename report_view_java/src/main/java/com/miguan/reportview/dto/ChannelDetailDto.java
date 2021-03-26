package com.miguan.reportview.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 子渠道明细dto
 */
@Setter
@Getter
@ApiModel
public class ChannelDetailDto {

    @ApiModelProperty(value = "日期")
    @Excel(name = "日期")
    private String dd;

    @ApiModelProperty(value = "设备id")
    @Excel(name = "设备id")
    private String distinctId;

    @ApiModelProperty(value = "imei")
    @Excel(name = "imei")
    private String imei;

    @ApiModelProperty(value = "手机型号")
    @Excel(name = "手机型号")
    private String model;

    @ApiModelProperty(value = "应用")
    @Excel(name = "应用")
    private String packageName;

    @ApiModelProperty(value = "版本")
    @Excel(name = "版本")
    private String appVersion;

    @ApiModelProperty(value = "子渠道")
    @Excel(name = "子渠道")
    private String channel;

    @ApiModelProperty(value = "更新渠道")
    @Excel(name = "更新渠道")
    private String changeChannel;

    @ApiModelProperty(value = "播放次数")
    @Excel(name = "播放次数")
    private Long playCount;

    @ApiModelProperty(value = "有效播放次数")
    @Excel(name = "有效播放次数")
    private Long vplayCount;

    @ApiModelProperty(value = "播放时长")
    @Excel(name = "播放时长")
    private Double playTimeReal;

    @ApiModelProperty(value = "广告展示数")
    @Excel(name = "广告展示数")
    private Long vadShowCount;

    @ApiModelProperty(value = "广告点击数")
    @Excel(name = "广告点击数")
    private Long vadClickCount;
}
