package com.miguan.xuanyuan.vo;

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
public class PairLineVo {

    @ApiModelProperty(value="左Y轴数据值", position=10)
    private List<LineChartVo> left;

    @ApiModelProperty(value="左Y轴数据值", position=20)
    private List<LineChartVo> right;


}
