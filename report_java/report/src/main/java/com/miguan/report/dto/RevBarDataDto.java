package com.miguan.report.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description TODO
 * @Author zhangbinglin
 * @Date 2020/6/19 16:58
 **/
@Data
@ApiModel("营收柱状图数据DTO")
public class RevBarDataDto {

    @ApiModelProperty("应用名称")
    private String name;

    @ApiModelProperty("占比")
    private String zb;

    @ApiModelProperty("收益")
    private Double value;
}
