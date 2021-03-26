package com.miguan.ballvideo.vo.video;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 流量变现平台使用
 * @author suhj
 * @date 2020/08/07
 **/
@ApiModel("流量变现平台广告VO")
@Data
public class AdvertFlowCodeVo {

    @ApiModelProperty("广告代码位主键ID")
    private Long id;

    @ApiModelProperty("代码位ID：第三方或98广告后台生成的广告ID")
    private String adId;

    @ApiModelProperty("首次加载位置")
    private Integer firstLoadPosition;

    @ApiModelProperty("再次加载位置")
    private Integer secondLoadPosition;

    @ApiModelProperty("banner广告展示次数限制")
    private Integer maxShowNum;

    @ApiModelProperty("广告平台（例如：穿山甲-chuan_shan_jia，广点通-guang_dian_tong，广告-98_adv）")
    private String plat;

    @ApiModelProperty("广告类型（例如：信息流-c_flow，插屏广告-c_table_screen，Draw信息流广告-c_draw_flow）")
    private String adType;


    @ApiModelProperty("渲染方式（例如：模版渲染-c_template_render，自渲染-c_self_render）")
    private String render;

    @ApiModelProperty("广告素材（例如：图文-g_img_text，视频-g_video，图文+视频-g_img_text_video）")
    private String material;

    @ApiModelProperty("广告标题")
    private String title;

    @ApiModelProperty("落地页/应用包地址")
    private String url;

    @ApiModelProperty("落地页应用包类型：0-落地页，1-应用包")
    private String linkType;

    @ApiModelProperty("原生图片路径")
    private String imgPath;

    @ApiModelProperty("当前广告是否需要权限（测试用）")
    private String permission;

    @ApiModelProperty("阶梯价格")
    private String ladderPrice;

}
