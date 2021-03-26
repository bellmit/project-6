package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("用户信息")
public class GitVo {

    @ApiModelProperty("用户名")
    private long id;

    @ApiModelProperty("用户名")
    private String loginName;

    @ApiModelProperty("密码")
    private String loginPwd;
}
