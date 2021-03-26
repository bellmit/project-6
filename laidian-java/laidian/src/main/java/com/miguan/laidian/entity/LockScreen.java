package com.miguan.laidian.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * 锁屏壁纸表
 *
 * @Author xy.chen
 * @Date 2019/12/10
 **/
@Entity(name = "lock_screen")
@Data
public class LockScreen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty("标题")
    private String title;
    @ApiModelProperty("分类ID")
    @Column(name = "class_id")
    private String classId;
    @ApiModelProperty("状态 1开启 2关闭")
    private String state;
    @ApiModelProperty("图片地址")
    @Column(name = "image_url")
    private String imageUrl;
    @ApiModelProperty("文案颜色")
    @Column(name = "font_color")
    private String fontColor;
    @ApiModelProperty("解锁 1开启 2关闭")
    @Column(name = "lock_state")
    private String lockState;
    @ApiModelProperty("设置默认次数")
    @Column(name = "set_default_nums")
    private Integer setDefaultNums;
    @ApiModelProperty("基础权重")
    @Column(name = "base_weight")
    private String baseWeight;
}
