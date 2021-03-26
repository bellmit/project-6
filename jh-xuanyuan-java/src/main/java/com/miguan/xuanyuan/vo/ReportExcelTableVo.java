package com.miguan.xuanyuan.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 报表表格vo
 * @Author zhangbinglin
 * @Date 2020/11/24 13:54
 **/
@Data
public class ReportExcelTableVo {

    @ApiModelProperty("日期")
    @Excel(name = "日期", orderNum = "10")
    private String date;

    @ApiModelProperty("名称")
    @Excel(name = "名称", orderNum = "20")
    private String name;

    @ApiModelProperty("曝光量")
    @Excel(name = "曝光量", orderNum = "30")
    private Integer showNum;

    @ApiModelProperty("曝光用户数")
    @Excel(name = "曝光用户数", orderNum = "40")
    private Integer showUser;

    @ApiModelProperty("点击量")
    @Excel(name = "点击量", orderNum = "50")
    private Integer clickNum;

    @ApiModelProperty("点击用户数")
    @Excel(name = "点击用户数", orderNum = "60")
    private Integer clickUser;

    @ApiModelProperty("点击率(百分比，已乘了100)")
    @Excel(name = "点击率(%)", orderNum = "70")
    private Double clickRate;
}
