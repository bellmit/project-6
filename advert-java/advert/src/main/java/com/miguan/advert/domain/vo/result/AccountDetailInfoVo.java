package com.miguan.advert.domain.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("账户列表信息")
@Data
public class AccountDetailInfoVo {

    @ApiModelProperty("账户Id")
    private Integer id;

    @ApiModelProperty("应用个数")
    private Integer app_num;

    @ApiModelProperty("公司名称")
    private String company_name;

    @ApiModelProperty("包名是否必须唯一：0：否，1：是")
    private Integer is_package_only;

    @ApiModelProperty("最后操作用户")
    private String last_operate_admin;

    @ApiModelProperty("账户名称")
    private String name;

    @ApiModelProperty("应用包")
    private String packages;

    @ApiModelProperty("电话号码")
    private String phone_num;

    @ApiModelProperty("状态：0：关闭，1：启用")
    private Integer status;

    @ApiModelProperty("创建时间")
    private String create_time;

    @ApiModelProperty("更新时间")
    private String update_time;
}