package com.miguan.flow.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("AB测试命中规则存储表")
@Data
public class AbTestRuleVo {

    @ApiModelProperty("广告位置ID")
    private Long positionId;

    @ApiModelProperty("广告位关键字")
    private String positionType;

    @ApiModelProperty("flow表的id")
    private Long flowId;

    @ApiModelProperty("ab实验组id")
    private String abFlowId;

    @ApiModelProperty("实验开启状态: 1开启")
    private Integer openStatus;
}
