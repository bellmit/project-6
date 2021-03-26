package com.miguan.advert.domain.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("城市列表")
@Data
public class AdCityVo {

    @ApiModelProperty("主键Id")
    private Integer id;

    @ApiModelProperty("城市名")
    private String city;

}