package com.miguan.idmapping.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表(cl_device)实体类
 *
 * @author zhongli
 * @since 2020-09-01 11:40:54
 * @description 
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("cl_device")
public class ClDevice extends Model<ClDevice> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
	private Long id;
    /**
     * 设备ID
     */
    private String deviceId;
    /**
     * 状态（10 正常 20 禁用）
     */
    private String state;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField(update = "now()")
	private Date updateTime;
    /**
     * 友盟对设备的唯一标识
     */
    private String deviceToken;
    /**
     * 华为对设备的唯一标识
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
     * 包名
     */
    private String appPackage;

    /**
     * distinctIds
     */
    private String distinctId;
}