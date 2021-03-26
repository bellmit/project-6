package com.miguan.report.dto;

import com.miguan.expression.util.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zhangbinglin
 * @Date 2020/6/17 11:52
 **/
@Data
@ApiModel("活跃度明细DTO")
public class ActiveDetailDto{

    @ApiModelProperty("日期")
    private String date;

    @ApiModelProperty("类型（如：应用名称，或者平台名称）")
    private String type;

    @ApiModelProperty("左侧数值")
    private Double leftValue;

    @ApiModelProperty("右侧数值")
    private Double rightValue;
}
