package com.miguan.xuanyuan.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * @Description TODO
 * @Author zhangbinglin
 * @Date 2021/3/4 9:20
 **/
@Data
public class ThirdPlatDataVo {

    /**
     * 日期
     */
    private String date;

    /**
     * 平台key,例如：kuai_shou，guang_dian_tong，chuan_shan_jia
     */
    private String adSource;

    /**
     * 开发者账号
     */
    private String name;

    /**
     * 项目名称
     */
    private String appName;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 代码位类型
     */
    private String adType;

    /**
     * 代码位id
     */
    private String adId;

    /**
     * 代码位名称
     */
    private String adName;



    /**
     * 预估收益
     */
    private Double revenue;

    /**
     * 曝光量
     */
    private Integer show;

    /**
     * 点击量
     */
    private Integer click;

    /**
     * 点击率(点击量 / 曝光量 * 100%)
     */
    private Double clickRate;

    /**
     * eCPM
     */
    private Double ecpm;

    /**
     * 广告位请求量
     */
    private Integer requestCount;

    /**
     * 广告位返回量
     */
    private Integer returnCount;

    /**
     * 广告请求量
     */
    private Integer adRequestCount;

    /**
     * 广告返回量
     */
    private Integer adReturnCount;

    /**
     * 广告位填充率 (广告位返回量 / 广告位请求量 * 100%)
     */
    private Double fillRate;

    /**
     * 广告位曝光率 (曝光量/广告位返回量 * 100%)
     */
    private Double exposureRate;

    /**
     * 广告填充率 (广告返回量/广告请求量 * 100%)
     */
    private Double adFillRate;

    /**
     * 广告曝光率 (曝光量 / 广告返回量 * 100%)
     */
    private Double adExposureRate;

    /**
     * 点击成本(收入 / 点击量) (单位：元)
     */
    private Double cpc;
}
