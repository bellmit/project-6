package com.miguan.ballvideo.vo.video;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("首页视频源VO-V1.6.1(视频广告放一起)")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FirstVideosVo{

	public static final String ADV = "adv";

	public static final String VIDEO = "video";

	@ApiModelProperty("类型：adv-广告，video-视频")
	private String type;

	Videos161Vo video;
	Adv161Vo adv;

	@ApiModelProperty("V2.3.0新增")
	List<Adv161Vo> advList;

	@ApiModelProperty("V2.5.0广告返回数据")
	List<AdvertCodeVo> advertCodeVos;
}
