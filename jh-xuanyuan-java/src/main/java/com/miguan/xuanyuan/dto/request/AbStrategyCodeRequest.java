package com.miguan.xuanyuan.dto.request;

import com.miguan.xuanyuan.dto.ab.AbFlowGroupParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.List;

@ApiModel("策略代码位")
@Data
public class AbStrategyCodeRequest {

    private Long strategyGroupId;
    private Integer customSwitch ;
    private List<AbStrategyRequest> list;

}
