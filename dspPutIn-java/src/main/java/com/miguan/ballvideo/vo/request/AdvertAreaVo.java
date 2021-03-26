package com.miguan.ballvideo.vo.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("区域表")
public class AdvertAreaVo {
    @ApiModelProperty("区域表id")
    private Long id;
    @ApiModelProperty("类型,1:不限区域，2：指定区域")
    private Integer type;
    @ApiModelProperty("计划ID")
    private Long plan_id;
    @ApiModelProperty("区域")
    private String area;
    @ApiModelProperty(value = "创建时间", hidden=true)
    private Date created_at;

    @ApiModelProperty(value = "修改时间" , hidden=true)
    private Date updated_at;
}
