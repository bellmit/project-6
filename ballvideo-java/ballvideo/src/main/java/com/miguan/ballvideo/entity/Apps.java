package com.miguan.ballvideo.entity;

import com.miguan.ballvideo.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@ApiModel("用户app列表")
@Entity(name = "cl_apps")
public class Apps extends BaseModel {

    @ApiModelProperty("设备表id")
    @Column(name = "device_id")
    private Long deviceId;

    @ApiModelProperty("app名称")
    @Column(name = "app_name")
    private String appName;

    @ApiModelProperty("app包名")
    @Column(name = "package_name")
    private String packageName;

    @ApiModelProperty("app版本")
    @Column(name = "app_version")
    private String appVersion;

    @ApiModelProperty("是否是系统app应用  0否  1是")
    @Column(name = "is_system_app")
    private Integer isSystemApp;
}
