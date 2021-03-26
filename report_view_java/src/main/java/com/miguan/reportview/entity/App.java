package com.miguan.reportview.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 应用
 * </p>
 *
 * @author zhongli
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class App implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private long id;
    /**
     * app名称
     */
    private String name;

    /**
     * 手机类型：1-ios 2-安卓
     */
    private Integer mobileType;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 所属应用
     */
    private String appPackage;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}
