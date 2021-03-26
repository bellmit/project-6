package com.miguan.advert.domain.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("应用类型信息")
@Data
public class AppTypeVo {

    @ApiModelProperty("应用类型id")
    private Integer id;

    @ApiModelProperty("应用类型名称")
    private String type_name;
}