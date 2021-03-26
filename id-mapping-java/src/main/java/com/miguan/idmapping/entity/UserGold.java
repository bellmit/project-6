package com.miguan.idmapping.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户金币信息表
 * </p>
 *
 * @author jobob
 * @since 2020-07-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserGold implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 总金币
     */
    private Integer totalGold;

    /**
     * 已用金币
     */
    private Integer usedGold;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * ios内购套餐过期时间
     */
    private LocalDateTime iosExpireDay;

    /**
     * 第一季度金币(过期时间6月1日 00:00:00)
     */
    private Integer firstGold;

    /**
     * 第二季度金币(过期时间9月1日 00:00:00)
     */
    private Integer secondGold;

    /**
     * 第三季度金币(过期时间12月1日 00:00:00)
     */
    private Integer thirdGold;

    /**
     * 第四季度金币(过期时间次年3月1日 00:00:00)
     */
    private Integer fourthGold;

    /**
     * 应用类别：xysp-茜柚视频，ggsp-果果视频
     */
    private String appType;

    private String appPackage;

    /**
     * 微信openId
     */
    private String  openId;

    /**
     * 苹果用户ID（苹果登录时使用）
     */
    private String  appleId;
}
