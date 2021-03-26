package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description 查询参数VO
 * @author laiyudan
 * @date 2019-10-22
 **/
@ApiModel("查询参数VO")
@Getter
@Setter
public class QueryParamsVo {

    @ApiModelProperty("设备ID")
    private String deviceId;
}
