package com.miguan.laidian.vo;

import com.miguan.laidian.entity.ActActivityConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chenwf
 * @date 2020/5/22
 */
@Data
public class ActivityConfigVo extends ActActivityConfig {
    @ApiModelProperty("会员获取的碎片数量")
    private Integer debrisNum;
    @ApiModelProperty("兑换状态 0 核实中 1 已发放 2 未兑换")
    private Integer state;
}
