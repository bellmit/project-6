package com.miguan.reportview.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhongli
 * @date 2020-08-05 
 * 这个类有点特殊，不要随便添加ApiModelProperty，要添加的话，需要指定position属性，且不能有重复
 */
@Setter
@Getter
@ApiModel
public class ChannelDataDto {
    @ApiModelProperty(value = "新增用户", position = 1)
    @Excel(name = "新增用户", orderNum = "5")
    private double newUser;
    @ApiModelProperty(value = "注册用户", position = 2)
    @Excel(name = "注册用户", orderNum = "6")
    private double regUser;
    @ApiModelProperty(value = "有效行为率", notes = "%",position = 3)
    @Excel(name = "有效行为率", orderNum = "7")
    private double vbRate;
    @ApiModelProperty(value = "首页浏览率", notes = "%",position = 4)
    @Excel(name = "首页浏览率", orderNum = "8")
    private double indexshowRate;
    @ApiModelProperty(value = "播放转化率", notes = "%",position = 5)
    @Excel(name = "播放转化率", orderNum = "9")
    private double uplayRate;
    @ApiModelProperty(value = "人均播放数",position = 6)
    @Excel(name = "人均播放数", orderNum = "10")
    private double perplayNum;
    @ApiModelProperty(value = "有效播放率", notes = "%",position = 7)
    @Excel(name = "有效播放率", orderNum = "11")
    private double vuplayRate;
    @ApiModelProperty(value = "人均有效播放数",position = 8)
    @Excel(name = "人均有效播放数", orderNum = "12")
    private double vperplayNum;
    @ApiModelProperty(value = "人均播放时长",position = 9)
    @Excel(name = "人均播放时长", orderNum = "13")
    private double perplayTime;
    @ApiModelProperty(value = "有效播放视频率", notes = "%",position = 10)
    @Excel(name = "有效播放视频率", orderNum = "14")
    private double vplayRate;
    @ApiModelProperty(value = "广告点击转化率", notes = "%", position = 11)
    @Excel(name = "广告点击转化率", orderNum = "15")
    private double adclickRate;
    @ApiModelProperty(value = "人均广告点击次数",position = 12)
    @Excel(name = "人均广告点击次数", orderNum = "16")
    private double adclickNum;
    @ApiModelProperty(value = "新用户留存",position = 13)
    @Excel(name = "新用户留存", orderNum = "17")
    private double newUserKeepRate;

    @ApiModelProperty(value = "新增用户（友盟）", position = 14)
    @Excel(name = "新增用户（友盟）", orderNum = "18")
    private double newUserUmeng;

    @ApiModelProperty(value = "新增用户中存量用户",position = 15)
    @Excel(name = "新增用户中存量用户", orderNum = "19")
    private double newStockUser;


    @Excel(name = "日期")
    private String date;
    @Excel(name = "应用", orderNum = "1")
    private String packageName;
    @Excel(name = "版本号", orderNum = "2")
    private String appVersion;
    @Excel(name = "是否新用户", orderNum = "3")
    private String isNew;
    @Excel(name = "渠道", orderNum = "4")
    private String channel;
    @Excel(name = "父渠道", orderNum = "4")
    private String fatherChannel;
}
