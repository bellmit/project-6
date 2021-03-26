package com.miguan.laidian.vo;

import com.miguan.laidian.entity.ActActivityConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chenwf
 * @date 2020/5/22
 */
@Data
public class ActivityDrawProductVo extends ActActivityConfig {
    @ApiModelProperty("抽奖记录id")
    private Long drawRecordId;
}
