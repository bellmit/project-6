package com.miguan.ballvideo.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("广告创意列表")
public class AdvertDesignListRes {
    @ApiModelProperty("广告创意id")
    private Long id;

    @ApiModelProperty("广告主id")
    private Long advert_user_id;

    @ApiModelProperty("计划创意名称")
    private String name;

    @ApiModelProperty("落地页")
    private String put_in_value;

    @ApiModelProperty("所属广告计划")
    private String plan_name;

    @ApiModelProperty("广告计划状态,0-暂停,1-投放中")
    private Integer plan_state;

    @ApiModelProperty("0-暂停,1-投放中")
    private Integer state;

    @ApiModelProperty("创意形式")
    private Integer material_shape;

    @ApiModelProperty("展示数 || 曝光量")
    private Integer exposure;

    @ApiModelProperty("点击量")
    private Integer valid_click;

    @ApiModelProperty("点击用户数")
    private Integer click_user;

    @ApiModelProperty("点击率  点击数/展示数")
    private Double pre_click_rate;

    @ApiModelProperty("点击均价 单次点击的价格，花费/点击数")
    private Double click_price;

    @ApiModelProperty("花费")
    private Double consume;

    @ApiModelProperty("千次展示均价 消耗/展示数 * 1000")
    private Double exposure_price;

    public Integer getExposure() {
        return exposure == null ? 0 : exposure;
    }

    public Integer getValid_click() {
        return valid_click == null ? 0 : valid_click;
    }

    public Integer getClick_user() {
        return click_user == null ? 0 : click_user;
    }

    public Double getPre_click_rate() {
        return pre_click_rate == null ? 0 : pre_click_rate;
    }

    public Double getClick_price() {
        return click_price == null ? 0 : click_price;
    }

    public Double getConsume() {
        return consume == null ? 0 : consume;
    }

    public Double getExposure_price() {
        return exposure_price == null ? 0 : exposure_price;
    }
}
