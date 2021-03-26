package com.miguan.ballvideo.vo.video;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CatIdWeightVo {

	@ApiModelProperty("分类id")
	private Long catId;

	@ApiModelProperty("权重值")
	private Double sumCount;
}
