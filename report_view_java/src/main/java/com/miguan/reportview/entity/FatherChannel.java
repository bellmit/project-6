package com.miguan.reportview.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 父渠道维表
 *
 * @author linliqing
 * @since 2020-10-28 18:40:02
 * @description
 */
@Data
@NoArgsConstructor
@TableName("dim_father_channel")
public class FatherChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private long id;

    /**
     * 父渠道
     */
    private String fatherChannel;

    /**
     * 负责人
     */
    private String owner;

    /**
     * 返点
     */
    private Double rebate;



    /**
     * 状态
     */
    private int status;

    /**
     * 状态修改时间
     */
    private String statusTime;
}
