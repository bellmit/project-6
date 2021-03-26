package com.miguan.report.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author zhangbinglin
 * @Date 2020/6/19 16:39
 **/
@Data
@ApiModel("营收柱状图DTO")
public class RevMidBarDto {

    @ApiModelProperty("平台名称")
    private String name;

    @ApiModelProperty("应用名称")
    private String stack;

    @ApiModelProperty("具体数据")
    private List<RevBarDataDto> data;
}
