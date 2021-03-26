package com.miguan.xuanyuan.dto.request;

import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.enums.PermissionTypeEnum;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.entity.AdminPermission;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.*;

/**
 * @author kangxuening
 * @since 2021-03-03
 */
@Data
@ApiModel("权限状态")
public class AdminPermissionStatusRequest {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    @Min(1)
    private Long id;

    @ApiModelProperty(value = "状态，1正常，0禁用")
    @Min(0)
    @Max(1)
    @NotNull(message = "status不能为空")
    private Integer status;

}
