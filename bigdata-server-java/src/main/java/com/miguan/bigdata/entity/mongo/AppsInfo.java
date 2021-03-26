package com.miguan.bigdata.entity.mongo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel("注册设备")
public class AppsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String distinctId;

    private String appPackage;

    private String deviceId;
}
