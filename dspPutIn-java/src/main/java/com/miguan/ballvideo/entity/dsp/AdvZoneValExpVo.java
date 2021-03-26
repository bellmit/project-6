package com.miguan.ballvideo.entity.dsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**Dsp广告内容数据集合
 * @Author suhj
 * @Date 2020/4/24
 **/
@ApiModel("广告有效点击/曝光")
@Data
public class AdvZoneValExpVo {
    @ApiModelProperty("广告跟踪id，第三方返回广告id")
    private String aid;
    @ApiModelProperty("广告请求批次id，md5（用户id+毫秒时间戳+四位随机数）")
    private String batch_id;
    @ApiModelProperty("关键字")
    private String ad_key;
    @ApiModelProperty("广告排序，广告在列表中排在第几位（针对首页列表、小视频列表广告位）")
    private String seq__id;
    @ApiModelProperty("列表排序位置，广告排列的相对位置")
    private String q_id;
    @ApiModelProperty("代码位id")
    private String ad_id;
    @ApiModelProperty("广告商来源")
    private String ad_source;
    @ApiModelProperty("sdk版本号")
    private String sdk_version;
    @ApiModelProperty("广告类型")
    private String ad_type;
    @ApiModelProperty("广告素材类型，（图片/视频）第三方返回")
    private String adc_type;
    @ApiModelProperty("计划id")
    private String plan_id;
    @ApiModelProperty("创意id")
    private String design_id;
    @ApiModelProperty("广告主名称")
    private String advertiser;
    @ApiModelProperty("渲染方式")
    private String render_type;
    @ApiModelProperty("阶梯价格，后台配置的价格")
    private String price;
    @ApiModelProperty("事件变量英文名，广告有效曝光-ad_zone_exposure 广告有效点击-ad_zone_valid_click")
    private String action_id;
    @ApiModelProperty("创建时间")
    private String creat_time;
    @ApiModelProperty("self_sdk")
    private String self_sdk;

}
