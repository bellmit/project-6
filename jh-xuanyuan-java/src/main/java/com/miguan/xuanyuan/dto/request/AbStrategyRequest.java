package com.miguan.xuanyuan.dto.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@ApiModel("策略")
@Data
public class AbStrategyRequest {

    private Long strategyId;

//    private Integer abStatus;

    private  Integer sortType;

    private List<String> customField;

    private List<AbStrategyCodeFlowRequest> adCodeList;


}
