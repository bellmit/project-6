package com.miguan.reportview.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author linliqing
 * @date 2020-10-30
 * 渠道成本录入报表数据
 */
@Setter
@Getter
@ApiModel
public class ChannelCostDto {

    @ApiModelProperty(value = "自增id")
    @Excel(name = "自增id")
    private String id;

    @ApiModelProperty(value = "日期")
    @Excel(name = "日期")
    private String date;

    @ApiModelProperty(value = "app类型:1视频，2炫来电")
    @Excel(name = "app类型:1视频，2炫来电")
    private String appType;

    @ApiModelProperty(value = "马甲包")
    @Excel(name = "马甲包")
    private String packageName;

    @ApiModelProperty(value = "父渠道")
    @Excel(name = "父渠道")
    private String fatherChannel;

    @ApiModelProperty(value = "负责人")
    @Excel(name = "负责人")
    private String owner;

    @ApiModelProperty(value = "渠道返点")
    @Excel(name = "渠道返点")
    private double rebate;

    @ApiModelProperty(value = "账面消耗")
    @Excel(name = "账面消耗")
    private double cost;

    @ApiModelProperty(value = "新增用户数（友盟）")
    @Excel(name = "新增用户数（友盟）")
    private int newUserUm;

    @ApiModelProperty(value = "新增用户数")
    @Excel(name = "新增用户数")
    private int pureNewUser;

    @ApiModelProperty(value = "人均广告点击")  //原来是广告点击数，后来改了口径
    @Excel(name = "人均广告点击")
    private double adClickNum;

    @ApiModelProperty(value = "次日留存")
    @Excel(name = "次日留存")
    private double keep1;


    @ApiModelProperty(value = "现金消耗")
    @Excel(name = "现金消耗")
    private double realCost;

    @ApiModelProperty(value = "净新增激活单价")
    @Excel(name = "净新增激活单价")
    private double pureNewUserPrice;

    @ApiModelProperty(value = "友盟激活单价")
    @Excel(name = "友盟激活单价")
    private double newUserUmPrice;

}
