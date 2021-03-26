package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 计划组vo
 * @Author zhangbinglin
 * @Date 2020/11/20 14:55
 **/
@Data
public class AdvertGroupListVo {

    @ApiModelProperty("计划组id")
    private Integer id;

    @ApiModelProperty("推广目的：1-应用推广，2-品牌推广")
    private Integer promotionPurpose;

    @ApiModelProperty("计划组名称")
    private String name;

    @ApiModelProperty("状态：0-暂停，1-投放中")
    private Integer state;

    @ApiModelProperty("消耗")
    private Double consume;

    @ApiModelProperty("组日预算，-1表示不限制预算")
    private Double dayPrice;

    @ApiModelProperty("展示数（有效曝光）")
    private Integer exposure;

    @ApiModelProperty("点击数（有效点击数）")
    private Integer validClick;

    @ApiModelProperty("点击用户数")
    private Integer clickUser;

    @ApiModelProperty("平均千次展示收益")
    private Double preEcpm;

    @ApiModelProperty("平均点击单价")
    private Double preClickPrice;

    @ApiModelProperty("点击率(百分比，已乘了100)")
    private Double preClickRate;

    public Double getConsume() {
        return (consume == null ? 0D : consume);
    }

    public Double getDayPrice() {
        return (dayPrice == null ? 0D : dayPrice);
    }

    public Integer getExposure() {
        return (exposure == null ? 0 : exposure);
    }

    public Integer getValidClick() {
        return (validClick == null ? 0 : validClick);
    }

    public Integer getClickUser() {
        return clickUser == null ? 0 : clickUser;
    }

    public Double getPreEcpm() {
        return (preEcpm == null ? 0 : preEcpm);
    }

    public Double getPreClickPrice() {
        return (preClickPrice == null ? 0D : preClickPrice);
    }

    public Double getPreClickRate() {
        return (preClickRate == null ? 0D : preClickRate);
    }
}
