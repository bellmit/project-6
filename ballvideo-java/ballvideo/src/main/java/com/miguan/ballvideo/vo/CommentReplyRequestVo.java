package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 评论查询接口参数对象
 * 1.评论查询接口一级评论接口所需参数：currentPage，pageSize，type,userId,replyType,videoId，videoType，sortOrder
 * 2.查一级评论下的所有二级评论所需参数：currentPage，pageSize, type,userId,replyType,ppCommentId
 * */
@Data
public class CommentReplyRequestVo {

    @ApiModelProperty("主评论Id")
    private String ppCommentId;

    @ApiModelProperty("replyType:查询一级评论1 ，二级以上评论2")
    private Integer replyType;

    @ApiModelProperty("视频id")
    private Long videoId;

    @ApiModelProperty("视频类型 10首页视频 20 小视频")
    private Integer videoType;

    @ApiModelProperty("排序方式 10代表时间正序排序  20代表时间倒序排序   30代表点赞热点排序   空值默认时间正序排序")
    private String sortOrder;

    private String userId;

    private int currentPage;

    private int pageSize;

    @ApiModelProperty("type : 一级评论1 ， 我的消息2 ，二级评论3")
    private int type;

    @ApiModelProperty("冗余回复目标用户id(reply_type=0时，当前字段为空)")
    private Long toFromUid;//接口文档没有该字段描述，安卓代码里也没有传这个字段 add shixh1226
}