package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 首页视频源列表bean
 * @author xy.chen
 * @date 2019-08-09
 **/
@Data
@ApiModel("首页视频源列表实体")
public class FirstVideosVo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("主键")
	private Long id;

	@ApiModelProperty("分类id")
	private Long catId;

	@ApiModelProperty("标题")
	private String title;

	@ApiModelProperty("源视频url")
	private String url;

	@ApiModelProperty("源音频url")
	private String urlAudio;

	@ApiModelProperty("源图片url")
	private String urlImg;

	@ApiModelProperty("本地视频地址")
	private String localUrl;

	@ApiModelProperty("本地音频地址")
	private String localAudioUrl;

	@ApiModelProperty("本地图片地址")
	private String localImgUrl;

	@ApiModelProperty("白山云视频地址")
	private String bsyUrl;

	@ApiModelProperty("白山云音频地址")
	private String bsyAudioUrl;

	@ApiModelProperty("白山云图片地址")
	private String bsyImgUrl;

	@ApiModelProperty("收藏数")
	private Long collectionCount;

	@ApiModelProperty("点赞数")
	private Long loveCount;

	@ApiModelProperty("评论数")
	private Long commentCount;

	@ApiModelProperty("观看数")
	private Long watchCount;

	@ApiModelProperty("创建时间")
	private String createdAt;

	@ApiModelProperty("更新时间")
	private String updatedAt;

	@ApiModelProperty("状态 1开启 2关闭")
	private Integer state;

	@ApiModelProperty("收藏 0 未收藏 1收藏")
	private String collection;

	@ApiModelProperty("点赞 0 未点赞 1点赞")
	private String love;

	@ApiModelProperty("点赞 0 未点赞 1点赞")
	private String collectionTime;

	@ApiModelProperty("收藏ID")
	private String collectionId;

	@ApiModelProperty("视频源头像")
	private String urlHeadimg;

	@ApiModelProperty("白云山头像地址")
	private String bsyHeadUrl;

	@ApiModelProperty("视频作者")
	private String videoAuthor;

	@ApiModelProperty("时长")
	private String videoTime;

	@ApiModelProperty("视频类型")
	private String videoType;
}
