package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description 广告VO
 * @author laiyudan
 * @date 2019-09-09
 **/
@ApiModel("广告VO")
@Getter
@Setter
public class AdvertVo {

    @ApiModelProperty("广告ID")
    private Long id;

    @ApiModelProperty("广告所在位置类型")
    private String positionType;

    @ApiModelProperty("广告所在位置名称")
    private String positionName;

    @ApiModelProperty("广告标题")
    private String title;

    @ApiModelProperty("广告url（V2.5以后不用）")
    private String url;

    @ApiModelProperty("图片路径（V2.5以后不用）")
    private String imgPath;

    @ApiModelProperty("图片路径2（V2.5以后不用）")
    private String imgPath2;

    @ApiModelProperty("广告备注（V2.5以后不用）")
    private String remark;

    @ApiModelProperty("广告类型（1表示自定义 2表示sdk）")
    private String adType;

    @ApiModelProperty("广告商（广告商（1表示百度 2表示广点通 3表示穿山甲激励视频 4表示穿山甲全屏视频广告，" +
            "5表示穿山甲开屏广告，6表示穿山甲banner广告，7表示穿山甲Draw信息流广告8表示穿山甲信息流广告））")
    private String adCode ;

    @ApiModelProperty("广告ID")
    private String adId;

    @ApiModelProperty("应用ID")
    private String appId;

    @ApiModelProperty("手机类型")
    private String mobileType;

    @ApiModelProperty("首次加载位置")
    private Integer firstLoadPosition;

    @ApiModelProperty("再次加载位置")
    private Integer secondLoadPosition;

    @ApiModelProperty("锁屏广告开关")
    private Integer lockScreenToken;

    @ApiModelProperty("广告位置ID")
    private Long positionId;

    @ApiModelProperty("展示概率")
    private int probability;

    @ApiModelProperty("广告状态：1启用  0禁用")
    private Integer state;

    @ApiModelProperty("banner广告展示次数限制")
    private Integer maxShowNum;

    @ApiModelProperty("阶梯价格广告商")
    private String bannerBusinessId;

    @ApiModelProperty("填充互补率：1启用  0禁用")
    private Integer fillState;

    @ApiModelProperty("0原广告 1填充互补广告 2阶梯广告")
    private int type;

    @ApiModelProperty("计划组key值")
    private String groupKey;

    @ApiModelProperty("计划组Id")
    private Long groupId;

    @ApiModelProperty("连接类型")
    private String linkType;

    public AdvertVo(){}
    public AdvertVo(Long id,Integer firstLoadPosition,Integer secondLoadPosition){
        this.id = id;
        this.secondLoadPosition = secondLoadPosition;
        this.firstLoadPosition = firstLoadPosition;
    }
}
