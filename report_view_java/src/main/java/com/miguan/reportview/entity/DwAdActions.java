package com.miguan.reportview.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * jpa entity class for dw_ad_actions
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DwAdActions implements Serializable {

    private LocalDate dd;

    private Integer dh;

    private String uuid;

    private String adKey;

    private String adId;

    private String adSource;

    private String adType;

    private String renderType;

    private String packageName;

    private String appVersion;

    private String channel;

    private String changeChannel;

    private Integer isNew;

    private Integer isNewApp;

    private String model;

    private String distinctId;

    private java.util.Date receiveTime;

    private java.util.Date creatTime;

    private String country;

    private String province;

    private String city;

    private Integer request;

    private Integer response;

    private Integer render;

    private Integer show;

    private Integer click;

}
