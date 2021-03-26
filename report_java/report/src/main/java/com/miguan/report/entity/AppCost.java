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
 * jpa entity class for app_cost
 * 应用总成本
 */
@Data
@Entity
@Table(name = "app_cost")
public class AppCost implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT")
    /**
     * 主键
     */
    private Long id;

    /**
     * 日期
     */
    private java.util.Date date;

    /**
     * 总成本
     */
    private Double cost;
    /**
     * app表的id
     */
    @Column(name = "app_id")
    private Integer appId;
    /**
     * app 名称
     */
    @Column(name = "app_name")
    private String appName;

    /**
     * 1安卓 ， 2ios
     */
    @Column(name = "client_id")
    private Integer clientId;

    /**
     * app类型:1视频，2炫来电
     */
    @Column(name = "app_type")
    private Integer appType;

    /**
     * 投放渠道-父渠道
     */
    @Column(name = "channel")
    private String channel;

}
