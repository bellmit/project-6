package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * iOS视频源列表bean
 *
 * @author xy.chen
 * @date 2019-08-02
 **/
@Data
@ApiModel("小视频源列表实体")
public class SmallVideoVo extends AdvertCount implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("源视频url")
    private String url;

    @ApiModelProperty("分类id")
    private Long catId;

    @ApiModelProperty("源图片url")
    private String urlImg;

    @ApiModelProperty("本地视频地址")
    private String localUrl;

    @ApiModelProperty("白山云视频地址")
    private String bsyUrl;

    @ApiModelProperty("本地图片地址")
    private String localImgUrl;

    @ApiModelProperty("白山云图片地址")
    private String bsyImgUrl;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("创建时间")
    private String createdAt;

    @ApiModelProperty("更新时间")
    private String updatedAt;

    @ApiModelProperty("状态 1开启 2关闭")
    private Long state;

    @ApiModelProperty("用户名")
    private String name;

    @ApiModelProperty("白山云头像图片地址")
    private String bsyHeadUrl;


    //待优化

    @ApiModelProperty("收藏ID")
    private String collectionId;

    @ApiModelProperty("时长")
    private String videoTime;

    @ApiModelProperty("收藏数量")
    private String likeCount;

    //@ApiModelProperty("修改时间")
    //private String collectionTime;//update time

    @ApiModelProperty("分享数量")
    private Long shareCount;

    @ApiModelProperty("评论数")
    private Long commentCount;

    @ApiModelProperty("收藏")
    private String collection;

    @ApiModelProperty("用户收藏数量")
    private Long userLikeCount;

    @ApiModelProperty("点赞")
    private String love;

    @ApiModelProperty("举报数")
    private Long report;

    @ApiModelProperty("点击数")
    private Long clickCount;
}
