package com.miguan.ballvideo.entity.dsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**Dsp广告内容数据集合
 * @Author suhj
 * @Date 2020/4/24
 **/
@ApiModel("Dsp广告内容")
@Data
public class AdvertDspInfo{
    @ApiModelProperty("代码位ID")
    private String code;
    @ApiModelProperty("计划ID")
    private String planId;
    @ApiModelProperty("创意ID")
    private String designId;
    @ApiModelProperty("订单ID（每次返回id不同）")
    private String orderId;
    @ApiModelProperty("广告主ID")
    private String usrId;
    @ApiModelProperty("应用ID")
    private String appId;
    /*    @ApiModelProperty("创意标题")
        private String designName;*/
    @ApiModelProperty("素材类型，1:大图，2：视频，3：长图，4：左图右文，5：右文左图")
    private String materialType;
    @ApiModelProperty("创意素材")
    private String materialUrl;
    @ApiModelProperty("广告主logo")
    private String userLogo;
    @ApiModelProperty("产品名称")
    private String productName;
    @ApiModelProperty("广告主名称")
    private String userName;
    @ApiModelProperty("跳转链接类型")
    private String putInMethod;
    @ApiModelProperty("跳转链接")
    private String putInValue;
    @ApiModelProperty("广告来源,默认:98_adv=98自投")
    private String advSource;
    @ApiModelProperty("视频广告自动播放开启声音,默认：true")
    private boolean isView;
    @ApiModelProperty("广告类型")
    private String positionType;
    @ApiModelProperty("广告规格")
    private String styleSize;
    @ApiModelProperty("出价")
    private double price;
    @ApiModelProperty("文案")
    private String ideaTitle;
    @ApiModelProperty("按钮文案")
    private String buttonIdeaTitle;
}
