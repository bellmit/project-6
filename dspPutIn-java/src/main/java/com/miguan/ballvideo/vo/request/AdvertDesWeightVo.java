package com.miguan.ballvideo.vo.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("广告关联表")
public class AdvertDesWeightVo {
    @ApiModelProperty("关联表id")
    private Long id;
    @ApiModelProperty("计划组id")
    private Long group_id;
    @ApiModelProperty("计划ID")
    private Long plan_id;
    @ApiModelProperty("创意ID")
    private Long design_id;
    @ApiModelProperty("权重")
    private Long weight;
    @ApiModelProperty(value = "创建时间", hidden=true)
    private Date created_at;

    @ApiModelProperty(value = "修改时间" , hidden=true)
    private Date updated_at;
}
