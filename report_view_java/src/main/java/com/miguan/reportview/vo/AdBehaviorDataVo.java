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
public class AdBehaviorDataVo {
    /**
     * 请求量
     */
    private double req;
    /**
     * 返回量
     */
    private double res;
    /**
     * 展现量(曝光量)
     */
    private double vshow;
    /**
     * 自监测展现量
     */
    private double zoneShow;
    /**
     * 点击量
     */
    private double vclick;
    /**
     * 自监测点击量
     */
    private double zoneClick;
    /**
     * 库存
     */
    private double stock;

    /**
     * 有效爆光用户
     */
    private double vshowUser;
    /**
     * 广告点击用户
     */
    private double clickUser;
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

}
