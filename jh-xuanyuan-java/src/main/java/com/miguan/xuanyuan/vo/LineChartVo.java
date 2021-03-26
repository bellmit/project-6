package com.miguan.xuanyuan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author zhangbinglin
 **/
@Data
@ApiModel("折线图表VO")
public class LineChartVo {

    @ApiModelProperty("日期(x轴的值)")
    private String date;

    @ApiModelProperty("类型（折线图的名称，如：应用名称，或者平台名称）")
    private String type;

    @ApiModelProperty("数值（y轴的值）")
    private Double value;
}
