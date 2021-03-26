package com.miguan.ballvideo.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
/**
 * 菜单栏配置表bean
 * @author hyl
 * @date 2019-08-23
 **/
@Data
@ApiModel("菜单栏配置表实体")
@Entity(name = "cl_menu_config")
public class ClMenuConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty("标题")
    @Column(name = "title")
    private String title;

    @ApiModelProperty("图标URL")
    @Column(name = "img_url")
    private String imgUrl;

    @ApiModelProperty("图标URL2")
    @Column(name = "img_url2")
    private String imgUrl2;

    @ApiModelProperty("链接地址")
    @Column(name = "link_addr")
    private String linkAddr;

    @ApiModelProperty("状态：0启用  1禁用")
    @Column(name = "state")
    private Integer state;

    @ApiModelProperty("优先级1-5")
    @Column(name = "sort")
    private Integer sort;

    @ApiModelProperty("创建时间")
    @Column(name = "created_at")
    private String createdAt;

    @ApiModelProperty("更新时间")
    @Column(name = "updated_at")
    private String updatedAt;

    @ApiModelProperty("选择标题")
    @Column(name = "select_title")
    private String selectTitle;

    @ApiModelProperty("包名")
    @Column(name = "app_package")
    private String appPackage;

    @ApiModelProperty("开始版本")
    @Column(name = "start_version")
    private String startVersion;

    @ApiModelProperty("结束版本")
    @Column(name = "end_version")
    private String endVersion;

    @ApiModelProperty("屏蔽渠道")
    @Column(name = "hide_channel")
    private String hideChannel;

    @ApiModelProperty("tips提示语")
    @Column(name = "tips")
    private String tips;

    @ApiModelProperty("关键字")
    @Column(name = "keyword")
    private String keyword;

    @ApiModelProperty("是否启用H5链接 1启用 0不启用")
    @Column(name = "h5_switch")
    private Integer h5Switch;

}
