package com.miguan.ballvideo.entity;

import com.miguan.ballvideo.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@ApiModel("评论模板实体")
@Entity(name = "comment_template")
@Data
public class CommentTemplate extends BaseModel {

    @ApiModelProperty("评论内容")
    @Column(name = "content")
    private String content;

    @ApiModelProperty("状态 1开启 0关闭")
    @Column(name = "state")
    private Long state;
}
