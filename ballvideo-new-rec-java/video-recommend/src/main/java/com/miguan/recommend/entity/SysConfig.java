package com.miguan.recommend.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统参数实体类
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-06-20 10:48:24
 */
@Data
@ApiModel("系统参数实体类")
public class SysConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("主键")
	private Long id;

	@ApiModelProperty("参数编号 唯一")
	private String code;

	@ApiModelProperty("参数名称")
	private String name;

	@ApiModelProperty("参数对应的值")
	private Object value;

	@ApiModelProperty("类型")
	private Integer type;

	@ApiModelProperty("状态，0：禁用，1：启用 ")
	private Integer status;
	
	@ApiModelProperty("创建者")
	private Long creator;
	
	@ApiModelProperty("备注")
	private String remark;

	@ApiModelProperty("视频类别")
	private String videoCat;

	@ApiModelProperty("果果视频类别")
	private String videoCatGg;
}
