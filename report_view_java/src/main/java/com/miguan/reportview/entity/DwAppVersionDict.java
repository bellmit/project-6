package com.miguan.reportview.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * APP版本配置表
 * </p>
 *
 * @author zhongli
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DwAppVersionDict implements Serializable {

    private static final long serialVersionUID = 1L;

    private String appVersion;


}
