package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 短信基础信息配置表
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-08-09
 */
@Data
@ApiModel("短信基础信息配置")
public class SmsConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("短信接口code")
    private String smsCode;

    @ApiModelProperty("短信接口名称")
    private String smsName;

    @ApiModelProperty("短信服务商名称")
    private String smsServiceName;

    @ApiModelProperty("短信apikey")
    private String apikey;

    @ApiModelProperty("短信secretkey")
    private String secretkey;

    @ApiModelProperty("短信接口地址，多个的时候逗号分隔")
    private String interfaceUrl;

    @ApiModelProperty("短信报告地址，多个的时候逗号分隔")
    private String reportUrl;

    @ApiModelProperty("短信渠道编号")
    private String channelNo;

    @ApiModelProperty("短信接口名称，多个的时候逗号分隔")
    private String interfaceName;

    @ApiModelProperty("短信倒计时时间（秒）")
    private Integer countdownTime;

    @ApiModelProperty("短信超时失效时间（分）")
    private Integer timeLimit;

    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("短信签名")
    private String token;

    @ApiModelProperty("状态：0-启用，1-禁用")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("模板模板内容")
    private String tpl;
}
