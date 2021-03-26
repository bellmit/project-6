package com.miguan.ballvideo.vo.response;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("区域信息")
public class DistrictRes {
    @ApiModelProperty("区域表id")
    private Long id;
    @ApiModelProperty("父区域")
    private Long pid;
    @ApiModelProperty("区域名称")
    private String name;
}
