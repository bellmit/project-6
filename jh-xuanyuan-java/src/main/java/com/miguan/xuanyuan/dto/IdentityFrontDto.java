package com.miguan.xuanyuan.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description 媒体账号信息（前台）
 * @Author zhangbinglin
 * @Date 2021/2/27 16:56
 **/
@Data
@ApiModel(value="媒体账号信息（前台）", description="媒体账号信息（前台）")
public class IdentityFrontDto {

    @ApiModelProperty(value = "id", position = 10)
    private Integer id;

    @ApiModelProperty(value = "用户id", position = 20)
    private Integer userId;

    @ApiModelProperty(value = "账号名称", position = 21)
    private String username;

    @ApiModelProperty(value = "注册手机号", position = 21)
    private String phone;

    @ApiModelProperty(value = "邮箱", position = 30)
    private String email;

    @ApiModelProperty(value = "联系电话", position = 40)
    private String contactPhone;

    @ApiModelProperty(value = "微信号", position = 50)
    private String wechat;

    @ApiModelProperty(value = "用户类型，1企业，2个人", position = 60)
    private Integer type = 1;

    @ApiModelProperty(value = "企业名称或个人名字", required = true, position = 70)
    @NotBlank(message = "企业名称或个人名字不能为空")
    private String name;

    @ApiModelProperty(value = "营业执照/个人身份证", required = true, position = 80)
    @NotBlank(message = "营业执照/个人身份证不能为空")
    private String certificateImg;

    @ApiModelProperty(value = "状状态，0未认证，1已认证，2待审核，3已禁用", position = 90)
    private Integer status = 0;
}
