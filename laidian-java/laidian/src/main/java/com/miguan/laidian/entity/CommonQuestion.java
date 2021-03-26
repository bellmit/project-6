package com.miguan.laidian.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "common_question")
@Data
@ApiModel("常见问题")
public class CommonQuestion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty("标题")
    @Column(name = "title")
    private String title;

    @ApiModelProperty("手机品牌ID: 0-全部品牌")
    @Column(name = "tel_brand_id")
    private String telBrandId;

    @ApiModelProperty("手机机型")
    @Column(name = "tel_type")
    private String telType;

    @ApiModelProperty("app类型  微来电-wld,炫来电-xld")
    @Column(name = "app_type")
    private String appType;

    @ApiModelProperty("状态 1开启 0关闭")
    @Column(name = "state")
    private int state;

    @ApiModelProperty("点击数")
    @Column(name = "click_num")
    private int clickNum;

    @ApiModelProperty("选择有用数")
    @Column(name = "useful_num")
    private int usefulNum;

    @ApiModelProperty("选择没用数")
    @Column(name = "unuseful_num")
    private int unusefulNum;

    @ApiModelProperty("权重值")
    @Column(name = "weight")
    private int weight;

    @ApiModelProperty("创建时间")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @Column(name = "update_time")
    private Date updateTime;

    @ApiModelProperty("内容")
    @Column(name = "content")
    private String content;

}
