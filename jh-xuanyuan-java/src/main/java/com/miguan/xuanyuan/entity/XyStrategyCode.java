package com.miguan.xuanyuan.entity;

import com.miguan.xuanyuan.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 策略代码位
 *
 */
@Data
public class XyStrategyCode extends BaseModel {

    private Long strategyId;

    private Long adCodeId;

    private String codeId;

    private Long orderNum;

    private Long priority;

    private Long rateNum;

    private Integer status ;


}
