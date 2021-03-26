package com.miguan.xuanyuan.dto.request;

import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.entity.XyUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author kangxuening
 * @since 2021-03-04
 */
@Data
@ApiModel("重置密码")
public class ResetPasswordRequest {

    @ApiModelProperty(value = "密码")
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)(?!([^(0-9a-zA-Z)])+$).{8,20}$" , message="密码格式错误")
    private String password;

    @ApiModelProperty(value = "确认密码")
    @NotBlank(message = "确认密码不能为空")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)(?!([^(0-9a-zA-Z)])+$).{8,20}$" , message="确认密码格式错误")
    private String passwordRe;

    @ApiModelProperty(value = "手机号码")
    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[0-9]{10}$", message="手机号格式错误")
    private String phone;

    @ApiModelProperty(value = "短信验证码")
    @NotBlank(message = "短信验证码不能为空")
    @Pattern(regexp = "^[0-9]{6}$")
    private String verifiCode;


    /**
     * 转化类型
     *
     * @return
     */
    public XyUser convertUserEntity() {
        XyUser xyUser = new XyUser();
//        xyUser.setUsername(this.username);
//        xyUser.setPhone(this.phone);
//        xyUser.setNickname("");
//        xyUser.setAvatarUrl("");
//        xyUser.setRoleId(0);
//        xyUser.setUserType(XyConstant.USER_TYPE_MEDIA);
//        xyUser.setType(0);
//        xyUser.setStatus(XyConstant.STATUS_NORMAL);
//
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        String password = passwordEncoder.encode(this.password);
//        xyUser.setPassword(password);

        return xyUser;
    }



}
