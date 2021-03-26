package com.miguan.xuanyuan.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description TODO
 * @Author zhangbinglin
 * @Date 2021/3/16 14:26
 **/
@Data
public class PlanPositionDto {

    public PlanPositionDto() {
    }

    public PlanPositionDto(Integer planId, Integer positionId, String codeId) {
        this.planId = planId;
        this.positionId = positionId;
        this.codeId = codeId;
    }

    @ApiModelProperty(value = "计划id")
    private Integer planId;

    @ApiModelProperty(value = "广告位id")
    private Integer positionId;

    @ApiModelProperty(value = "代码位id")
    private String codeId;
}
