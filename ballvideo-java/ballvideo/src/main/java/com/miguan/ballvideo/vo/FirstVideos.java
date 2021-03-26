package com.miguan.ballvideo.vo;

import com.cgcg.context.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.miguan.ballvideo.common.util.NumberUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 首页视频源列表bean
 * @author xy.chen
 * @date 2019-08-09
 **/
@Data
@ApiModel("首页视频源列表实体")
public class FirstVideos implements Serializable {

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
	private Integer collectionCount;

	@ApiModelProperty("点赞数")
	private Integer loveCount;

	@ApiModelProperty("真实点赞数")
	private String loveCountreal;

	@ApiModelProperty("评论数")
	private Integer commentCount;

	@ApiModelProperty("观看数")
	private String watchCount;

	@ApiModelProperty("真实观看数")
	private String watchCountReal;

	@ApiModelProperty("创建时间")
	private String createdAt;

	@ApiModelProperty("更新时间")
	private String updatedAt;

	@ApiModelProperty("状态 1开启 2关闭 0-下线")
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

	@ApiModelProperty("广告加载位置列表")
	private List<AdvertVo> advertVoList;

	@ApiModelProperty("分享数")
	private String shareCount;

	@ApiModelProperty("虚假的分享数")
	private String fakeShareCount;

	@ApiModelProperty("显示的分享数（等于shareCount + fakeShareCount）,万以上显示“X.XX万”，保留2位小数")
	private String showShareCount;

	@ApiModelProperty("完整播放数")
	private String playAllCount;

	@ApiModelProperty("基础权重")
	private String baseWeight;

	@ApiModelProperty("视频大小")
	private String videoSize;

	@ApiModelProperty("真实权重")
	private String realWeight;

	@ApiModelProperty("超过30%时长播放数")
	private String playCount;

	@ApiModelProperty("分类名称")
	private String catName;//神策用到

	@ApiModelProperty("合集id")
	private String gatherId;

	@ApiModelProperty("V2.5.0广告返回数据")
	List<AdvertCodeVo> advertCodeVos;

	@ApiModelProperty("用户ID")
	private Long userId;

	@ApiModelProperty("专辑标题")
	private String albumTitle;

	@ApiModelProperty("是否需要解锁:1=是,-1=否")
	private Integer needUnlock;

	@ApiModelProperty("是否激励视频 1:是,0:否")
	private Integer incentiveVideo;

    @ApiModelProperty("白山云m3u8地址")
    private String bsyM3u8;

    @ApiModelProperty("安卓视频加密地址")
    @JsonIgnore
    private String encryptionAndroidUrl;

    @ApiModelProperty("ios视频加密地址")
    @JsonIgnore
    private String encryptionIosUrl;

	@ApiModelProperty("视频来源。98du，wuli")
	private String videosSource;

	@ApiModelProperty("小程序分享图")
	private String xcxShareImg;

	@ApiModelProperty("专辑ID")
	private Long albumId;

	public String getShowShareCount() {
		int shareCount = (StringUtils.isNotBlank(this.shareCount) ? Integer.parseInt(this.shareCount) : 0);
		int fakeShareCount = (StringUtils.isNotBlank(this.fakeShareCount) ? Integer.parseInt(this.fakeShareCount) : 0);
		double showShareCount = Double.parseDouble(String.valueOf(shareCount + fakeShareCount));
		return NumberUtil.numberFormat(showShareCount);
	}


}
