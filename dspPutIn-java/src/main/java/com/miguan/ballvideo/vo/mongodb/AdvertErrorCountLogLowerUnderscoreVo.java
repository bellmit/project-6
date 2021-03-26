package com.miguan.ballvideo.vo.mongodb;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author laiyd
 * @Date 2020/6/9
 **/
@Data
@Document(collection="ad_error_count_log")
public class AdvertErrorCountLogLowerUnderscoreVo {

    private String _id;

    @ApiModelProperty("设备id")
    private String device_id;

    @ApiModelProperty("广告平台（例如：穿山甲-chuan_shan_jia，广点通-guang_dian_tong，广告-98_adv）")
    private String plat_key;

    @ApiModelProperty("广告位置Id")
    private String position_id;

    @ApiModelProperty("代码位ID：第三方或98广告后台生成的广告ID")
    private String ad_id;

    @ApiModelProperty("广告类型（例如：信息流-c_flow，插屏广告-c_table_screen，Draw信息流广告-c_draw_flow）")
    private String type_key;

    @ApiModelProperty("包名：ex(xld,wld)")
    private String app_package;

    @ApiModelProperty("版本")
    private String app_version;

    @ApiModelProperty("手机类型 1ios 2Android")
    private String mobile_type;

    @ApiModelProperty("渲染方式")
    private String render;

    @ApiModelProperty("渠道ID")
    private String channel_id;

    @ApiModelProperty("sdk版本号")
    private String sdk;

    //统计字段
    @ApiModelProperty("请求成功")
    private int request_success;

    @ApiModelProperty("请求失败")
    private int request_failed;

    @ApiModelProperty("渲染成功")
    private int render_success;

    @ApiModelProperty("渲染失败")
    private int render_failed;

    @ApiModelProperty("展示成功")
    private int show_success;

    @ApiModelProperty("展示失败")
    private int show_failed;

    @ApiModelProperty("请求总次数")
    private int total_num;

    @ApiModelProperty("创建时间")
    private String creat_time;

    @ApiModelProperty("创建时间(APP传递时间)")
    private Long app_time;
}
