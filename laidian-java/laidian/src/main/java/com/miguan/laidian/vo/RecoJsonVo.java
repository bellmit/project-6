package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 关于我们bean
 * @author xy.chen
 * @date 2020-07-23
 **/
@Data
@ApiModel("推荐JSON")
public class RecoJsonVo {

	@ApiModelProperty("排序")
	private int sort;

	@ApiModelProperty("分类ID")
	private int catId;

	@ApiModelProperty("视频ID，多个用逗号隔开")
	private String videoIds;

}
