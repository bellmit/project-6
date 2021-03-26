package com.miguan.report.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**概览-- 总成本
 * @author zhongli
 * @date 2020-06-22 
 *
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("拆线图返回数据报文格式")
public class CostAndMomYoyDto implements Serializable {
    @ApiModelProperty("日期")
    private String date;
    @ApiModelProperty("星期几")
    private int dayOfWeek;
    @ApiModelProperty("此日期数值")
    private double valueOfDay;
    @ApiModelProperty("环比")
    private double mom;
    @ApiModelProperty("同比")
    private double yoy;
    @ApiModelProperty("合计")
    private double totalValue;
    @ApiModelProperty("均值")
    private double aveValue;
    @ApiModelProperty("拆线图图表值")
    private List<DisassemblyChartDto> chartData;
}
