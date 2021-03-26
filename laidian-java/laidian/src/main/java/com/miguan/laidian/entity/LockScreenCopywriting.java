package com.miguan.laidian.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "lock_screen_copywriting")
@Data
public class LockScreenCopywriting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty("文案")
    private String title;

    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("类型对应的内容")
    private String typeValue;

    @ApiModelProperty("生效日期：当天日期或者-1（无限制）")
    private String effectiveDate;

    @ApiModelProperty("点击数")
    private String clickNums;

    @ApiModelProperty("状态 1开启 2关闭")
    private Integer state;
}
