package com.miguan.ballvideo.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 代码位
 **/
@ApiModel("代码位(字段少)")
@Data
public class AdvertCodeSimpleRes {

    @ApiModelProperty("代码位ID")
    private Long id;

    @ApiModelProperty("广告位名称")
    private String name;

    @ApiModelProperty("app名称+广告位名称")
    private String app_position_name;

    @ApiModelProperty("0-关闭，1开启")
    private String state;

    @ApiModelProperty("appId")
    private Long app_id;
}

