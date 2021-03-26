package com.miguan.reportview.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author linliqing
 * @date 2020-10-10
 * 渠道ROI预测数据
 */
@Setter
@Getter
@ApiModel
public class ChannelRoiEstimateDto {

    @ApiModelProperty(value = "日期")
    @Excel(name = "日期")
    private String dt;

    @ApiModelProperty(value = "父渠道")
    @Excel(name = "父渠道")
    private String fatherChannel;

    @ApiModelProperty(value = "新增用户数")
    @Excel(name = "新增用户数")
    private int newUsers;

    @ApiModelProperty(value = "实际roi")
    @Excel(name = "实际roi")
    private double actualRoi;

    @ApiModelProperty(value = "预测roi")
    @Excel(name = "预测roi")
    private double predictedRoi;

    @ApiModelProperty(value = "当天roi")
    @Excel(name = "当天roi")
    private double todayRoi;

    @ApiModelProperty(value = "当天roi均值")
    @Excel(name = "当天roi均值")
    private double avgTodayRoi;

    @ApiModelProperty(value = "当前roi均值")
    @Excel(name = "当前roi均值")
    private double avgActualRoi;

    @ApiModelProperty(value = "7天roi")
    @Excel(name = "7天roi")
    private double sevenRoi;

    @ApiModelProperty(value = "7天roi均值")
    @Excel(name = "7天roi")
    private double avgSevenRoi;



}
