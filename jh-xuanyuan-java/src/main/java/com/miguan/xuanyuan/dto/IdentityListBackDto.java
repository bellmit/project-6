package com.miguan.xuanyuan.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 媒体账号信息（前台）
 * @Author zhangbinglin
 * @Date 2021/2/27 16:56
 **/
@Data
@ApiModel(value="媒体账号列表信息（后台）", description="媒体账号列表信息（后台）")
public class IdentityListBackDto {

    @ApiModelProperty(value = "用户id", position = 10)
    private Integer id;

    @ApiModelProperty(value = "用户认证信息id", position = 20)
    private Integer identityId;

    @ApiModelProperty(value = "用户名称", position = 21)
    private String username;

    @ApiModelProperty(value = "注册手机号", position = 30)
    private String phone;

    @ApiModelProperty(value = "用户类型，1企业，2个人", position = 60)
    private Integer type;

    @ApiModelProperty(value = "企业名称或个人名字", position = 70)
    private String name;

    @ApiModelProperty(value = "媒体角色，1：V1黑盒模式，2:V2白盒模式", position = 90)
    private Integer roleType;

    @ApiModelProperty(value = "媒体分成比例，正整数，不能大于100", position = 100)
    private Integer profitRate;

    @ApiModelProperty(value = "平台分成比例，正整数，不能大于100", position = 101)
    private Integer platRate;

    @ApiModelProperty(value = "注册时间", position = 140)
    private String registerTime;

    @ApiModelProperty(value = "状状态，0未认证，1已认证，2待审核，3已禁用", position = 150)
    private Integer status;
}
