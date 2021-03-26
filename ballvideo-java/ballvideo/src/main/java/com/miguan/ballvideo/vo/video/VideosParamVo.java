package com.miguan.ballvideo.vo.video;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class VideosParamVo {

	@ApiModelProperty("视频Id列表")
	private List<Long> videoIds;
}
