package com.miguan.ballvideo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 首页推荐接口参数对象
 * @Author shixh
 * @Date 2019/11/30
 * @version 1.8
 **/
@Data
public class VideoWeChatParamsDto {
    String userId;//用户ID
    String deviceId;//设备ID
    @NotBlank(message = "'openId'不能为空")
    @ApiModelProperty("(必传)openId")
    String openId;
    String positionType;//广告位置类型,空则查询全部
    String mobileType;//手机类型：1-ios，2：安卓
    String channelId;//渠道ID
    String permission;//是否开启储存、IMEI权限：0-未开启，1-开启，安卓调用参数，IOS不传
    String videoDuty;//视频占比，例如4:3:1，首次访问为空
    String appVersion;
    String appPackage;
    String lastCatId;//如果没有点击主标签和其他标签，只点击了最近3日的视频，返回它的分类ID作为主标签

    /**
     * 2.7.4版本表示设备的唯一标识
     */
    @ApiModelProperty(hidden = true)
    String distincId;
    @ApiModelProperty(hidden = true)
    String uuid;
    /* 是否合并广告，默认为 1*/
    String isCombine = "1";

    //version2.1.0新增集合视频
    String gatherIds;//要屏蔽的集合ID

    //非前端传递参数字段
    String catId;
    String catIds;
    String otherCatIds;//需要屏蔽的分类
    String showedIds;
    int num;
    int recentlyDay;//最近多少天
    String limitParam;//分页参数，如limitParam=“0,10”，表示 limit 0,10

}
