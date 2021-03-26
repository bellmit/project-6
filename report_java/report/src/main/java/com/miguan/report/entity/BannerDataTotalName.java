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
 * jpa entity class for banner_data_total_name
 * 数据汇总
 */
@Data
@Entity
@Table(name = "banner_data_total_name")
public class BannerDataTotalName implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /**
     * 汇总数据
     */
    private Integer id;

    /**
     * 日期
     */
    private java.util.Date date;

    /**
     * 展现量
     */
    @Column(name = "show_number")
    private Integer showNumber;

    /**
     * 展现配比
     */
    @Column(name = "show_rate")
    private Double showRate = 0.0;

    /**
     *  展现占比
     */
    @Column(name = "show_rate_occupy")
    private Double showRateOccupy = 0.0;

    /**
     * 点击量
     */
    @Column(name = "click_number")
    private Integer clickNumber;

    /**
     * 点击率=点击量/展现量
     */
    @Column(name = "click_rate")
    private Double clickRate;

    /**
     * 点击单价=营收/点击量
     */
    @Column(name = "click_price")
    private Double clickPrice;

    /**
     * cpm=营收/展现量*1000
     */
    private Double cpm;

    /**
     * 营收
     */
    private Double revenue;

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
     * 日活价值=营收/日活
     */
    @Column(name = "active_value")
    private String activeValue = "0";

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
     * app 名称
     */
    @Column(name = "app_name")
    private String appName;

    /**
     * app表自增 id
     */
    @Column(name = "app_id")
    private String appId;

    /**
     * 1安卓 ， 2ios
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
     * 广告位置规则 总名称(total_name)
     */
    @Column(name = "ad_space")
    private String adSpace;

    /**
     * app类型:1视频，2炫来电 3快手
     */
    @Column(name = "app_type")
    private Integer appType;

    /**
     * md5(date + total_name+ app_name + client_id +  plat_form)
     */
    @Column(name = "unique_key")
    private String uniqueKey;

}
