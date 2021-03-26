package com.miguan.advert.domain.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("应用列表信息")
@Data
public class AppInfoVo {

    @ApiModelProperty("主键Id")
    private Integer id;

    @ApiModelProperty("包名称")
    private String key;

    @ApiModelProperty("操作系统:1:ios,2:安卓")
    private Integer mobile_type;

    @ApiModelProperty("应用名称")
    private String name;
}