package com.miguan.xuanyuan.dto;

import com.miguan.xuanyuan.entity.common.BaseModel;
import lombok.Data;

/**
 * 策略代码位
 *
 */
@Data
public class XyStrategyCodeDto {

    private Long strategyCodeId;

    private Long strategyId;

    private Long codeId;

    private String sourceCodeId;

    private String sourcePlatKey;

    private String codeName;

    private Long orderNum;

    private Long priority;

    private Long rateNum;

    private Integer status;

    private Integer codeStatus;

    private Integer isLadder;

    private Long ladderPrice;

}
