package com.miguan.ballvideo.entity.es;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "ballvideo",type = "FirstVideoEsVo", shards = 1, replicas = 0)
public class FirstVideoEsVo{

	@ApiModelProperty("视频字段：主键")
	@Id
	private Long id;

	@ApiModelProperty("视频字段：视频标题带颜色")
	@Field(type = FieldType.Text)
	private String title;

	@ApiModelProperty("视频字段：视频标题")
	private String colorTitle;

	@ApiModelProperty("视频字段：分类id")
	private Long catId;

	@ApiModelProperty("视频字段：源图片url")
	private String urlImg;

	@ApiModelProperty("视频字段：白山云视频地址")
	private String bsyUrl;

	@ApiModelProperty("视频字段：白山云图片地址")
	private String bsyImgUrl;

	@ApiModelProperty("视频字段：收藏数")
	private Integer collectionCount;

	@ApiModelProperty("视频字段：点赞数")
	private Integer loveCount;

	@ApiModelProperty("视频字段：评论数")
	private Integer commentCount;

	@ApiModelProperty("视频字段：观看数")
	private String watchCount;

	@ApiModelProperty("视频字段：收藏 0 未收藏 1收藏")
	private String collection;

	@ApiModelProperty("视频字段：点赞 0 未点赞 1点赞")
	private String love;

	@ApiModelProperty("视频字段：视频源头像或者广告url")
	private String urlHeadimg;

	@ApiModelProperty("视频字段：白云山头像地址")
	private String bsyHeadUrl;//用户头像

	@ApiModelProperty("视频字段：视频作者")
	private String videoAuthor;

	@ApiModelProperty("视频字段：时长")
	private String videoTime;

	@ApiModelProperty("视频类型")
	private String videoType;

	@ApiModelProperty("视频字段：分享数")
	private String shareCount;

	@ApiModelProperty("虚假的分享数")
	private String fakeShareCount;

	@ApiModelProperty("视频字段：完整播放数")
	private String playAllCount;

	@ApiModelProperty("视频字段：视频大小")
	private String videoSize;

	@ApiModelProperty("创建时间")
	private String createdAt;

	private long createDate;//单位是秒

	@ApiModelProperty("分类名称")
	private String catName;//神策用到

	@ApiModelProperty("合集ID")
	private Long gatherId;//合集ID

	@ApiModelProperty("集合标题")
	private String gatherTitle;

	@ApiModelProperty("权重")
	private long totalWeight;

	@ApiModelProperty("权重（唯一）")
	private Long totalWeightLimit = 0L;

    @ApiModelProperty("是否激励视频 1:是,0:否")
    private Integer incentiveVideo;

	@ApiModelProperty("用户ID")
	private Long userId;

	@ApiModelProperty("白山云m3u8地址")
	private String bsyM3u8;

	@ApiModelProperty("安卓视频加密地址")
	private String encryptionAndroidUrl;

	@ApiModelProperty("ios视频加密地址")
	private String encryptionIosUrl;

	@ApiModelProperty("小程序视频加密地址")
	private String encryptionXcxUrl;

	@ApiModelProperty("视频来源。98du，wuli")
	private String videosSource;

	@ApiModelProperty("小程序分享图")
	private String xcxShareImg;
}
