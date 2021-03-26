package com.miguan.laidian.entity;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @Author: chenweijie
 * @Date: 2020/10/26 10:57
 * @Description:
 */
@Getter
@Setter
public class LdUserTagRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty("用户id")
    @Column(name = "user_id")
    private Integer userId;

    @ApiModelProperty("标签id")
    @Column(name = "tag_id")
    private Integer tagId;

    @ApiModelProperty("创建时间")
    @Column(name = "created_at")
    private Integer createdAt;

    @ApiModelProperty("更新时间")
    @Column(name = "updated_at")
    private Integer updatedAt;
}
