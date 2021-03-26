package com.miguan.reportview.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 友盟渠道数据vo
 */
@Setter
@Getter
public class UmengChannelDataVo {

    public UmengChannelDataVo() {
        packageName = "";
        fatherChannel = "";
        channel = "";
    }

    /**
     * 日期
     */
    private String date;

    /**
     * 包名
     */
    private String packageName;

    /**
     * 父渠道
     */
    private String fatherChannel;

    /**
     * 渠道
     */
    private String channel;

    /**
     * 新增用户（友盟）
     */
    private Double newUser;

    public String getKey() {
        return date + packageName + fatherChannel + channel;
    }
}
