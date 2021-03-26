package com.miguan.reportview.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhongli
 * @date 2020-08-05 
 *
 */
@Setter
@Getter
@ApiModel
public class AdErrorDto {
    @ApiModelProperty(value = "请求总数",position = 1)
    @Excel(name = "请求总数", orderNum = "8")
    private double reqNum;
    @ApiModelProperty(value = "错误数",position = 2)
    @Excel(name = "错误数", orderNum = "9")
    private double errNum;
    @ApiModelProperty(value = "错误类型",position = 7)
    @Excel(name = "错误类型", orderNum = "10")
    private String msg;
    @ApiModelProperty(value = "错误占比", notes = "%",position = 4)
    @Excel(name = "错误占比", orderNum = "11")
    private double errPerc;
    @ApiModelProperty(value = "错误用户数",position = 5)
    @Excel(name = "错误用户数", orderNum = "12")
    private double errUser;
    @ApiModelProperty(value = "错误用户占比", notes = "%",position = 6)
    @Excel(name = "错误用户占比", orderNum = "13")
    private double errUserRate;
    @ApiModelProperty(value = "错误率", notes = "%",position = 3)
    @Excel(name = "错误率", orderNum = "14")
    private double errRate;

    @Excel(name = "日期")
    private String date;
    @Excel(name = "应用", orderNum = "1")
    private String packageName;
    @Excel(name = "版本号", orderNum = "2")
    private String appVersion;
    @Excel(name = "是否新用户", orderNum = "3")
    private String isNew;
    @Excel(name = "父渠道", orderNum = "4")
    private String fatherChannel;
    @Excel(name = "渠道", orderNum = "4")
    private String channel;
    @Excel(name = "平台", orderNum = "4")
    private String flat;
    @Excel(name = "广告位", orderNum = "5")
    private String adSpace;
    @Excel(name = "代码位", orderNum = "6")
    private String adCode;
    @Excel(name = "机型", orderNum = "7")
    private String model;
}
