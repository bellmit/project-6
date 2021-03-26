package com.miguan.xuanyuan.dto.request;

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
@ApiModel("前台修改登录密码")
public class ResetPasswordFrontRequest {


    @ApiModelProperty(value = "原始密码")
    @NotBlank(message = "原始密码不能为空")
//    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)(?!([^(0-9a-zA-Z)])+$).{8,20}$" , message="原始密码格式错误")
    private String originalPassword;

    @ApiModelProperty(value = "密码")
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)(?!([^(0-9a-zA-Z)])+$).{8,20}$" , message="密码格式错误")
    private String password;

    @ApiModelProperty(value = "确认密码")
    @NotBlank(message = "确认密码不能为空")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)(?!([^(0-9a-zA-Z)])+$).{8,20}$" , message="确认密码格式错误")
    private String passwordRe;

}
