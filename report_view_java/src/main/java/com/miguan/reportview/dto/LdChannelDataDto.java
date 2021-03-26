package com.miguan.reportview.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 来电渠道数据
 */
@Setter
@Getter
@ApiModel
public class LdChannelDataDto {
    @ApiModelProperty(value = "新增用户", position = 1)
    @Excel(name = "新增用户", orderNum = "5")
    private Integer newUser;

    @ApiModelProperty(value = "注册用户", position = 2)
    @Excel(name = "注册用户", orderNum = "6")
    private Integer registerUser;

    @ApiModelProperty(value = "首页浏览率", position = 3)
    @Excel(name = "首页浏览率", orderNum = "7")
    private Double homePageBrowseRate;

    @ApiModelProperty(value = "设置转化率", position = 4)
    @Excel(name = "用户转化率", orderNum = "8")
    private Double userConversionRate;

    @ApiModelProperty(value = "设置转化率", position = 5)
    @Excel(name = "设置转化率", orderNum = "9")
    private Double setConversionRate;

    @ApiModelProperty(value = "来电设置成功率", position = 6)
    @Excel(name = "来电设置成功率", orderNum = "10")
    private Double setPhoneRate;

    @ApiModelProperty(value = "壁纸设置成功率", position = 7)
    @Excel(name = "壁纸设置成功率", orderNum = "11")
    private Double setWallpaperRate;

    @ApiModelProperty(value = "锁屏设置成功率", position = 8)
    @Excel(name = "锁屏设置成功率", orderNum = "12")
    private Double setLockScreenRate;

    @ApiModelProperty(value = "QQ/微信皮肤设置成功率", position = 9)
    @Excel(name = "QQ/微信皮肤设置成功率", orderNum = "13")
    private Double setSkinRate;

    @ApiModelProperty(value = "铃声设置成功率", position = 10)
    @Excel(name = "铃声设置成功率", orderNum = "14")
    private Double setRingConfirmRate;

    @ApiModelProperty(value = "广告点击转化率", position = 11)
    @Excel(name = "广告点击转化率", orderNum = "15")
    private Double adClickRate;

    @ApiModelProperty(value = "人均广告点击次数", position = 12)
    @Excel(name = "人均广告点击次数", orderNum = "16")
    private Double preAdClick;

    @ApiModelProperty(value = "新用户留存",position = 13)
    @Excel(name = "新用户留存", orderNum = "17")
    private double newUserKeepRate;


    @ApiModelProperty(value = "新增用户（友盟）", position = 14)
    @Excel(name = "新增用户（友盟）", orderNum = "18")
    private double newUserUmeng;

    @ApiModelProperty(value = "新增用户中存量用户",position = 15)
    @Excel(name = "新用户留存", orderNum = "19")
    private double newStockUser;



    @Excel(name = "日期")
    private String date;

    @Excel(name = "版本号", orderNum = "1")
    private String appVersion;

    @Excel(name = "是否新用户", orderNum = "2")
    private String isNewApp;

    @Excel(name = "父渠道", orderNum = "3")
    private String fatherChannel;

    @Excel(name = "渠道", orderNum = "4")
    private String channel;
}
