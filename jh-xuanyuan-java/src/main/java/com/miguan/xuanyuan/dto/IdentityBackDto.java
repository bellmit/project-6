package com.miguan.xuanyuan.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description 媒体账号信息（前台）
 * @Author zhangbinglin
 * @Date 2021/2/27 16:56
 **/
@Data
@ApiModel(value="媒体账号信息（后台）", description="媒体账号信息（后台）")
public class IdentityBackDto {

    @ApiModelProperty(value = "用户id", position = 10)
    private Integer id;

    @ApiModelProperty(value = "用户认证信息id", position = 20)
    private Integer identityId;

    @ApiModelProperty(value = "媒体账号名称", position = 21)
    @NotBlank(message = "媒体账号名称不能为空")
    private String username;

    @ApiModelProperty(value = "注册手机号", position = 22)
    @NotBlank(message = "注册手机号不能为空")
    private String phone;

    @ApiModelProperty(value = "邮箱", position = 30)
    private String email;

    @ApiModelProperty(value = "联系电话", position = 40)
    private String contactPhone;

    @ApiModelProperty(value = "微信号", position = 50)
    private String wechat;

    @ApiModelProperty(value = "用户类型，1企业，2个人", position = 60)
    private Integer type;

    @ApiModelProperty(value = "企业名称或个人名字", position = 70)
    @NotBlank(message = "企业名称或个人名字不能为空")
    private String name;

    @ApiModelProperty(value = "营业执照/个人身份证", position = 80)
    @NotBlank(message = "营业执照/个人身份证不能为空")
    private String certificateImg;

    @ApiModelProperty(value = "媒体角色，1：V1黑盒模式，2:V2白盒模式", position = 90)
    @NotNull(message = "媒体角色不能为空")
    private Integer roleType;

    @ApiModelProperty(value = "媒体分成比例，正整数，不能大于100", position = 100)
    @NotNull(message = "媒体分成比例不能为空")
    private Integer profitRate;

    @ApiModelProperty(value = "平台分成比例，正整数，不能大于100", position = 101)
    @NotNull(message = "平台分成比例不能为空")
    private Integer platRate;

    @ApiModelProperty(value = "银行卡户名", position = 110)
    private String cardName;

    @ApiModelProperty(value = "银行卡号", position = 120)
    private String cardNumber;

    @ApiModelProperty(value = "开户银行", position = 130)
    private String bank;

    @ApiModelProperty(value = "财务联系方式", position = 140)
    private String contactInfo;

    @ApiModelProperty(value = "状状态，0未认证，1已认证，2待审核，3已禁用", position = 150)
    private Integer status;
}
