package com.miguan.xuanyuan.dto.request;

import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.enums.PermissionTypeEnum;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.entity.AdminPermission;
import com.miguan.xuanyuan.entity.XyUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.*;

/**
 * @author kangxuening
 * @since 2021-03-04
 */
@Data
@ApiModel("用户注册请求")
public class UserRegisterRequest {

    @ApiModelProperty(value = "用户名")
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]{6,20}$", message="用户名格式错误")
    private String username;

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

    @ApiModelProperty(value = "类型")
    @NotNull(message = "type不能为空")
    private Integer type;




    /**
     * 转化类型
     *
     * @return
     */
    public XyUser convertUserEntity() {
        XyUser xyUser = new XyUser();
        xyUser.setUsername(this.username);
        xyUser.setPhone(this.phone);
        xyUser.setNickname("");
        xyUser.setAvatarUrl("");
        xyUser.setRoleId(0);
        xyUser.setUserType(XyConstant.USER_TYPE_MEDIA);
        xyUser.setType(this.type);
        xyUser.setStatus(XyConstant.STATUS_NORMAL);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode(this.password);
        xyUser.setPassword(password);

        return xyUser;
    }



}
