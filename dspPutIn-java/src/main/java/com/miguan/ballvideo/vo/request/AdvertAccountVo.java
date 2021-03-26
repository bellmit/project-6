package com.miguan.ballvideo.vo.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("创意广告-计划账户表")
public class AdvertAccountVo {
    @ApiModelProperty("账户表id")
    private Long id;
    @ApiModelProperty("剩余日预算（元）")
    private BigDecimal remain_day_price;
    @ApiModelProperty("剩余总预算（元）")
    private BigDecimal remain_total_price;
    @ApiModelProperty("计划ID")
    private Long plan_id;
    @ApiModelProperty("创建时间")
    private Date created_at;
    @ApiModelProperty("更新时间")
    private Date updated_at;
}
