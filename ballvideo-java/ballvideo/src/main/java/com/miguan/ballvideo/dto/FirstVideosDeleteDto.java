package com.miguan.ballvideo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户删除发布视频对象")
public class FirstVideosDeleteDto {

    @ApiModelProperty(value = "报名", required = true)
    private String appPackage;

    @ApiModelProperty(value = "用户ID", required = true)
    private String userId;

    @ApiModelProperty(value = "视频ID", required = true)
    private Long id;

}
