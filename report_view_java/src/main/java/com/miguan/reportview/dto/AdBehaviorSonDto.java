package com.miguan.reportview.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 子广告位分析
 */
@Setter
@Getter
@ApiModel
public class AdBehaviorSonDto {
    @ApiModelProperty(value = "日期")
    @Excel(name = "日期", orderNum = "0")
    private String date;

    @ApiModelProperty(value = "应用")
    @Excel(name = "应用", orderNum = "2")
    private String packageName;

    @ApiModelProperty(value = "平台")
    @Excel(name = "平台", orderNum = "3")
    private String adSource;

    @ApiModelProperty(value = "广告位")
    @Excel(name = "广告位", orderNum = "4")
    private String adPostion;

    @ApiModelProperty(value = "位置")
    @Excel(name = "位置", orderNum = "5")
    private String qId;

    @ApiModelProperty(value = "渲染方式")
    @Excel(name = "渲染方式", orderNum = "6")
    private String renderType;

    @ApiModelProperty(value = "素材")
    @Excel(name = "素材", orderNum = "7")
    private String adcType;

    @ApiModelProperty(value = "库存")
    @Excel(name = "库存", orderNum = "8")
    private double stock;

    @ApiModelProperty(value = "请求量")
    @Excel(name = "请求量", orderNum = "9")
    private double reqNum;

    @ApiModelProperty(value = "展示量")
    @Excel(name = "展示量", orderNum = "10")
    private double showNum;

    @ApiModelProperty(value = "展示率", notes = "%")
    @Excel(name = "展示率", orderNum = "11")
    private double showRate;

    @ApiModelProperty(value = "有效曝光")
    @Excel(name = "有效曝光", orderNum = "12")
    private Integer validShowNum;

    @ApiModelProperty(value = "有效点击")
    @Excel(name = "有效点击", orderNum = "13")
    private Integer validClickNum;

    @ApiModelProperty(value = "点击量")
    @Excel(name = "点击量", orderNum = "14")
    private double clickNum;

    @ApiModelProperty(value = "点击率", notes = "%")
    @Excel(name = "点击率", orderNum = "15")
    private double clickRate;

    @ApiModelProperty(value = "展现用户数")
    @Excel(name = "展现用户数", orderNum = "16")
    private double showUser;

    @ApiModelProperty(value = "展现用户数占比")
    @Excel(name = "展现用户数占比", orderNum = "17")
    private double showUserRate;

    @ApiModelProperty(value = "人均展现量")
    @Excel(name = "人均展现量", orderNum = "18")
    private double pershowUserR;

    public void setqId(String qId) {
        qId = qId.replace("BETWEEN", "").replace("AND","-").replace(" ", "");
        this.qId = qId;
    }
}
