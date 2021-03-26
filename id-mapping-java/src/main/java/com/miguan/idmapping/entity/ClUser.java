package com.miguan.idmapping.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author jobob
 * @since 2020-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ClUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 昵称
     */
    private String name;

    /**
     * 账号（手机号）
     */
    private String loginName;

    /**
     * 设备ID
     */
    private String deviceId;

    private String imgUrl;

    /**
     * 签名
     */
    private String sign;

    /**
     * 状态（10 正常 20 禁用）
     */
    private String state;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 友盟对设备的唯一标识
     */
    private String deviceToken;

    /**
     * 华为对设备的唯一标识1
     */
    private String huaweiToken;

    /**
     * vivo对设备的唯一标识
     */
    private String vivoToken;

    /**
     * oppo对设备的唯一标识
     */
    private String oppoToken;

    /**
     * 小米对设备的唯一标识
     */
    private String xiaomiToken;

    /**
     * 微信openid
     */
    private String openId;

    /**
     * 包名
     */
    private String appPackage;

    /**
     * 0 真实用户  1 虚拟用户
     */
    private Integer virtualUser;

    /**
     * 总金币
     */
    private Integer totalGold;

    /**
     * 已用金币
     */
    private Integer usedGold;


}
