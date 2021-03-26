package com.miguan.xuanyuan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("策略代码位信息")
@Data
public class StrategyCodeVo {

    @ApiModelProperty("策略代码id")
    private Long strategyCodeId;

    @ApiModelProperty("代码位id")
    private Long codeId;

    @ApiModelProperty("三方代码位id")
    private String sourceCodeId;

    @ApiModelProperty("广告平台标识")
    private String sourcePlatKey;

    @ApiModelProperty("广告源名称")
    private String codeName;

    @ApiModelProperty("是否阶梯广告")
    private Integer isLadder;

    @ApiModelProperty("阶梯价")
    private Long ladderPrice;

    @ApiModelProperty("序号")
    private Long priority;

    @ApiModelProperty("配比")
    private Long rateNum;

    @ApiModelProperty("代码位投放状态")
    private Integer codeStatus;

    @ApiModelProperty("启用情况")
    private Integer status;

    @ApiModelProperty("ecpm")
    private Integer ecpm = 0;

    @ApiModelProperty("收益")
    private Integer profitCnt = 0;

    @ApiModelProperty("展示")
    private Integer showCnt = 0;

    @ApiModelProperty("点击")
    private Integer clickCnt = 0;

}
