package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("ab实验平台参数")
@Data
public class AbTestAdvParamExpVo {

    @ApiModelProperty("实验ID")
    private Long group_id;

    @ApiModelProperty("实验key")
    private String exp_id;
}
