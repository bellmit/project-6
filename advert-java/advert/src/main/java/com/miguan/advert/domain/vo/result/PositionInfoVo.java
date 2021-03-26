package com.miguan.advert.domain.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("广告位置信息")
@Data
public class PositionInfoVo {

    @ApiModelProperty("代码位主键Id")
    private Integer id;

    @ApiModelProperty("代码位Id")
    private String ad_id;

    @ApiModelProperty("广告类型名称")
    private String adv_name;

    @ApiModelProperty("渠道：所有：-1，一个或者多个逗号隔开")
    private String channel_ids;

    @ApiModelProperty("渠道名称")
    private String channel_name;

    @ApiModelProperty("渠道类型：1-全部 2-仅限 3-排除")
    private String channel_type;

    @ApiModelProperty("是否配置")
    private Integer is_have;

    @ApiModelProperty("1阶梯，2非阶梯")
    private Integer ladder;

    @ApiModelProperty("阶梯价格")
    private String ladder_price;

    @ApiModelProperty("广告信息")
    private String name;

    @ApiModelProperty("排序值或者概率值")
    private Integer option_value;

    @ApiModelProperty("是否需要权限 0否 1是")
    private Integer permission;

    @ApiModelProperty("广告平台")
    private String plat_key;

    @ApiModelProperty("广告平台名称")
    private String plat_name;

    @ApiModelProperty("是否投放，1已投放，2未投放")
    private Integer put_in;

    @ApiModelProperty("广告类型标识")
    private String type_key;

    @ApiModelProperty("版本区间")
    private String version;

    @ApiModelProperty("起始版本")
    private String version1;

    @ApiModelProperty("结束版本")
    private String version2;
}
