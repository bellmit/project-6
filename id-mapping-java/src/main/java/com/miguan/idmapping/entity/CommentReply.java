package com.miguan.idmapping.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author jobob
 * @since 2020-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CommentReply implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 评论id
     */
    private String commentId;

    /**
     * 1为回复视频，2为回复别人的回复
     */
    private Integer replyType;

    /**
     * 回复目标id，reply_type为1时，是reply_id=0，reply_type为2时为回复表的id
     */
    private String replyId;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 冗余回复对象的昵称(reply_type=0时，当前字段为空)
     */
    private String toNickname;

    /**
     * 冗余回复目标用户id(reply_type=0时，当前字段为空)
     */
    private Long toFromUid;

    /**
     * 回复用户id
     */
    private Long fromUid;

    /**
     * 回复者的头像'
     */
    private String fromThumbImg;

    /**
     * 回复者的昵称
     */
    private String fromNickname;

    /**
     * 评论时间
     */
    private LocalDateTime nickTime;

    /**
     * 0为普通回复，1为后台管理员回复
     */
    private Integer isAuthor;

    /**
     * 是否置顶(0为不置顶,1为置顶)
     */
    private Integer isTop;

    /**
     * 是否热评(0为不热评,1为热评)
     */
    private Integer isHot;

    /**
     * 评论被点赞的次数
     */
    private Long likeNum;

    /**
     * 评论被回复的次数
     */
    private Long replyNum;

    /**
     * 主评论id(reply_type=0时，当前字段保存评论id)
     */
    private String pCommentId;

    /**
     * 视频id
     */
    private Long videoId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 是否已读(0未读,1已读)
     */
    private Integer alreadyRead;

    /**
     * 类型 10首页视频 20 小视频
     */
    private Integer videoType;

    private String giveUpType;

    private Long createNo;

    private Integer status;

    private Long updateNo;

    private LocalDateTime updateTime;


}
