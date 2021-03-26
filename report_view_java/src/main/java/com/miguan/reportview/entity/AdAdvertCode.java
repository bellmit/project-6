package com.miguan.reportview.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 广告代码位表
 * </p>
 *
 * @author zhongli
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AdAdvertCode implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 代码位ID*代码位ID：第三方或98广告后台生成的广告ID
     */
    private String adId;

    /**
     * 广告样式
     */
    private String advCss;

    /**
     * 应用包1*应用包
     */
    private String appPackage;

    /**
     * 渠道类型：1-全部 2-仅限 3-排除
     */
    private Integer channelType;

    /**
     * 所有：-1，一个或者多个逗号隔开
     */
    private String channelIds;

    /**
     * 广告素材，对应表ad_meterial
     */
    private String materialKey;

    /**
     * 是否需要权限 0否 1是
     */
    private String permission;

    /**
     * 广告平台，对应表ad_plat
     */
    private String platKey;

    /**
     * 使用SDK，对应表ad_sdk
     */
    private String sdkKey;

    /**
     * 广告类型,对应表ad_type
     */
    private String typeKey;

    /**
     * 版本区间1
     */
    private String version1;

    /**
     * 版本区间2
     */
    private String version2;

    /**
     * 渲染
     */
    private String renderKey;

    /**
     * 阶梯价格
     */
    private String ladderPrice;

    /**
     * 1阶梯，2非阶梯*1阶梯，2非阶梯
     */
    private Integer ladder;

    /**
     * 是否投放，1已投放，2未投放
     */
    private Integer putIn;

    private Integer state;

    /**
     * 视频来源。98du，wuli
     */
    private String videosSource;


}
