package com.miguan.reportview.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhongli
 * @date 2020-08-04 
 *
 */
@Setter
@Getter
public class RealTimeStaVo {
    private Integer newUser;
    private Integer suser;
    private Integer playUser;
    private Integer validPlayUser;
    private Integer adShowUser;
    private Integer adClickUser;
    private Integer playCount;
    private Integer validPlayCount;
    private Integer adShowCount;

    private double endPlayUser;
    private double vperPlayCount;
    private double perPlayTime;
    private double adClickCount;
    private double adClickRate;
    private double perAdShowCount;
    private double perAdclickCount;
    private double preShowTime;
    private double prePlayTime;
    /**
     * 单个统计使用的属性
     */
    private Double showValue;
    private String dd;
    private String dh;
    private String showMinute;
}
