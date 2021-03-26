package com.miguan.xuanyuan.vo.business;

import lombok.Data;

@Data
public class AppInfoReq {
    private String appPackage;
    private String platform;
    private String remark = "create by xuanyuan";
    private String type = "xy";
}
