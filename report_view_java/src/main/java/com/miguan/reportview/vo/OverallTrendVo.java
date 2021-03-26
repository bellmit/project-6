package com.miguan.reportview.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zhongli
 * @date 2020-08-04 
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class OverallTrendVo {
    @ApiModelProperty(value = "新增用户", position = 1)
    @Excel(name = "新增用户", orderNum = "1")
    private Double newUser;
    @ApiModelProperty(value = "活跃用户", position = 2)
    @Excel(name = "活跃用户", orderNum = "2")
    private Double user;
    @ApiModelProperty(value = "视频播放数",position = 3)
    @Excel(name = "视频播放数", orderNum = "3")
    private Double playCount;
    @ApiModelProperty(value = "日活用户价值",position = 4)
    @Excel(name = "日活用户价值", orderNum = "4", isColumnHidden = true)
    private Double duv;
    @ApiModelProperty(value = "日活客单成本",position = 5)
    @Excel(name = "日活客单成本", orderNum = "5", isColumnHidden = true)
    private Double duc;
    @ApiModelProperty(value = "新用户次留", notes = "%",position = 6)
    @Excel(name = "新用户次留", orderNum = "6")
    private Double nukeep;
    @ApiModelProperty(value = "活跃用户次留", notes = "%",position = 7)
    @Excel(name = "活跃用户次留", orderNum = "7")
    private Double dukeep;
    @ApiModelProperty(value = "人均有效播放数",position = 8)
    @Excel(name = "人均有效播放数", orderNum = "8")
    private Double duvpc;
    @ApiModelProperty(value = "广告展现量",position = 9)
    @Excel(name = "广告展现量", orderNum = "9")
    private Double adShowCount;
    @ApiModelProperty(value = "广告点击量",position = 10)
    @Excel(name = "广告点击量", orderNum = "10")
    private Double adClickCount;
    @ApiModelProperty(value = "广告点击率", notes = "%",position = 11)
    @Excel(name = "广告点击率", orderNum = "11")
    private Double adClickRate;
    @ApiModelProperty(value = "人均广告展示",position = 12)
    @Excel(name = "人均广告展示", orderNum = "12")
    private Double duAdshow;
    @ApiModelProperty(value = "人均广告点击",position = 13)
    @Excel(name = "人均广告点击", orderNum = "13")
    private Double duAdClick;
    @ApiModelProperty(value = "平均日使用时长",position = 14)
    @Excel(name = "平均日使用时长", orderNum = "14", isColumnHidden = true)
    private Double duAppTime;
    @ApiModelProperty(value = "人均播放时长",position = 15)
    @Excel(name = "人均播放时长", orderNum = "15")
    private Double perPlayTime;

    @ApiModelProperty(value="每曝光播放时长",position = 16)
    @Excel(name = "每曝光播放时长", orderNum = "16")
    private Double preShowTime;

    @ApiModelProperty(value="每播放播放时长",position = 17)
    @Excel(name = "每播放播放时长", orderNum = "17")
    private Double prePlayTime;

    @Excel(name = "日期")
    private String date;

    public Double getNukeep() {
        if(nukeep == null) {
            nukeep = 0D;
        }
        return nukeep;
    }

    public Double getDukeep() {
        if(dukeep == null) {
            dukeep = 0D;
        }
        return dukeep;
    }
}
