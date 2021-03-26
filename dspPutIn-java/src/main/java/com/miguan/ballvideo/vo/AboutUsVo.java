package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 关于我们bean
 * @author xy.chen
 * @date 2019-08-23
 **/
@Data
@ApiModel("关于我们实体")
public class AboutUsVo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("主键")
	private Long id;

	@ApiModelProperty("公司简介")
	private String company;

	@ApiModelProperty("商务合作")
	private String business;

	@ApiModelProperty("版权所有")
	private String version;

	@ApiModelProperty("QQ")
	private String QQ;

	@ApiModelProperty("email")
	private String myEmail;

	@ApiModelProperty("创建时间")
	private String createdAt;

	@ApiModelProperty("更新时间")
	private String updatedAt;


}
