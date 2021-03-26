package com.miguan.advert.domain.pojo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

@ApiModel("代码位")
@Data
public class AdAdvertCode {

    @ApiModelProperty("代码位Id")
    private Integer id;

    @ApiModelProperty("广告id")
    private String ad_id;

    @ApiModelProperty("广告样式")
    private String adv_css;

    @ApiModelProperty("应用包1*应用包")
    private String app_package;

    @ApiModelProperty("渠道状态")
    private Integer channel_type;

    @ApiModelProperty("所有：-1，一个或者多个逗号隔开")
    private String channel_ids;

    @ApiModelProperty("广告素材，对应表ad_meterial")
    private String material_key;

    @ApiModelProperty("是否需要权限 0否 1是")
    private String permission;

    @ApiModelProperty("广告平台，对应表ad_plat")
    private String plat_key;

    @ApiModelProperty("使用SDK，对应表ad_sdk")
    private String sdk_key;

    @ApiModelProperty("广告类型,对应表ad_type")
    private String type_key;

    @ApiModelProperty("版本区间1")
    private String version1;

    @ApiModelProperty("版本区间2")
    private String version2;

    @ApiModelProperty("渲染")
    private String render_key;

    @ApiModelProperty("阶梯价格")
    private String ladder_price;

    @ApiModelProperty("1阶梯，2非阶梯*1阶梯，2非阶梯")
    private Integer ladder;

    @ApiModelProperty("是否投放，1已投放，2未投放")
    private Integer put_in;

    @ApiModelProperty("状态")
    private Integer state;

    @ApiModelProperty("视频来源。98du，wuli")
    private String videos_source;

    @ApiModelProperty("操作系统：1：ios,2:android,3:小程序")
    private Integer mobile_type;

    @ApiModelProperty("创建日期")
    private Date created_at;
    @ApiModelProperty("修改日期")
    private Date updated_at;


    private String app_name;
    private String plat_name;
    private String rander_name;
    private String type_name;
    private String material_name;
    private Integer del_status;
    private String channel_name;
    private String position_name;

}