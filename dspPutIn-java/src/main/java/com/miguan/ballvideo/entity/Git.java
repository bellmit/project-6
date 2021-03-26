package com.miguan.ballvideo.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "cl_test")
@Getter
@Setter
@ApiModel("用户信息")
public class Git {

    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("用户名")
    @Column(name = "login_name")
    private String loginName;

    @ApiModelProperty("密码")
    @Column(name = "login_pwd")
    private String loginPwd;
}
