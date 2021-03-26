package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 视频分类管理表bean
 * @author xy.chen
 * @date 2019-08-13
 **/
@Data
@ApiModel("视频分类表实体")
public class VideosCatSortVo {

	@ApiModelProperty("主键")
	private Long id;

	@ApiModelProperty("归属应用")
	private String ascriptionApplication;

	@ApiModelProperty("作用渠道，用逗号隔开，全渠道为all")
	private String channel;

	@ApiModelProperty("开始的版本")
	private String versionStart;

	@ApiModelProperty("结束的版本")
	private String versionEnd;

	@ApiModelProperty("排序，逗号隔开")
	private String sort;

	@ApiModelProperty("状态1启用2禁用")
	private Integer status;
}
