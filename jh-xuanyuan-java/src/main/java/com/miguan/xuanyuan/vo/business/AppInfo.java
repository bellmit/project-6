package com.miguan.xuanyuan.vo.business;

import lombok.Data;

@Data
public class AppInfo {
    private String appKey;
    private String appPackage;
    private String appSecret;
    private String platform;
}
