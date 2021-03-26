package com.miguan.advert.domain.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("广告平台信息")
@Data
public class AdPlatVo {

    @ApiModelProperty("广告平台标识")
    private String key;

    @ApiModelProperty("广告平台名称")
    private String name;
}