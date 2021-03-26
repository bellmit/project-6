package com.miguan.ballvideo.vo.video;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import com.miguan.ballvideo.vo.VideoAlbumVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("首页专辑视频源V2.6.7(视频广告放一起)")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideosAlbumInfoVo {

	public static final String ADV = "adv";

	public static final String VIDEO = "video";

	@ApiModelProperty("类型：adv-广告，video-视频")
	private String type;

	VideoAlbumVo video;

	@ApiModelProperty("V2.5.0广告返回数据")
	List<AdvertCodeVo> advertCodeVos;
}
