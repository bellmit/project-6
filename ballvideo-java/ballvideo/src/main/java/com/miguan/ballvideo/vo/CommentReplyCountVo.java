package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CommentReplyCountVo {

    @ApiModelProperty("回复目标id")
    private String commentId;

    @ApiModelProperty("回复总数")
    private Integer countNum;
}
