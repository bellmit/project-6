package com.miguan.reportview.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 广告位置表（广告相关表统一ad_开头）
 * </p>
 *
 * @author zhongli
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AdAdvertPosition implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    private long id;
    /**
     * 500*包名
     */
    private String appPackage;

    /**
     * 首次加载*首次加载
     */
    private Integer firstLoadPosition;

    /**
     * 展示次数限制*展示次数限制
     */
    private Integer maxShowNum;

    /**
     * 手机类型1*应用端:1-ios，2-安卓
     */
    private String mobileType;

    /**
     * 广告位名称
     */
    private String name;

    /**
     * 关键字*
     */
    private String positionType;

    /**
     * 再加载*再次加载
     */
    private Integer secondLoadPosition;

    /**
     * 创建时间*创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 修改时间
     */
    private LocalDateTime updatedAt;

    /**
     * 广告位总称
     */
    private String totalName;

    /**
     * 报表展示名称
     */
    private String reportShowName;


}
