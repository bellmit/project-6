package com.miguan.reportview.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 每分钟汇总表
 * </p>
 *
 * @author zhongli
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RpTotalMinute implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日期，如20200801
     */
    private Date dd;

    /**
     * 分钟，如202008012130
     */
    private Long dm;

    /**
     * 显示分钟数，例如：01:30
     */
    private String showMinute;

    /**
     * 活跃用户数
     */
    private Integer user;

    /**
     * 新增用户数
     */
    private Integer newUser;

    /**
     * 播放数
     */
    private Integer playCount;

    /**
     *曝光数
     */
    private Integer showCount;

    /**
     * 有效播放数
     */
    private Integer validPlayCount;

    /**
     * 完播数
     */
    private Integer endPlayCount;

    /**
     * 播放用户数
     */
    private Integer playUser;

    /**
     * 有效播放用户数
     */
    private Integer validPlayUser;

    /**
     * 完播用户数
     */
    private Integer endPlayUser;

    /**
     * 总播放时长，毫秒为单位
     */
    private Long playTimeTotal;

    /**
     * 广告曝光量
     */
    private Integer adShowCount;

    /**
     * 广告点击量
     */
    private Integer adClickCount;

    /**
     * 广告曝光用户数
     */
    private Integer adShowUser;

    /**
     * 广告点击用户数
     */
    private Integer adClickUser;

    /**
     * 应用总使用时长，毫秒为单位
     */
    private Long appTimeTotal;

    /**
     * 应用实际使用时长，毫秒为单位
     */
    private Long appTimeTotalR;
}
