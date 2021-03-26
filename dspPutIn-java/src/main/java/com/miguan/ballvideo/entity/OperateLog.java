package com.miguan.ballvideo.entity;

import com.miguan.ballvideo.entity.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;


/**
 * 接口请求日志记录
 * @author shixh
 *
 */
@Data
@Entity(name="sys_operate_log")
public class OperateLog extends BaseModel{

	@ApiModelProperty("操作功能,参照OperateConstant")
	private String operate;
	
	@ApiModelProperty("操作具体业务")
	private String operateBusiness;

	@ApiModelProperty("访问路径")	
	private String url;

	@ApiModelProperty("访问参数")
	private String args;

	@ApiModelProperty("用户IP")	
	private String ip;       
	
	@ApiModelProperty("用户设备ID")
	private String deviceId;

	@ApiModelProperty("code")
	private int code;

	@ApiModelProperty("返回结果")
	private String operateResult;
	
	//@ApiModelProperty("请求响应时间 ")
	//private long time;


	
}
