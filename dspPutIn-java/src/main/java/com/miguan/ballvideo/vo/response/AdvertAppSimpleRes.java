package com.miguan.ballvideo.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("app(字段少)")
@Data
public class AdvertAppSimpleRes {

    @ApiModelProperty("appID")
    private Long id;

    @ApiModelProperty("app名称")
    private String name;

    @ApiModelProperty("appPackage")
    private String app_package;

    @ApiModelProperty("应用端：1-ios 2-Android")
    private String mobile_type;
}
