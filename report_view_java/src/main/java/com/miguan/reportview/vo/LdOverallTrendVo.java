package com.miguan.reportview.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class LdOverallTrendVo {
    @ApiModelProperty(value="新增用户", position = 1)
    @Excel(name = "新增用户", orderNum = "1")
    private Integer newUser;

    @ApiModelProperty(value="活跃用户", position = 2)
    @Excel(name = "活跃用户", orderNum = "2")
    private Integer user;

    @ApiModelProperty(value="详情页播放用户", position = 3)
    @Excel(name = "详情页播放用户", orderNum = "3")
    private Integer detailPlayUser;

    @ApiModelProperty(value="设置用户", position = 4)
    @Excel(name = "设置用户", orderNum = "4")
    private Integer setUser;

    @ApiModelProperty(value="成功设置用户", position = 5)
    @Excel(name = "成功设置用户", orderNum = "5")
    private Integer setConfirmUser;

    @ApiModelProperty(value="人均观看次数", position = 6)
    @Excel(name = "人均观看次数", orderNum = "6")
    private double prePlayCount;

    @ApiModelProperty(value="人均设置次数", position = 7)
    @Excel(name = "人均设置次数", orderNum = "7")
    private double preSetCount;

    @ApiModelProperty(value="人均成功设置次数", position = 8)
    @Excel(name = "人均成功设置次数", orderNum = "8")
    private double presetConfirmCount;

    @ApiModelProperty(value="广告展现用户", position = 9)
    @Excel(name = "广告展现用户", orderNum = "9")
    private Integer adShowUser;

    @ApiModelProperty(value="广告点击用户", position = 10)
    @Excel(name = "广告点击用户", orderNum = "10")
    private Integer adClickUser;

    @ApiModelProperty(value="广告展现量", position = 11)
    @Excel(name = "广告展现量", orderNum = "11")
    private Integer adShow;

    @ApiModelProperty(value="广告点击量", position = 12)
    @Excel(name = "广告点击量", orderNum = "12")
    private Integer adClick;

    @ApiModelProperty(value="人均广告展现", position = 13)
    @Excel(name = "人均广告展现", orderNum = "13")
    private double preAdShow;

    @ApiModelProperty(value="人均广告点击", position = 14)
    @Excel(name = "人均广告点击", orderNum = "14")
    private double preAdClick;

    @ApiModelProperty(value="广告点击率", position = 15)
    @Excel(name = "广告点击率", orderNum = "15")
    private double adClickRate;

    @ApiModelProperty(value="广告点击用户占比", position = 16)
    @Excel(name = "广告点击用户占比", orderNum = "16")
    private double adClickShowRate;

    @ApiModelProperty(value="人均APP启动次数", position = 17)
    @Excel(name = "人均APP启动次数", orderNum = "17")
    private double preAppStart;

    @Excel(name = "日期")
    private String date;
}
