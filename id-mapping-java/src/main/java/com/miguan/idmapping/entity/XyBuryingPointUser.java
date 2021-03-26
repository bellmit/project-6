package com.miguan.idmapping.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author jobob
 * @since 2020-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class XyBuryingPointUser implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 初始化渠道
     */
    private String channelId;

    /**
     * 作用包
     */
    private String appPackage;


}
