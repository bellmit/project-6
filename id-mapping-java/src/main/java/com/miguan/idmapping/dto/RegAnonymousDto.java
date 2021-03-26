package com.miguan.idmapping.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zhongli
 * @date 2020-07-21 
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("注册匿名用户")
public class RegAnonymousDto {

    /**
     * 设备唯一标识
     */
    @ApiModelProperty(value = "原设备唯一标识,客户端无此值传空或null")
    private String distinctId;

    @ApiModelProperty("客户端类型: android,ios,h5,weixin,xiaochengxu,web")
    private String type;

    /**
     * 创建时应用版本号
     */
    @ApiModelProperty("应用版本号")
    private String appVersion;

    /**
     * 创建初始渠道
     */
    @ApiModelProperty("渠道ID")
    private String channel;

    /**
     * 创建时的马甲包
     */
    @ApiModelProperty("马甲包")
    private String packageName;
    /**
     * 设备ID
     */
    @ApiModelProperty("设备ID")
    private String deviceId;
    //========================== 设备信息字段 =========================================//

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

}
