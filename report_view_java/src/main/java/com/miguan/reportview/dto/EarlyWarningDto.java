package com.miguan.reportview.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 商业化 钉钉预警dto
 * @Author zhangbinglin
 * @Date 2020/10/23 9:00
 **/
@Data
public class EarlyWarningDto {

    @ApiModelProperty("应用名称")
    private String packageNameZw;

    @ApiModelProperty("广告位名称")
    private String adKeyName;

    @ApiModelProperty("库存")
    private int stock;

    @ApiModelProperty("广告展示/广告库存比值(百分比)")
    private double showStockRate;
}
