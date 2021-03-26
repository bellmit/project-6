package com.miguan.ballvideo.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 报表表格vo
 * @Author zhangbinglin
 * @Date 2020/11/24 13:54
 **/
@Data
public class ReportTableVo {

    @ApiModelProperty("日期")
    @Excel(name = "日期", orderNum = "10")
    private String date;

    @ApiModelProperty("名称")
    @Excel(name = "名称", orderNum = "20")
    private String name;

    @ApiModelProperty("出价")
    @Excel(name = "出价", orderNum = "30")
    private Double price;

    @ApiModelProperty("曝光量")
    @Excel(name = "曝光量", orderNum = "40")
    private Integer exposure;

    @ApiModelProperty("点击量")
    @Excel(name = "点击量", orderNum = "50")
    private Integer validClick;

    @ApiModelProperty("点击用户数")
    @Excel(name = "点击用户数", orderNum = "55")
    private Integer clickUser;

    @ApiModelProperty("点击率(百分比，已乘了100)")
    @Excel(name = "点击率(%)", orderNum = "60")
    private Double preClickRate;

    @ApiModelProperty("点击均价")
    @Excel(name = "点击均价", orderNum = "70")
    private Double preClickPrice;

    @ApiModelProperty("花费")
    @Excel(name = "花费", orderNum = "80")
    private Double consume;

    @ApiModelProperty("千次展示均价")
    @Excel(name = "千次展示均价", orderNum = "90")
    private Double preEcpm;
}
