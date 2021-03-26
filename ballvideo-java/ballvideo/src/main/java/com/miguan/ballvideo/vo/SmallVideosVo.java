package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 小视频列表bean
 * @author xy.chen
 * @date 2019-08-09
 **/
@Data
@ApiModel("小视频列表实体")
public class SmallVideosVo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("主键")
	private Long id;

	@ApiModelProperty("分类id")
	private Long catId;

	@ApiModelProperty("标题")
	private String title;

	@ApiModelProperty("白山云视频地址")
	private String bsyUrl;

	@ApiModelProperty("白山云音频地址")
	private String bsyAudioUrl;

	@ApiModelProperty("白山云图片地址")
	private String bsyImgUrl;

	@ApiModelProperty("白山云头像图片地址")
	private String bsyHeadUrl;

	@ApiModelProperty("视频作者")
	private String videoAuthor;

	@ApiModelProperty("收藏数")
	private String collectionCount;

	@ApiModelProperty("点赞数")
	private String loveCount;

	@ApiModelProperty("真实点赞数")
	private String loveCountreal;

	@ApiModelProperty("评论数")
	private String commentCount;

	@ApiModelProperty("观看数")
	private String watchCount;

	@ApiModelProperty("真实观看数")
	private String watchCountReal;

	@ApiModelProperty("状态 1开启 2关闭")
	private Integer state;

	@ApiModelProperty("收藏 0 未收藏 1收藏")
	private String collection;

	@ApiModelProperty("点赞 0 未点赞 1点赞")
	private String love;

	@ApiModelProperty("时长")
	private String videoTime;

	@ApiModelProperty("广告加载位置列表")
	private List<AdvertVo> advertVoList;

	@ApiModelProperty("分享数")
	private String shareCount;
}
