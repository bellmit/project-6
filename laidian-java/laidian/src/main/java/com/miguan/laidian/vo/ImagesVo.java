package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 图片列表bean
 *
 * @author xy.chen
 * @date 2019-07-09
 **/
@Data
@ApiModel("图片列表实体")
public class ImagesVo extends AdvertCount implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("分类")
    private String category;

    @ApiModelProperty(" ")
    private String coverUrl;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty(" ")
    private String previewUrl;

    @ApiModelProperty("源图片地址")
    private String sourceUrl;

    @ApiModelProperty(" ")
    private String type;

    @ApiModelProperty("本地图片地址")
    private String localUrl;

    @ApiModelProperty("白山云图片地址")
    private String bsyUrl;

    @ApiModelProperty("收藏数量")
    private String likeCount;

    @ApiModelProperty("分享数量")
    private Long shareCount;

    @ApiModelProperty("创建时间")
    private String createdAt;

    @ApiModelProperty("更新时间")
    private String updatedAt;

    @ApiModelProperty("状态 1开启 2关闭")
    private Long state;
}
