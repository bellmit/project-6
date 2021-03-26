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
 * 权限表
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="AdminPermission对象", description="权限表")
public class AdminPermission extends Model<AdminPermission> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "名称（目录、菜单或者操作等）")
    private String name;

    @ApiModelProperty(value = "权限标识")
    private String actionCode;

    @ApiModelProperty(value = "地址")
    private String url;

    @ApiModelProperty(value = "项目id")
    private Integer projectId;

    @ApiModelProperty(value = "父id")
    private Integer parentId;

    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "svg图标名称")
    private String svgName;

    @ApiModelProperty(value = "是否展示，1展示，0隐藏")
    private Integer isShow;

    @ApiModelProperty(value = "排序")
    private Integer orderNum;

    @ApiModelProperty(value = "状态，1正常，0禁用")
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
