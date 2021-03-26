package com.miguan.xuanyuan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 账号认证表
 * </p>
 *
 * @author zhangbinglin
 * @since 2021-02-27
 */
@Data
@ApiModel(value="XyIdentity对象", description="账号认证表")
@TableName("xy_identity")
public class Identity {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    @ApiModelProperty(value = "微信号")
    private String wechat;

    @ApiModelProperty(value = "用户类型，1企业，2个人")
    private Integer type;

    @ApiModelProperty(value = "企业名称或个人名字")
    private String name;

    @ApiModelProperty(value = "营业执照/个人身份证")
    private String certificateImg;

    @ApiModelProperty(value = "媒体角色，1：V1黑盒模式，2:V2白盒模式")
    private Integer roleType;

    @ApiModelProperty(value = "媒体分成比例，正整数，不能大于100")
    private Integer profitRate;

    @ApiModelProperty(value = "平台分成比例，正整数，不能大于100")
    private Integer platRate;

    @ApiModelProperty(value = "银行卡户名")
    private String cardName;

    @ApiModelProperty(value = "银行卡号")
    private String cardNumber;

    @ApiModelProperty(value = "开户银行")
    private String bank;

    @ApiModelProperty(value = "财务联系方式")
    private String contactInfo;

    @ApiModelProperty(value = "状状态，0未认证，1已认证，2待审核，3已禁用")
    private Integer status = 0;

}
