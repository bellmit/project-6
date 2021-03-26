package com.miguan.reportview.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 报表
 * @Author zhangbinglin
 * @Date 2020/9/8 9:31
 **/
@Data
public class HeadFieldDto {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "字段code")
    private String fieldCode;

    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    @ApiModelProperty(value = "字段类型：0=整数，1=百分数（保留2位小数），2=小数（保留2位小数）,3=小数（保留四位小数），4=字符串")
    private Integer fieldType;

    @ApiModelProperty(value = "指标类型")
    private Integer showType;

    @ApiModelProperty(value = "字段注释")
    private String fieldRemark;

    @ApiModelProperty(value = "字段是否显示，-1=不显示，1=显示")
    private Integer isShow;
}
