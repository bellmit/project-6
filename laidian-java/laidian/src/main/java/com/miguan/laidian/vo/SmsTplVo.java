package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 短信模板实体
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-08-09
 */
@Data
@ApiModel("短信模板")
public class SmsTplVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("使用通道，即短信配置表id，和cl_sms_config的id对应")
    private Long smsConfigId;

    @ApiModelProperty("短信模板类型")
    private String type;

    @ApiModelProperty("模板名称")
    private String typeName;

    @ApiModelProperty("模板模板内容")
    private String tpl;

    @ApiModelProperty("模板编号")
    private String number;

    @ApiModelProperty("短信模板状态 10启用 20禁用")
    private String state;

    @ApiModelProperty("每日限制次数，为空的时候则表示无限制")
    private Integer maxSend;

}