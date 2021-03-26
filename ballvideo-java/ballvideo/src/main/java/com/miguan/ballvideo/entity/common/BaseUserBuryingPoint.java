package com.miguan.ballvideo.entity.common;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

/**
 * 埋点数据父类
 * @Author hyl
 * @Date 2019年10月31日10:04:44
 **/
@MappedSuperclass
@Data
public class BaseUserBuryingPoint implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("设备id")
    private String deviceId;

    @ApiModelProperty("app版本")
    private String appVersion;

    @ApiModelProperty("手机版本")
    private String osVersion;

    @ApiModelProperty("创建时间")
    private Date creatTime;

    @ApiModelProperty("启动事件标识")
    private String actionId;

    @ApiModelProperty("新老用户:10-新用户，20老用户")
    private int isNew = 20;

    @ApiModelProperty("来源渠道号")
    private String channelId;

    @ApiModelProperty("是否首次访问:1-是，0-否")
    private int isFirstView;

    @ApiModelProperty("是否首次启动:1-是，0-否")
    private int isFirstLoad;

    @ApiModelProperty("是否从后台唤醒:1-是，0-否")
    private int resumeFromBack;

    @ApiModelProperty("ICON类别:10-主页 ,20-小视频 ,30-我的")
    private int iconType;

    /**
     * 短视频播放：1.首次加载,2.刷新首页，
     * 短视频详情点击：10-主页（或列表）点击，20-详情内推荐，30-观看历史点击，40-我的收藏点击
     * 小视频播放: 1-小视频列表,2-详情切换,3-观看历史,4-我的收藏
     * */
    @ApiModelProperty("视频来源")
    private int source;

    @ApiModelProperty("分类栏目id")
    private Long catid;

    @ApiModelProperty("视频ID")
    private Long videoId;

    @ApiModelProperty("分享来源: 1-微信，2-朋友圈,3-QQ好友，4-QQ空间，5-复制链接")
    private int shareWay;

    @ApiModelProperty("图片ID(展示在WIFI通知栏图片的ID)")
    private String picId;

    @ApiModelProperty("播放时长(短视频实际播放的时间)")
    private String videoPlayTime;

    @ApiModelProperty("视频总时长")
    private String videoTime;

    @ApiModelProperty("完整播放率")
    private Double videoRate;

    @ApiModelProperty("手机系统类型：andriod/ios")
    private String systemVersion;

    @ApiModelProperty("php统计用到")
    private String createDate;

    @ApiModelProperty("广告位ID(广告位置key)")
    private String adZoneId;

}
