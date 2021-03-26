package com.miguan.xuanyuan.vo.sdk;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("AB测试命中规则存储表")
@Data
public class AbTestRuleVo {

    @ApiModelProperty("广告位置KEY")
    private String positionKey;

    @ApiModelProperty("xy_strategy_group表的id")
    private Long groupId;

    @ApiModelProperty("ab实验组id")
    private String abId;

    @ApiModelProperty("实验开启状态: 1开启")
    private Integer customSwitch;
}
