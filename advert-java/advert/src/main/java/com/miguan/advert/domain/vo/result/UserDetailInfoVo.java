package com.miguan.advert.domain.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("用户信息")
@Data
public class UserDetailInfoVo {

    @ApiModelProperty("用户Id")
    private Integer id;

    @ApiModelProperty("头像地址")
    private String avatar_url;

    @ApiModelProperty("邮件")
    private String email;

    @ApiModelProperty("手机号码")
    private String mobile;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("状态:0=停用,1=启用")
    private String status;

    @ApiModelProperty("token")
    private String token;

    @ApiModelProperty("创建时间")
    private String created_at;

    @ApiModelProperty("修改时间")
    private String updated_at;
}