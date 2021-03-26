package com.miguan.ballvideo.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("计划和代码位的关联表")
public class AdvertPlanCodeRelationVo {

    @ApiModelProperty("关联表id")
    private Long id;
    @ApiModelProperty("计划id")
    private Long plan_id;
    @ApiModelProperty("代码位id")
    private Long code_id;
    @ApiModelProperty("创建时间")
    private Date created_at;
    @ApiModelProperty("更新时间")
    private Date updated_at;
}
