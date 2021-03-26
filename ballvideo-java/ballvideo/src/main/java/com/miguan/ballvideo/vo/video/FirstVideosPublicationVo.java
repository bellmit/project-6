package com.miguan.ballvideo.vo.video;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miguan.ballvideo.common.util.NumberUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("个人主页视频对象")
public class FirstVideosPublicationVo {

    @ApiModelProperty("主键ID")
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

    @ApiModelProperty("白云山头像地址")
    private String bsyHeadUrl;

    @ApiModelProperty("视频作者")
    private String videoAuthor;

    @ApiModelProperty("收藏数")
    private Long collectionCount;

    @ApiModelProperty("点赞数")
    private Long loveCount;

    @ApiModelProperty("评论数")
    private Long commentCount;

    @ApiModelProperty("观看数")
    private Long watchCount;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern="MM-dd", timezone="GMT+8")
    private Date createdAt;

    @ApiModelProperty("时长")
    private String videoTime;

    @ApiModelProperty("举报数")
    private Long report;

    @ApiModelProperty("分享数")
    private Long shareCount;

    @ApiModelProperty("虚假的分享数")
    private Long fakeShareCount;

    @ApiModelProperty("显示的分享数（等于shareCount + fakeShareCount）,万以上显示“X.XX万”，保留2位小数")
    private String showShareCount;

    @ApiModelProperty("视频大小")
    private String videoSize;

    @ApiModelProperty("真实观看数")
    private Long watchCountReal;

    @ApiModelProperty("真实点赞数")
    private Long loveCountReal;

    @ApiModelProperty("简介")
    private String brief;

    @ApiModelProperty("审核状态：0 审核中 1通过 2不通过")
    private Integer examState;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("专辑ID")
    private Long albumId;

    @ApiModelProperty("专辑标题")
    private String albumTitle;

    public String getShowShareCount() {
        long shareCount = (this.shareCount != null ? this.shareCount : 0);
        long fakeShareCount = (this.fakeShareCount != null ? this.fakeShareCount : 0);
        double showShareCount = Double.parseDouble(String.valueOf(shareCount + fakeShareCount));
        return NumberUtil.numberFormat(showShareCount);
    }

}
