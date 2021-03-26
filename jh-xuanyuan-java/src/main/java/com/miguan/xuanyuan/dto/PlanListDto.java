package com.miguan.xuanyuan.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value="计划列表dto", description="计划列表dto")
public class PlanListDto {

    @ApiModelProperty(value = "计划id", position = 1)
    private Integer id;

    @ApiModelProperty(value = "启用状态：1启用，0未启用", position = 10)
    private Integer status;

    @ApiModelProperty(value = "计划名称", position = 20)
    private String name;

    @ApiModelProperty(value = "总预算（元）", position = 30)
    private Double totalPrice;

    @ApiModelProperty(value = "曝光数", position = 40)
    private Integer show;

    @ApiModelProperty(value = "点击数", position = 60)
    private Integer click;

    @ApiModelProperty(value = "点击率（%）", position = 70)
    private Double clickRate;

    @ApiModelProperty(value = "投放日期类型，0-长期投放，1-指定开始日期和结束日期", position = 80)
    private Integer putTimeType;

    @ApiModelProperty(value = "投放时间", position = 90)
    private String putInTimeSlot;

    @ApiModelProperty(value = "广告类型；interaction:插屏, infoFlow:信息流广告, banner:Banner广告, open_screen:开屏广告", position = 100)
    private String advertType;

    @ApiModelProperty(value = "投放应用", position = 110)
    private String putInApp;

    @ApiModelProperty(value = "投放广告位", position = 120)
    private String positionName;

    @ApiModelProperty(value = "创意数", position = 130)
    private Integer designCount;

    @ApiModelProperty(value = "投放状态,0-未开始投放，1-投放中，2-投放结束", position = 140)
    private Integer putInState;

    @ApiModelProperty(value = "开始日期", position = 150)
    private Date startDate;

    @ApiModelProperty(value = "结束日期", position = 160)
    private Date endDate;
}
