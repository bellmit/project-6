package com.miguan.flow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("通用请求参数实体")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncentiveParamDto {

    @ApiModelProperty(required = true, value = "需要返回的激励视频个数")
    private Integer incentiveNum;
    @ApiModelProperty(required = true, value = "全局参数：设备标识(客户端上报埋点时的值)")
    private String device_id;
    @ApiModelProperty(required = false, value = "全局参数：用户注册后的用户ID")
    private String user_id;
    @ApiModelProperty(required = true, value = "全局参数：渠道")
    private String channel;
    @ApiModelProperty(required = true, value = "全局参数：app的包名")
    private String package_name;
    @ApiModelProperty(required = true, value = "全局参数：app的版本")
    private String app_version;
    @ApiModelProperty(required = true, value = "全局参数：机型")
    private String model;
    @ApiModelProperty(required = true, value = "全局参数：操作系统 android/ios")
    private String os;
    @ApiModelProperty(required = true, value = "全局参数：是否首次访问该app, true/false")
    private String is_first_app;
    @ApiModelProperty(required = true, value = "全局参数：是否新用户, true/false")
    private String is_first;
    @ApiModelProperty(required = true, value = "全局参数：客户端IP地址")
    private String ip;
    @ApiModelProperty(required = false, value = "全局参数：国家")
    private String country;
    @ApiModelProperty(required = false, value = "全局参数：省")
    private String province;
    @ApiModelProperty(required = false, value = "全局参数：市")
    private String city;
//    @ApiModelProperty(required = true, value = "全局参数：ab实验组")
//    private String ab_exp;
//    @ApiModelProperty(required = true, value = "全局参数：ab实验隔离域")
//    private String ab_isol;
}
