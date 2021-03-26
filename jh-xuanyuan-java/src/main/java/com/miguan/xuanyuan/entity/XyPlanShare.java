package com.miguan.xuanyuan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 广告计划分享表
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XyPlanShare对象", description="广告计划分享表")
public class XyPlanShare extends Model<XyPlanShare> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "自增主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "计划id")
    private Long planId;

    @ApiModelProperty(value = "过期日期")
    private LocalDateTime expiredAt;

    @ApiModelProperty(value = "操作用户id")
    private Long operateUserId;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdAt;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
