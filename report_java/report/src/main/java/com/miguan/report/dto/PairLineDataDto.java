package com.miguan.report.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author zhangbinglin
 * @Date 2020/6/17 11:52
 **/
@Data
@ApiModel("双Y轴的折线图表DTO")
public class PairLineDataDto {

    @ApiModelProperty("时间集合")
    private String name;

    @ApiModelProperty("轴数")
    private Integer yAxisIndex;

    @ApiModelProperty("数值")
    private List<Double> data;

}
