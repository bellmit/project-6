package com.miguan.advert.domain.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel("新增账户信息")
@Data
public class AccountAddVo {

    @NotBlank(message = "账户名不能为空")
    @ApiModelProperty("账户名")
    private String name;

    @NotBlank(message = "公司名不能为空")
    @ApiModelProperty("公司名")
    private String company_name;

    @NotBlank(message = "电话号码不能为空")
    @ApiModelProperty("电话号码")
    private String phone_num;

    @NotNull(message = "状态不能为空")
    @ApiModelProperty("状态")
    private Integer status;
}