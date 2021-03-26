package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 计划组vo
 * @Author zhangbinglin
 * @Date 2020/11/20 14:55
 **/
@Data
public class AdvertGroupVo {

    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("推广目的：1-应用推广，2-品牌推广")
    private Integer promotionPurpose;

    @ApiModelProperty("计划组名称")
    private String name;

    @ApiModelProperty("日预算，-1表示不限制预算")
    private Double dayPrice;

    @ApiModelProperty("状态：0-暂停，1-投放中")
    private Integer state;
}
