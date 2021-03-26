package com.miguan.idmapping.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author jobob
 * @since 2020-07-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel("注册设备")
public class DeviceInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备唯一标识
     */
    @ApiModelProperty(value = "设备id", hidden = true)
    private String distinct_id;

    @ApiModelProperty("客户端类型: android,ios,h5,weixin,xiaochengxu,web")
    private String type;

    /**
     * 创建初始渠道
     */
    @ApiModelProperty("初始渠道ID")
    private String channel;

    @ApiModelProperty("操作系统")
    private String os;

    @ApiModelProperty("设备制造商 如Apple")
    private String manufacturer;

    @ApiModelProperty("屏幕高度")
    private Integer screenHeight;

    @ApiModelProperty("屏幕宽度")
    private Integer screenWidth;

    @ApiModelProperty("品牌: huawei、iphone、xiaomi、OPPO")
    private String brand;

    @ApiModelProperty("型号: iphone xr")
    private String model;

    /**
     * 关键字段1
     */
    private String number1;

    /**
     * 关键字段2
     */
    private String number2;

    /**
     * 关键字段3
     */
    private String number3;

    /**
     * 关键字段4
     */
    private String number4;

    /**
     * 关键字段5
     */
    private String number5;

    /**
     * 关键字段6
     */
    private String number6;

    /**
     * 关键字段7
     */
    private String number7;

    /**
     * 关键字段8
     */
    private String number8;

    /**
     * 关键字段9
     */
    private String number9;

    /**
     * 关键字段10
     */
    private String number10;

    /**
     * 创建时间
     */
    @ApiModelProperty(hidden = true)
    private String createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(hidden = true)
    private String updateTime;

}
