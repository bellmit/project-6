package com.miguan.xuanyuan.dto.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@ApiModel("策略代码位排序等")
@Data
public class AbStrategyCodeFlowRequest {

    private String sourceCodeId;

    private Long codeId;

    private Long priority;

    private Long rateNum;

    private Long orderNum = 0l;
}
