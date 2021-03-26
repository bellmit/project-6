package com.miguan.xuanyuan.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description 菜单权限列表dto
 * @Author zhangbinglin
 * @Date 2021/3/12 16:45
 **/
@Data
public class PermissionListDto {

    @ApiModelProperty(value = "id")
    private Integer id;

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

    @ApiModelProperty(value = "类型：0平台，1目录，2菜单，3功能")
    private Integer type;

    @ApiModelProperty(value = "svg图标名称")
    private String svgName;

    @ApiModelProperty(value = "是否展示，1展示，0隐藏")
    private Integer isShow;

//    @ApiModelProperty(value = "排序")
//    private Integer orderNum;

    @ApiModelProperty(value = "状态，1正常，0禁用")
    private Integer status;

//    @ApiModelProperty(value = "是否存在子节点")
//    private Integer hasChildren;

    @ApiModelProperty(value = "子菜单列表")
    private List<PermissionListDto> childrenList;
}
