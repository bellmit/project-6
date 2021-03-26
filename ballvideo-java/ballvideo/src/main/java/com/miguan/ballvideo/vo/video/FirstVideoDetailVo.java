package com.miguan.ballvideo.vo.video;

import com.miguan.ballvideo.vo.AdvertCodeVo;
import com.miguan.ballvideo.vo.AdvertVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class FirstVideoDetailVo {

	@ApiModelProperty("广告集合")
	private List<AdvertVo> advers;

	@ApiModelProperty("视频合集")
	private VideoGatherVo videoGatherVo;//如果是合集视频，返回合集集合，如果是专辑视频，返回专辑集合

	@ApiModelProperty("视频列表")
	private List<Videos161Vo> videos;

	@ApiModelProperty("V2.5.0广告返回数据")
	List<AdvertCodeVo> advertCodeVos;
}
