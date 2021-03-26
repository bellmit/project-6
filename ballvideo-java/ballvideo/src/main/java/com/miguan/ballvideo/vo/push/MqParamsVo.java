package com.miguan.ballvideo.vo.push;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Mq参数通用实体类
 **/
@Data
@ApiModel("Mq参数通用实体类")
public class MqParamsVo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("业务ID， UUID唯一值")
	private String businessId;

	@ApiModelProperty("消息类型")
	private String type;

	@ApiModelProperty("自定义参数")
	private String params;



}
