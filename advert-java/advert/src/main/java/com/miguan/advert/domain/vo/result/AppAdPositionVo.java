package com.miguan.advert.domain.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("广告位置列表信息")
@Data
public class AppAdPositionVo {

    @ApiModelProperty("主键Id")
    private Integer id;

    @ApiModelProperty("关键字")
    private String key;

    @ApiModelProperty("操作系统:1:ios,2:安卓")
    private Integer mobile_type;

    @ApiModelProperty("广告位名称")
    private String name;

    @ApiModelProperty("应用名称")
    private String app_name;

    @ApiModelProperty("包名")
    private String app_package;
}