package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 短信记录实体
 * 
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-08-09
 */
 @Data
 @ApiModel("短信发送记录")
 public class SmsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("手机号码")
    private String phone;

    @ApiModelProperty("发送时间")
    private Date sendTime;

    @ApiModelProperty("发送信息")
    private String content;

    @ApiModelProperty("响应时间")
    private Date respTime;

    @ApiModelProperty("响应信息")
    private String resp;

    @ApiModelProperty("短信类型")
    private String smsType;

    @ApiModelProperty("验证码")
    private String code;

    @ApiModelProperty("订单号")
    private String orderNo;

    @ApiModelProperty("状态 10发送成功未使用，20已使用，30发送中 40发送失败")
    private String state;

    @ApiModelProperty("短信验证次数")
    private int verifyTime;

}