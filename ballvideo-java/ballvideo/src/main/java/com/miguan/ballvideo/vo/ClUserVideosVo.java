package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户视频关联表bean
 * @author xy.chen
 * @date 2019-08-09
 **/
@Data
@ApiModel("用户视频关联表实体")
public class ClUserVideosVo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("主键")
	private Long id;

	@ApiModelProperty("用户id")
	private Long userId;

	@ApiModelProperty("视频id")
	private Long videoId;

	@ApiModelProperty("视频类型 10 首页视频 20小视频")
	private Integer videoType;

	@ApiModelProperty("收藏 0 未收藏 1收藏")
	private String collection;

	@ApiModelProperty("点赞 0 未点赞 1点赞")
	private String love;

	@ApiModelProperty("兴趣 0 感兴趣 1 不感兴趣")
	private String interest;

	@ApiModelProperty("操作类型：10--收藏 20--点赞 30--观看 40--取消收藏 50--取消点赞")
	private String opType;

	@ApiModelProperty("收藏时间")
	private Date collectionTime;

	@ApiModelProperty("点赞数")
	private Long loveCnt;

}
