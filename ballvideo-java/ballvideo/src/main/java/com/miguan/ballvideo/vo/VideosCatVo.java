package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 视频分类表bean
 * @author xy.chen
 * @date 2019-08-09
 **/
@Data
@ApiModel("视频分类表实体")
public class VideosCatVo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("主键")
	private Long id;

	@ApiModelProperty("分类名称")
	private String name;

	@ApiModelProperty("权重")
	private Integer weight;

	@ApiModelProperty("类型 10首页视频 20 小视频")
	private String type;

	@ApiModelProperty("状态 2关闭 1开启")
	private String state;
}
