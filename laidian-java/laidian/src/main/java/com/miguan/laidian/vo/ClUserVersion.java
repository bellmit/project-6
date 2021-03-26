package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel("用户更新APP表")
public class ClUserVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("设备id")
    private String deviceId;

    @ApiModelProperty("用户登录手机号")
    private String loginName;

    @ApiModelProperty("更新版本号")
    private String versionNumber;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("App类型")
    private String appType;

}
