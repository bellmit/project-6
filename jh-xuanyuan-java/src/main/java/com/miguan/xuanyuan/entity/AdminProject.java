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
 * 项目表
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="AdminProject对象", description="项目表")
public class AdminProject extends Model<AdminProject> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "项目名称")
    private String name;

    @ApiModelProperty(value = "域名")
    private String host;

    @ApiModelProperty(value = "排序")
    private Integer orderNum;

    @ApiModelProperty(value = "状态，1启用，0禁用")
    private Integer status;

    @ApiModelProperty(value = "是否显示，1显示，0隐藏")
    private Integer isShow;

    @ApiModelProperty(value = "添加时间")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updatedAt;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
