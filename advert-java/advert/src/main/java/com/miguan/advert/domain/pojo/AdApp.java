package com.miguan.advert.domain.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class AdApp {

    @ApiModelProperty("Id")
    private Integer id;

    @ApiModelProperty("app_key")
    private String app_id;

    @ApiModelProperty("密钥")
    private String secret_key;

    @ApiModelProperty("应用类型")
    private Integer type;

    @ApiModelProperty("对接方式：1：api,2:sdk")
    private Integer dock_type;

    @ApiModelProperty("结算方式：1:CPS,2:CPC")
    private Integer balance_type;

    @ApiModelProperty("计算方式对应的计算参数")
    private String balance_param;

    @ApiModelProperty("账户id")
    private Integer account_id;

    @ApiModelProperty("应用名称")
    private String app_name;

    @ApiModelProperty("包名称")
    private String package_name;

    @ApiModelProperty("操作系统:1:ios,2:安卓")
    private String operating_system;

    @ApiModelProperty("状态 1：启用，0：未启用")
    private Integer status;

    @ApiModelProperty("最后操作用户")
    private String last_operate_admin;

    @ApiModelProperty("是否接入dsp平台：1：是，0：否")
    private Integer is_dsp;

    @ApiModelProperty("创建日期")
    private Date created_at;
    @ApiModelProperty("修改日期")
    private Date updated_at;
}