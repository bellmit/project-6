package com.miguan.laidian.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * @Author: chenweijie
 * @Date: 2020/10/26 10:57
 * @Description:
 */
@Getter
@Setter
public class LdInterestTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty("标签名称")
    @Column(name = "name")
    private String name;

    @ApiModelProperty("分类id")
    @Column(name = "cat_id")
    private Integer catId;

    @ApiModelProperty("排序")
    @Column(name = "sort")
    private Integer sort;

    @ApiModelProperty("软删除，0：删除，1：未删除")
    @Column(name = "is_delete")
    private Integer isDelete;

    @ApiModelProperty("创建时间")
    @Column(name = "created_at")
    private Date createdAt;

    @ApiModelProperty("更新时间")
    @Column(name = "updated_at")
    private Integer updatedAt;

    private boolean isCheck;
}
