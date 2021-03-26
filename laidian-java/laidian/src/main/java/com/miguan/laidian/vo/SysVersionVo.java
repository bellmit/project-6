package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysVersionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("最新版本")
    private String appVersion;

    @ApiModelProperty("下载地址")
    private String appAddress;

    @ApiModelProperty("手机类型：1-ios，2：安卓")
    private String mobileType;

    @ApiModelProperty("是否强制更新：10--否，20--是")
    private String forceUpdate;

    @ApiModelProperty("手机app最新版本更新内容")
    private String updateContent;

    @ApiModelProperty("渠道")
    private String channel;

    @ApiModelProperty("项目ID")
    private String sId;

    @ApiModelProperty("APP配置Id")
    private String groupId;

    @ApiModelProperty("实际升级人数")
    private String realUserUpdateCount;

    @ApiModelProperty("计划升级总人数")
    private String updateUserTotalCount;

}
