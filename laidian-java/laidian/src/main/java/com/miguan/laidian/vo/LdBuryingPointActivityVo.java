package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class LdBuryingPointActivityVo {

    @ApiModelProperty(value = "埋点类型")
    private String pointType;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "设备id")
    private String deviceId;

    @ApiModelProperty(value = "新老用户:10-新用户，20老用户")
    private Integer isNew;

    @ApiModelProperty(value = "APP应用的版本")
    private String appVersion;

    @ApiModelProperty(value = "操作系统版本")
    private String osVersion;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @ApiModelProperty(value = "来源渠道ID")
    private String channelId;

    @ApiModelProperty(value = "活动ID")
    private Long activityId;

    @ApiModelProperty(value = "活动名称")
    private String activityName;

    @ApiModelProperty(value = "入口类型 1.首页悬浮窗2.【我的】banner3.【首页】弹窗")
    private Integer entranceType;

    @ApiModelProperty(value = "是否抽奖 1.是 2.否")
    private Integer isLottery;

    @ApiModelProperty(value = "奖品来源 1.抽奖获得2.签到获得")
    private Integer prizeResource;

    @ApiModelProperty(value = "领取结果 1.立即领取2.观看视频抽手机3.关闭弹窗")
    private Integer receiveResult;

    @ApiModelProperty(value = "剩余抽奖次数")
    private Integer lastNum;

    @ApiModelProperty(value = "用户名")
    private String name;

    @ApiModelProperty(value = "账号")
    private String account;

    @ApiModelProperty(value = "联系方式（手机号/QQ号）")
    private String phone;

    @ApiModelProperty(value = "奖品名称")
    private String prizeName;

    @ApiModelProperty(value = "商品成本")
    private BigDecimal cost;

    @ApiModelProperty(value = "此次完成任务类型 1.观看视频2.设置来电秀成功3.设置铃声成功4.分享活动")
    private Integer taskType;

    @ApiModelProperty("包名")
    private String packageName;

    @ApiModelProperty(value = "签到天数（1-7）")
    private Integer day;
}