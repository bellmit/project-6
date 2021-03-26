package com.miguan.xuanyuan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Data
@ApiModel("用户表")
@Entity(name = "xy_user")
@TableName("xy_user")
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户账号")
    @Column(name = "username")
    private String username;

    @ApiModelProperty("密码")
    @Column(name = "password")
    private String password;

    @ApiModelProperty("注册手机号")
    @Column(name = "phone")
    private String phone;

    @ApiModelProperty("用户类型，1表示媒体用户，2表示公司用户")
    @Column(name = "user_type")
    private Integer userType;

    @ApiModelProperty("昵称")
    @Column(name = "nickname")
    private String nickname;

    @ApiModelProperty("头像")
    @Column(name = "avatar_url")
    private String avatarUrl;

    @ApiModelProperty("角色id")
    @Column(name = "role_id")
    private Integer roleId;

    @ApiModelProperty("用户类型")
    @Column(name = "type")
    private Integer type;

    @ApiModelProperty("状态")
    @Column(name = "status")
    private Short status;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @Column(name = "created_at")
    private Date createdAt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @Column(name = "updated_at")
    private Date updatedAt;
}
