package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 广告VO
 * @Author zhangbinglin
 * @Date 2019/6/27 14:49
 **/
@ApiModel("广告VO")
@Getter
@Setter
public class Advert implements Serializable {

    @ApiModelProperty("广告位置ID")
    private Long id;

    @ApiModelProperty("广告所在位置类型")
    private String postitionType;

    @ApiModelProperty("广告所在位置名称")
    private String postitionName;

    @ApiModelProperty("广告标题")
    private String title;

    @ApiModelProperty("广告url")
    private String url;

    @ApiModelProperty("图片路径")
    private String imgPath;

    @ApiModelProperty("图片路径2")
    private String imgPath2;

    @ApiModelProperty("广告备注")
    private String remark;

    @ApiModelProperty("广告类型（1表示自定义 2表示sdk）")
    private String adType;

    @ApiModelProperty("广告商（1百度 2广点通 3穿山甲-激励视频 4穿山甲-全屏广告 5穿山甲-开屏广告 6穿山甲-banner广告 7穿山甲-draw信息流广告 8穿山甲-信息流广告 9广点通-激励视频广告 10广点通-自渲染广告）")
    private String adCode;

    @ApiModelProperty("广告ID")
    private String adId;

    @ApiModelProperty("马甲类型")
    private String appType;

    @ApiModelProperty("每日展示次数，默认值0，0不限制次数")
    private Long advCount;

    @ApiModelProperty("广告状态：0启用  1禁用")
    private Integer state;

    @ApiModelProperty("广告ID")
    private Long xId;
}
