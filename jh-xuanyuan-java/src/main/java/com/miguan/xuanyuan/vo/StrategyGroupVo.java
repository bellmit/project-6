package com.miguan.xuanyuan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class StrategyGroupVo {

    @ApiModelProperty("分组id")
    private Long strategyGroupId;

    @ApiModelProperty("广告位id")
    private Long positionId;

    @ApiModelProperty("渠道")
    private String channel = "";

    @ApiModelProperty("包名")
    private String packageName = "";

    @ApiModelProperty("appId")
    private Long appId;

    @ApiModelProperty("广告样式")
    private String adType = "";

    @ApiModelProperty("广告平台代码位id")
    private String appVersion = "";

    @ApiModelProperty("实验id")
    private Long abId;

    @ApiModelProperty("实验状态")
    private Integer abStatus;

    @ApiModelProperty("运行时间")
    private String beginTime;

    @ApiModelProperty("自定义开关")
    private Integer customSwitch;

    @ApiModelProperty("策略列表")
    private List<StrategyVo> strategylist;

    @ApiModelProperty("所有代码位列表")
    private List<StrategyCodeVo> allCodeList;

    public void init() {
        
    }


}
