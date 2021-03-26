package com.miguan.ballvideo.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "cl_user_comment")
@Getter
@Setter
@ApiModel("用户评论点赞表")
public class ClUserComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty("用户id")
    @Column(name = "user_id")
    private Long userId;

    @ApiModelProperty("评论id")
    @Column(name = "comment_id")
    private String commentId;

    @ApiModelProperty("数据状态")
    @Column(name = "type")
    private Integer type;
}