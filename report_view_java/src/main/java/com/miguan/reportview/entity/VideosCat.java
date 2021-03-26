package com.miguan.reportview.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 视频类别表
 * </p>
 *
 * @author zhongli
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class VideosCat implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private long id;
    /**
     * 分类名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 权重
     */
    private Integer weight;

    /**
     * 类型 10首页视频 20 小视频
     */
    private String type;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 状态 2关闭 1开启
     */
    private Integer state;

    /**
     * 后台状态 2关闭 1开启
     */
    private Integer adminState;

    /**
     * 客户端
     */
    private String dataColumn;

    /**
     * 归属应用：用包名区分，多个用逗号隔开
     */
    private String ascriptionApplication;

    /**
     * 后端备注
     */
    private String adminRemarks;


}
