package com.miguan.advert.domain.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("账户列表信息")
@Data
public class AccountInfoVo {

    @ApiModelProperty("账户Id")
    private Integer account_id;

    @ApiModelProperty("公司名称")
    private String company_name;

    @ApiModelProperty("账户名称")
    private String name;
}