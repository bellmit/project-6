package com.miguan.ballvideo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户个人中心对象")
public class UserCenterDto {

    @ApiModelProperty(value = "APP包名",  required = true)
    private String appPackage;

    @ApiModelProperty(value = "用户Id",  required = true)
    private String queryId;

    @ApiModelProperty(value = "是否自己：0 否 1 是", required = false)
    private Integer isOwn;

    @ApiModelProperty(value = "是否返回用户信息和视频统计信息：0 否(只返回视频列表) 1 是(返回用户信息和视频统计、以及视频列表)", required = true)
    private Integer isMoreInfo;

    @ApiModelProperty("每页大小，默认8")
    private Integer pageSize;

    @ApiModelProperty(value = "页码", required = true)
    private Integer pageNum;
}
