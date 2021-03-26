package com.miguan.reportview.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 视频数据宽表
 * </p>
 *
 * @author zhongli
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DwVideoActions implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate dd;

    private Integer dh;

    private String uuid;

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

    private Integer show;

    private Integer playStart;

    private Integer playEnd;

    private Integer videoTime;

    private Integer playTime;

    private Double playRate;

    private Integer isPlayValid;

    private Integer isPlayEnd;

    private Integer comment;

    private Integer praise;

    private Integer collect;


}
