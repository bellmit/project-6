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
public class AdBehaviorDto {
    @ApiModelProperty(value = "广告请求量", position = 1)
    @Excel(name = "广告请求量", orderNum = "7")
    private double reqNum;
    @ApiModelProperty(value = "广告返回量", position = 2)
    @Excel(name = "广告返回量", orderNum = "8")
    private double resNum;
    @ApiModelProperty(value = "广告填充率", notes = "%", position = 3)
    @Excel(name = "广告填充率", orderNum = "9")
    private double fillRate;
    @ApiModelProperty(value = "广告展现量", position = 4)
    @Excel(name = "广告展现量", orderNum = "10")
    private double showNum;
    @ApiModelProperty(value = "广告展示率", notes = "%", position = 5)
    @Excel(name = "广告展示率", orderNum = "11")
    private double showRate;
    @ApiModelProperty(value = "广告点击量", position = 6)
    @Excel(name = "广告点击量", orderNum = "12")
    private double clickNum;
    @ApiModelProperty(value = "广告点击率", notes = "%", position = 7)
    @Excel(name = "广告点击率", orderNum = "13")
    private double clickRate;
    @ApiModelProperty(value = "展现用户数", position = 8)
    @Excel(name = "展现用户数", orderNum = "14")
    private double showUser;
    @ApiModelProperty(value = "人均广告展示", position = 9)
    @Excel(name = "人均广告展示", orderNum = "15")
    private double perShowNum;
    @ApiModelProperty(value = "人均广告点击", position = 10)
    @Excel(name = "人均广告点击", orderNum = "16")
    private double perClickNum;
    @ApiModelProperty(value = "广告点击转化率", notes = "%", position = 11)
    @Excel(name = "广告点击转化率", orderNum = "17")
    private double uclickRate;
    @ApiModelProperty(value = "营收", position = 12)
    @Excel(name = "营收", orderNum = "18")
    private double revenue;
    @ApiModelProperty(value = "CPM", position = 13)
    @Excel(name = "CPM", orderNum = "19")
    private double cpm;
    @ApiModelProperty(value = "库存", position = 14)
    @Excel(name = "库存", orderNum = "20")
    private double stock;
    @ApiModelProperty(value = "自监测点击量", position = 15)
    @Excel(name = "自监测点击量", orderNum = "21")
    private double vclick;
    @ApiModelProperty(value = "自监测曝光量", position = 16)
    @Excel(name = "自监测曝光量", orderNum = "22")
    private double vshow;

    @Excel(name = "日期", orderNum = "0")
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

}
