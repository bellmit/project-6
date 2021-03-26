package com.miguan.reportview.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author linliqing
 * @date 2020-10-10
 * 渠道ROI评估数据
 */
@Setter
@Getter
@ApiModel
public class ChannelRoiPrognosisDto {

    @ApiModelProperty(value = "父渠道")
    @Excel(name = "父渠道")
    private String fatherChannel;

    @ApiModelProperty(value = "最近几天")
    @Excel(name = "最近几天")
    private int dayNum;

    @ApiModelProperty(value = "获客成本")
    @Excel(name = "获客成本")
    private double customerCost;

    @ApiModelProperty(value = "回本周期")
    @Excel(name = "回本周期")
    private int packCycle;

    @ApiModelProperty(value = "边际roi")
    @Excel(name = "边际roi")
    private double marginalRoi;

    @ApiModelProperty(value = "投放金额上限")
    @Excel(name = "投放金额上限")
    private double amountLimit;

    @ApiModelProperty(value = "新增用户数")
    @Excel(name = "新增用户数")
    private int newUserNum;

}
