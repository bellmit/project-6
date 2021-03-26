package com.miguan.reportview.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 应用总成本(app_cost)实体类
 *
 * @author zhongli
 * @since 2020-08-07 18:40:02
 * @description
 */
@Data
@NoArgsConstructor
@TableName("app_cost")
public class AppCost implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 日期
     */
    private Date date;
    /**
     * 总成本
     */
    private BigDecimal cost;
    /**
     * app表的id
     */
    private Integer appId;
    /**
     * app 名称
     */
    private String appName;
    /**
     * 1安卓 ， 2ios
     */
    private Integer clientId;
    /**
     * app类型:1视频，2炫来电
     */
    private Integer appType;

}