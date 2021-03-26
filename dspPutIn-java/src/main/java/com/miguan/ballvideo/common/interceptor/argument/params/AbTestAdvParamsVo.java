package com.miguan.ballvideo.common.interceptor.argument.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("AB实验平台入参实体")
public class AbTestAdvParamsVo {

    @ApiModelProperty("实验分组Id")
    private String abTestId;
}
