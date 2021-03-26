package com.miguan.xuanyuan.vo;

import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.enums.PermissionTypeEnum;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.entity.AdminPermission;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author kangxuening
 * @since 2021-03-02
 */
@Data
@ApiModel("权限")
public class AdminPermissionVo {

    @ApiModelProperty(value = "id")
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

    @ApiModelProperty(value = "是否存在子节点")
    private Integer hasChildren;


}
