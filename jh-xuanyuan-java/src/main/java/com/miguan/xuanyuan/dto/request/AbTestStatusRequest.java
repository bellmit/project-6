package com.miguan.xuanyuan.dto.request;

import com.miguan.xuanyuan.dto.ab.AbItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("ab实验状态请求")
@Data
public class AbTestStatusRequest {

    @ApiModelProperty("分组id")
    private Long strategyGroupId;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("开始时间")
    private String beginTime;

}

