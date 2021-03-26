package com.miguan.flow.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description demo
 * @Author zhangbinglin
 * @Date 2020/12/7 14:08
 **/
@Data
public class DemoDto {

    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("包名")
    private String appPackage;

    @ApiModelProperty("广告位名称")
    private String name;

    @ApiModelProperty("关键字")
    private String positionType;
}
