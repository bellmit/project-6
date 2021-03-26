package com.miguan.ballvideo.vo.video;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("视频专辑详情")
public class VideoAlbumDetailVo {

	@ApiModelProperty("视频Id")
	private Long firstVideosId;

	@ApiModelProperty("专辑Id")
	private Long videoAlbumId;

	@ApiModelProperty("总权重：基础权重+真实权重")
	private Long totalWeight;

	@ApiModelProperty("专辑排序")
	private Long sort;
}
