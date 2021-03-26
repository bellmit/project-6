package com.miguan.xuanyuan.dto.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.enums.PermissionTypeEnum;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.entity.AdminPermission;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author kangxuening
 * @since 2021-03-02
 */
@Data
@ApiModel("权限")
public class AdminPermissionRequest {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称（目录、菜单或者操作等）")
    @NotBlank(message = "名称不能为空")
    @Size(max = 50,message = "名称不能超过50个字符")
    private String name;

    @ApiModelProperty(value = "权限标识")
    @NotBlank(message = "权限标识不能为空")
    @Size(max = 50,message = "权限标识不能超过50个字符")
    private String actionCode;

    @ApiModelProperty(value = "地址")
    @Size(max = 255,message = "地址不能超过255个字符")
    private String url;

    @ApiModelProperty(value = "项目id")
    @NotNull(message = "项目id不能为空")
    private Integer projectId;

    @ApiModelProperty(value = "父id")
    @NotNull(message = "父id不能为空")
    private Integer parentId;

    @ApiModelProperty(value = "类型")
    @NotNull(message = "类型不能为空")
    private Integer type;

    @ApiModelProperty(value = "svg图标名称")
    @Size(max = 50,message = "svg图标名称不能超过50个字符")
    private String svgName;

    @ApiModelProperty(value = "是否展示，1展示，0隐藏")
    private Integer isShow;

    @ApiModelProperty(value = "排序")
    private Integer orderNum;

    @ApiModelProperty(value = "状态，1正常，0禁用")
    private Integer status;


    public void checkAdd() throws ValidateException {
        if (url == null) {
            url = "";
        }

        if (svgName == null) {
            svgName = "";
        }

        if (orderNum == null) {
            orderNum = 0;
        }

        if (status == null) {
            status = XyConstant.STATUS_NORMAL;
        }

        if (status != XyConstant.STATUS_NORMAL && status != XyConstant.STATUS_CLOSE) {
            throw new ValidateException("status数据错误");
        }

        if (!PermissionTypeEnum.exist(type)) {
            throw new ValidateException("类型type数据错误");
        }

        if (isShow != null && (isShow != XyConstant.IS_SHOW && isShow != XyConstant.UN_SHOW)) {
            throw new ValidateException("isShow数据错误");
        }
    }


    public void checkEdit() throws ValidateException {
        if (url == null) {
            url = "";
        }

        if (svgName == null) {
            svgName = "";
        }

        if (orderNum == null) {
            orderNum = 0;
        }

        if (status == null) {
            status = XyConstant.STATUS_NORMAL;
        }

        if (id == null) {
            throw new ValidateException("id不能为空");
        }

        if (status != XyConstant.STATUS_NORMAL && status != XyConstant.STATUS_CLOSE) {
            throw new ValidateException("status数据错误");
        }

        if (!PermissionTypeEnum.exist(type)) {
            throw new ValidateException("类型type数据错误");
        }

        if (isShow != null && (isShow != XyConstant.IS_SHOW && isShow != XyConstant.UN_SHOW)) {
            throw new ValidateException("isShow数据错误");
        }
    }

    /**
     * 转化类型
     *
     * @return
     */
    public AdminPermission convertAdminPermission() {
        AdminPermission adminPermission = new AdminPermission();
        BeanUtils.copyProperties(this, adminPermission);
        return adminPermission;
    }


}
