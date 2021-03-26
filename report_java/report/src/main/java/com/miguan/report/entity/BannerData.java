package com.miguan.report.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * jpa entity class for banner_data
 * 广告商数据
 */
@Data
@Entity
@Table(name = "banner_data")
public class BannerData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /**
     * 广告位id
     */
    private Integer id;

    /**
     * 日期
     */
    private java.util.Date date;

    /**
     * 应用id/媒体id
     */
    @Column(name = "app_id")
    private String appId;

    /**
     * 媒体/app名
     */
    @Column(name = "app_name")
    private String appName;

    /**
     * 代码位/广告位
     */
    @Column(name = "ad_space")
    private String adSpace;

    /**
     * 代码位id/广告位id
     */
    @Column(name = "ad_space_id")
    private String adSpaceId;

    /**
     * 代码位类型/渲染方式
     */
    @Column(name = "ad_space_type")
    private String adSpaceType;

    /**
     * 接入方式
     */
    @Column(name = "access_mode")
    private String accessMode;

    /**
     * 展现量
     */
    @Column(name = "show_number")
    private Integer showNumber;

    /**
     * 点击量
     */
    @Column(name = "click_number")
    private Integer clickNumber;

    /**
     * 点击率
     */
    @Column(name = "click_rate")
    private Double clickRate;

    /**
     * 预估收益
     */
    private Double profit;

    /**
     * 千展收益
     */
    private Double cpm;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private java.util.Date createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private java.util.Date updatedAt;

    /**
     * 规则ID
     */
    @Column(name = "rule_id")
    private Integer ruleId;

    /**
     * 广告位  规则表
     */
    @Column(name = "rule_ad_space")
    private String ruleAdSpace;

    /**
     * app名 处理后的数据
     */
    @Column(name = "cut_app_name")
    private String cutAppName;

    /**
     * 客户端：1安卓 2ios
     */
    @Column(name = "client_id")
    private Integer clientId;

    /**
     * 1穿山甲 2广点通 3快手
     */
    @Column(name = "plat_form")
    private Integer platForm;

    /**
     * 广告样式
     */
    @Column(name = "ad_style")
    private String adStyle;

    /**
     * 广告类型
     */
    @Column(name = "ad_type")
    private String adType;

    /**
     * app类型:1视频，2炫来电
     */
    @Column(name = "app_type")
    private Integer appType;

    /**
     * 总名称 banner_rule表的
     */
    @Column(name = "total_name")
    private String totalName;

    /**
     * 预估收益/点击数 profit/click_number
     */
    @Column(name = "click_price")
    private Double clickPrice;

    /**
     * 日活
     */
    private Integer active = 0;

    /**
     *  人均展现数=展现量/日活
     */
    @Column(name = "pre_show_num")
    private Double preShowNum = 0.0;

    /**
     * 日活价值=预估收益/日活
     */
    @Column(name = "active_value")
    private String activeValue = "0";

    /**
     * 广告位请求量
     */
    @Column(name = "ad_space_request")
    private Integer adSpaceRequest = 0;

    /**
     * 广告位返回
     */
    @Column(name = "ad_space_return")
    private Integer adSpaceReturn = 0;

    /**
     * 广告位填充率(广告位返回量/广告位请求量) ps:有乘100
     */
    @Column(name = "ad_space_filling")
    private Double adSpaceFilling = 0.0;

    /**
     * 广告请求量
     */
    @Column(name = "ad_request")
    private Integer adRequest = 0;

    /**
     * 广告返回量
     */
    @Column(name = "ad_return")
    private Integer adReturn = 0;

    /**
     * 广告填充率（广告返回量/广告请求量） ps:有乘100
     */
    @Column(name = "ad_filling")
    private Double adFilling = 0.0;

    /**
     * 曝光率
     */
    @Column(name = "exposure_rate")
    private Double exposureRate = 0.0;

    /**
     * 报错率
     */
    @Column(name = "error_rate")
    private Double errorRate = 0.0;

    private String earnings;

    @Column(name = "err_num")
    private Integer errNum = 0;

    @Column(name = "err_rate")
    private Double errRate = 0.0;

}
