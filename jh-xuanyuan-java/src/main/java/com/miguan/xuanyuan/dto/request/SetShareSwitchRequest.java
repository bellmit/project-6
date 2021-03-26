package com.miguan.xuanyuan.dto.request;

import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.entity.XyUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author kangxuening
 * @since 2021-03-16
 */
@Data
@ApiModel("设置数据报表分享开关请求")
public class SetShareSwitchRequest {

    @ApiModelProperty(value = "开关，1打开，0关闭")
    @NotNull(message = "shareSwitch不能为空")
    private Integer shareSwitch;



}
