package com.miguan.laidian.vo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 音频信息表bean
 * @author xy.chen
 * @date 2020-05-22
 **/
@Data
@ApiModel("音频信息表实体")
public class AudioVo{

	@ApiModelProperty("主键")
	private Integer id;

	@ApiModelProperty("分类id")
	private Integer catId;

	@ApiModelProperty("标题")
	private String title;

	@ApiModelProperty("歌手")
	private String singer;

	@ApiModelProperty("白山云图片地址")
	private String bsyImgUrl;

	@ApiModelProperty("白山云音频地址")
	private String bsyAudioUrl;

	@ApiModelProperty("铃声长度（秒）")
	private String audioTime;

	@ApiModelProperty("分享数")
	private Integer shareCount;

	@ApiModelProperty("试听数")
	private Integer auditionCount;

	@ApiModelProperty("下载数")
	private Integer downloadCount;

	@JsonIgnore
	@ApiModelProperty("人工收藏数")
	private Integer likeCount;

	@JsonIgnore
	@ApiModelProperty("用户收藏数")
	private Integer userLikeCount;

	@JsonIgnore
	@ApiModelProperty("基础权重值")
	private Integer baseWeight;

	@JsonIgnore
	@ApiModelProperty("0不推荐 1推荐")
	private Integer recommend;

	@JsonIgnore
	@ApiModelProperty("状态 1开启 2关闭")
	private Integer state;

	@JsonIgnore
	@ApiModelProperty("创建时间")
	private String createdAt;

	@JsonIgnore
	@ApiModelProperty("更新时间")
	private String updatedAt;

	@ApiModelProperty("总的收藏数（人工+用户）")
	private String totalLikeCount;
}
