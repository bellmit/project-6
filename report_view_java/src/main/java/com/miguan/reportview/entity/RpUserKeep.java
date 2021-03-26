package com.miguan.reportview.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 用户留存数据表
 * </p>
 *
 * @author zhongli
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RpUserKeep implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private long id;
    /**
     * 日期，如20200801
     */
    private Integer dd;

    /**
     * 应用包名，不使用该维度使用null
     */
    private String packageName;

    /**
     * 应用版本，不使用该维度使用null
     */
    private String appVersion;

    private String fatherChannel;
    /**
     * 更新渠道，不使用该维度使用null
     */
    private String changeChannel;

    /**
     * 是否新设备 1-是，0-否，不使用该维度为null
     */
    private Integer isNew;
    private Integer type;
    private Integer sd;

    /**
     * 用户数
     */
    private Integer user;

    /**
     * 次日留存
     */
    private Integer keep1;

    /**
     * 2日留存
     */
    private Integer keep2;

    /**
     * 3日留存
     */
    private Integer keep3;

    /**
     * 4日留存
     */
    private Integer keep4;

    /**
     * 5日留存
     */
    private Integer keep5;

    /**
     * 6日留存
     */
    private Integer keep6;

    /**
     * 7日留存
     */
    private Integer keep7;

    /**
     * 14日留存
     */
    private Integer keep14;

    /**
     * 30日留存
     */
    private Integer keep30;

    /**
     * app类型：1=视频，2=来电
     */
    private Integer appType;

}
