package com.miguan.report.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("使用时长总览数据实体")
@Data
public class AppUseTimePandectVo {

    @ApiModelProperty("日期")
    private String day;
    @ApiModelProperty("时长")
    private String time;
    @ApiModelProperty("环比")
    private String linkRelativeRatio;
    @ApiModelProperty("同比")
    private String sameRelativeRatio;
}
