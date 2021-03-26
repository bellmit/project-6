package com.miguan.bigdata.entity.xy;

import lombok.Data;

import java.util.Date;

@Data
public class ClDevice {

    private Long id;
    private String deviceId;
    private String state;
    private Date createTime;
    private Date updateTime;
    private String deviceToken;
    private String huaweiToken;
    private String vivoToken;
    private String oppoToken;
    private String xiaomiToken;
    private String appPackage;
    private Integer illegalToken;
    private Integer isDelete;
    private String distinctId;
    private String appVersion;
    private String utdId;
}
