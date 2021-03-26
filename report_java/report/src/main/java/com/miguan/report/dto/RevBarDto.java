package com.miguan.report.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * @Author zhangbinglin
 * @Date 2020/6/19 16:58
 **/
@Data
@ApiModel("营收柱状图数据DTO")
public class RevBarDto {

    @ApiModelProperty("时间集合")
    private LinkedHashSet<String> dates;

    @ApiModelProperty("柱状图数据")
    private List<RevMidBarDto> barDates;
}
