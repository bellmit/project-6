package com.miguan.reportview.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhongli
 * @date 2020-08-05 
 *
 */
@Setter
@Getter
public class AdErrorDataVo {
    /**
     * 请求量
     */
    private double reqCount;
    /**
     * 错误设备数
     */
    private double deviceCount;
    /**
     * 错误数
     */
    private double errCount;
    /**
     * 日活
     */
    private double activeUser;

    /**
     * 日期
     */
    private String dd;
    private String packageName;
    private String appVersion;
    private String isNew;
    private String fatherChannel;
    private String channel;
    /**
     * 平台
     */
    private String adSource;
    /**
     * 广告位置关键字
     */
    private String adKey;
    /**
     * 代码位ID
     */
    private String adId;
    /**
     * 错误类型
     */
    private String msg;
    /**
     * 机型
     */
    private String model;

}
