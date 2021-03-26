package com.miguan.laidian.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.util.Date;

@Getter
@Setter
public class CommentReplyRequest {

    @Column(name = "id")
    private Long id;

    @ApiModelProperty("评论id")
    private String commentId;

    @ApiModelProperty("1为回复视频，2为回复别人的回复")
    private Integer replyType;

    @ApiModelProperty("回复目标id，reply_type为1时，是reply_id=0，reply_type为2时为回复表的id")
    private String replyId;

    @ApiModelProperty("回复内容")
    private String content;

    @ApiModelProperty("冗余回复对象的昵称(reply_type=0时，当前字段为空)")
    private String toNickname;

    @ApiModelProperty("冗余回复目标用户id(reply_type=0时，当前字段为空)")
    private Long toFromUid;

    @ApiModelProperty("回复用户id")
    private Long fromUid;

    @ApiModelProperty("回复者的头像")
    private String fromThumbImg;

    @ApiModelProperty("回复者的昵称")
    private String fromNickname;

    @ApiModelProperty("评论时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date nickTime;

    @ApiModelProperty("0为普通回复，1为后台管理员回复")
    private Integer isAuthor;

    @ApiModelProperty("是否置顶(0为不置顶,1为置顶)")
    private Integer isTop;

    @ApiModelProperty("是否热评(0为不热评,1为热评)")
    private Integer isHot;

    @ApiModelProperty("评论被点赞的次数")
    private Long likeNum;

    @ApiModelProperty("评论被回复的次数")
    private Long replyNum;

    @ApiModelProperty("主评论id(reply_type=0时，当前字段保存评论id)")
    private String ppCommentId;

    @ApiModelProperty("视频id")
    private Long videoId;

    @ApiModelProperty("视频类型 10首页视频 20 小视频")
    private Integer videoType;

    @ApiModelProperty("评论状态，0为删除，1为待审核，2为已发布")
    private Integer status;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    @ApiModelProperty("创建人")
    private Long createNo;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;

    @ApiModelProperty("更新人")
    private Long updateNo;

    @ApiModelProperty("马甲类型")
    private String appType;
};