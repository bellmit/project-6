package com.miguan.flow.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("代码位收益")
@Data
public class AdProfitVo {

    @ApiModelProperty("代码位id")
    private String adId;

    @ApiModelProperty("收益")
    private Double profit;

    @ApiModelProperty("展示数(自监测)")
    private Integer show;

    @ApiModelProperty("点击数（自监测）")
    private Integer click;

    @ApiModelProperty("有效展示数")
    private Integer exposure;

    @ApiModelProperty("有效点击数")
    private Integer validClick;
}
