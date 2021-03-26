package com.miguan.laidian.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "comment_reply")
@Getter
@Setter
@ApiModel("评论回复")
public class CommentReply implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty("评论id")
    @Column(name = "comment_id")
    private String commentId;

    @ApiModelProperty("1为回复视频，2为回复别人的回复")
    @Column(name = "reply_type")
    private Integer replyType;

    @ApiModelProperty("回复目标id，reply_type为1时，是reply_id=0，reply_type为2时为回复表的id")
    @Column(name = "reply_id")
    private String replyId;

    @ApiModelProperty("回复内容")
    @Column(name = "content")
    private String content;

    @ApiModelProperty("冗余回复对象的昵称(reply_type=0时，当前字段为空)")
    @Column(name = "to_nickname")
    private String toNickname;

    @ApiModelProperty("冗余回复目标用户id(reply_type=0时，当前字段为空)")
    @Column(name = "to_from_uid")
    private Long toFromUid;

    @ApiModelProperty("回复用户id")
    @Column(name = "from_uid")
    private Long fromUid;

    @ApiModelProperty("回复者的头像")
    @Column(name = "from_thumb_img")
    private String fromThumbImg;

    @ApiModelProperty("回复者的昵称")
    @Column(name = "from_nickname")
    private String fromNickname;

    @ApiModelProperty("评论时间")
    @Column(name = "nick_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date nickTime;

    @ApiModelProperty("0为普通回复，1为后台管理员回复")
    @Column(name = "is_author")
    private Integer isAuthor;

    @ApiModelProperty("是否置顶(0为不置顶,1为置顶)")
    @Column(name = "is_top")
    private Integer isTop;

    @ApiModelProperty("是否热评(0为不热评,1为热评)")
    @Column(name = "is_hot")
    private Integer isHot;

    @ApiModelProperty("评论被点赞的次数")
    @Column(name = "like_num")
    private Long likeNum;

    @ApiModelProperty("评论被回复的次数")
    @Column(name = "reply_num")
    private Long replyNum;

    @ApiModelProperty("主评论id(reply_type=0时，当前字段保存评论id)")
    @Column(name = "p_comment_id")
    private String pCommentId;

    @ApiModelProperty("视频id")
    @Column(name = "video_id")
    private Long videoId;

    @ApiModelProperty("视频类型 10首页视频 20 小视频")
    @Column(name = "video_type")
    private Integer videoType;

    @ApiModelProperty("评论状态，0为删除，1为待审核，2为已发布")
    @Column(name = "status")
    private Integer status;

    @ApiModelProperty("是否已读(0未读,1已读)")
    @Column(name = "already_read")
    private Integer alreadyRead;

    @ApiModelProperty("创建时间")
    @Column(name = "create_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    @ApiModelProperty("创建人")
    @Column(name = "create_no")
    private Long createNo;

    @ApiModelProperty("更新时间")
    @Column(name = "update_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;

    @ApiModelProperty("更新人")
    @Column(name = "update_no")
    private Long updateNo;

    @ApiModelProperty("马甲类型")
    @Column(name = "app_type")
    private String appType;
}