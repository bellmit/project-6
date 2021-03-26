package com.miguan.laidian.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 图片分类实体
 *
 * @Author xy.chen
 * @Date 2019/7/8
 **/
@Entity
@Table(name = "images_cat")
@Data
@ApiModel("图片分类实体")
public class ImagesCat implements Serializable {

    private static final long serialVersionUID = 7124228991050361653L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty("主键")
    private Long id;

    @Column(name = "name")
    @ApiModelProperty("分类名称")
    private String name;

    @Column(name = "created_at")
    @ApiModelProperty("创建时间")
    private Date createdAt;

    @Column(name = "updated_at")
    @ApiModelProperty("更新时间")
    private Date updatedAt;

    @Column(name = "sort")
    @ApiModelProperty("排序")
    private Long sort;
}
