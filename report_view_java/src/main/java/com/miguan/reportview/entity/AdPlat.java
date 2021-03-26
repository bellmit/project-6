package com.miguan.reportview.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 广告平台
 * </p>
 *
 * @author zhongli
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AdPlat implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 广告名称
     */
    private String advName;

    /**
     * 广告平台标识
     */
    private String platKey;

    /**
     * 备注
     */
    private String remark;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}