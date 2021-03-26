package com.miguan.xuanyuan.dto.request;

import com.miguan.xuanyuan.entity.XyUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author kangxuening
 * @since 2021-03-09
 */
@Data
@ApiModel("后台强制重置密码")
public class ResetPasswordBackRequest {

    @ApiModelProperty(value = "密码")
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)(?!([^(0-9a-zA-Z)])+$).{8,20}$" , message="密码格式错误")
    private String password;

    @ApiModelProperty(value = "确认密码")
    @NotBlank(message = "确认密码不能为空")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)(?!([^(0-9a-zA-Z)])+$).{8,20}$" , message="确认密码格式错误")
    private String passwordRe;

    @ApiModelProperty(value = "用户id")
    @NotNull(message = "用户id不能为空")
    private Long userId;

}
