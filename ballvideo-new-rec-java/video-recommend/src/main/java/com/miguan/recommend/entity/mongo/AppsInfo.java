package com.miguan.recommend.entity.mongo;

import com.alibaba.fastjson.JSONArray;
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

    private JSONArray appsList;
}
