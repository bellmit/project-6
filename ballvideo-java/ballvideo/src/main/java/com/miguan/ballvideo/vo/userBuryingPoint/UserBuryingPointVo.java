package com.miguan.ballvideo.vo.userBuryingPoint;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class UserBuryingPointVo{

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

    @ApiModelProperty("设备权限Imei")
    private String imei;

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

    @ApiModelProperty("恢复数据对象文件名称")
    private String fileName;

    @ApiModelProperty("广告位ID(广告位置key)")
    private String adZoneId;

    @ApiModelProperty("新老用户:10-新用户，20老用户")
    private int isNew = 20;

    //包名初始化，完成新老版本的向上兼容
    @ApiModelProperty("App包名")
    private String appPackage = "com.mg.xyvideo";

    //HYL 2020年3月10日15:03:29  新增西柚视频埋点2.2.0
    @ApiModelProperty("是否完整播放")
    private String isPlayOver;

    @ApiModelProperty("是否展开全屏")
    private String isFullscreen;

    @ApiModelProperty("合集ID")
    private String videoCollectionId;

    @ApiModelProperty("合集名称")
    private String videoCollectionName;

    //@ApiModelProperty("合集点击来源")
    //private String playSource;

    @ApiModelProperty("提示弹窗点击")
    private String clickResult;

    @ApiModelProperty("是否设置成功")
    private String setResult;

    @ApiModelProperty("广告id")
    private String xId;

    @ApiModelProperty("广告展示是否成功  0：成功  1：失败")
    private String state;

    //2020年4月20日10:10:41  HYL  新增V2.5.0  埋点
    @ApiModelProperty("是否合集")
    private String isCollection;

    @ApiModelProperty("具体来源")
    private String otherSource;

    @ApiModelProperty("是否是阶梯广告")
    private String isDifferentiaAdv;

    @ApiModelProperty("视频类型 10-短视频；20-小视频 30-激励视频")
    private Integer type;

    @ApiModelProperty("代码位ID")
    private String adId;

    @ApiModelProperty("广告商来源:0:广点通；1：穿山甲；2：98广告；3：快手")
    private Integer adSource;

    @ApiModelProperty("用户解锁方式，10不解锁；11首页点击；12提前解锁；13弹窗解锁")
    private Integer unlockType;

    @ApiModelProperty("专辑id")
    private Long albumId = 0L;

    @ApiModelProperty("专辑名称")
    private String albumName;

    @ApiModelProperty("1：微信；2：链接")
    private Integer shareType;
}