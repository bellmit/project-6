package com.miguan.report.dto;

import com.miguan.expression.util.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @Author zhangbinglin
 * @Date 2020/6/17 11:52
 **/
@Data
@ApiModel("双Y轴的折线图表DTO")
public class PairLineDto {

    @ApiModelProperty("时间集合")
    private LinkedHashSet<String> dates;

    @ApiModelProperty("折线图数据")
    private List<PairLineDataDto> lineDatas;

}
