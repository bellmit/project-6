package com.miguan.reportview.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhongli
 * @date 2020-08-05 
 *
 */
@Setter
@Getter
@ApiModel
public class UserContentDto {
    @ApiModelProperty(value = "视频播放数", position = 1)
    @Excel(name = "视频播放数", orderNum = "6")
    private double playNum;
    @ApiModelProperty(value = "视频曝光数", position = 2)
    @Excel(name = "视频曝光数", orderNum = "7")
    private double showNum;
    @ApiModelProperty(value = "视频播放率", notes = "%", position = 3)
    @Excel(name = "视频播放率", orderNum = "8")
    private double playRate;
    @ApiModelProperty(value = "播放用户", position = 4)
    @Excel(name = "播放用户", orderNum = "9")
    private double playUser;
    @ApiModelProperty(value = "有效播放数", position = 5)
    @Excel(name = "有效播放数", orderNum = "10")
    private double vplayNum;
    @ApiModelProperty(value = "曝光用户数", position = 6)
    @Excel(name = "曝光用户数", orderNum = "11")
    private double showUser;
    @ApiModelProperty(value = "人均播放数", position = 7)
    @Excel(name = "人均播放数", orderNum = "12")
    private double perPlayNum;
    @ApiModelProperty(value = "有效播放用户转化率", notes = "%", position = 8)
    @Excel(name = "有效播放用户转化率", orderNum = "13")
    private double vpuRate;
    @ApiModelProperty(value = "人均播放时长", position = 9)
    @Excel(name = "人均播放时长", orderNum = "14")
    private double perPlayTime;
    @ApiModelProperty(value = "完整播放数", position = 10)
    @Excel(name = "完整播放数", orderNum = "15")
    private double allPlayNum;
    @ApiModelProperty(value = "进文量", position = 11)
    @Excel(name = "进文量", orderNum = "16")
    private double jwNum;
    @ApiModelProperty(value = "进文量占比", notes = "%", position = 12)
    @Excel(name = "进文量占比", orderNum = "17")
    private double jwRate;
    @ApiModelProperty(value = "人均曝光数", position = 13)
    @Excel(name = "人均曝光数", orderNum = "18")
    private double perShowNum;
    @ApiModelProperty(value = "播放用户转化率", notes = "%", position = 14)
    @Excel(name = "播放用户转化率", orderNum = "19")
    private double puRate;
    @ApiModelProperty(value = "平均播放进度", position = 15)
    @Excel(name = "平均播放进度", orderNum = "20")
    private double perPlayPro;
    @ApiModelProperty(value = "有效播放率", notes = "%", position = 16)
    @Excel(name = "有效播放率", orderNum = "21")
    private double vplayRate;
    @ApiModelProperty(value = "播放总时长", position = 17)
    @Excel(name = "播放总时长", orderNum = "22")
    private double playTime;
    @ApiModelProperty(value = "完整播放率", notes = "%", position = 18)
    @Excel(name = "完整播放率", orderNum = "23")
    private double allPlayRate;
    @ApiModelProperty(value = "下线量", position = 19)
    @Excel(name = "下线量", orderNum = "24")
    private double offlineNum;
    @ApiModelProperty(value = "评论量", position = 20)
    @Excel(name = "评论量", orderNum = "25")
    private double reviewNum;
    @ApiModelProperty(value = "评论用户", position = 21)
    @Excel(name = "评论用户", orderNum = "26")
    private double reviewUser;
    @ApiModelProperty(value = "评论率", notes = "%", position = 22)
    @Excel(name = "评论率", orderNum = "27")
    private double reviewRate;
    @ApiModelProperty(value = "人均评论数", position = 23)
    @Excel(name = "人均评论数", orderNum = "28")
    private double perReviewNum;
    @ApiModelProperty(value = "点赞量", position = 24)
    @Excel(name = "点赞量", orderNum = "29")
    private double likeNum;
    @ApiModelProperty(value = "点赞用户", position = 25)
    @Excel(name = "点赞用户", orderNum = "30")
    private double likeUser;
    @ApiModelProperty(value = "收藏量", position = 26)
    @Excel(name = "收藏量", orderNum = "31")
    private double favNum;
    @ApiModelProperty(value = "收藏率", notes = "%", position = 27)
    @Excel(name = "收藏率", orderNum = "32")
    private double favRate;
    @ApiModelProperty(value = "人均完播数", position = 28)
    @Excel(name = "人均完播数", orderNum = "33")
    private double preEndPayCount;
    @ApiModelProperty(value = "人均有效播放数", position = 29)
    @Excel(name = "人均有效播放数", orderNum = "34")
    private double preVplayCount;
    @ApiModelProperty(value = "新上线视频数", position = 30)
    @Excel(name = "新上线视频数", orderNum = "35")
    private Integer newOnlineVideoCount;
    @ApiModelProperty(value = "新下线视频数", position = 31)
    @Excel(name = "新下线视频数", orderNum = "36")
    private Integer newOfflineVideoCount;
    @ApiModelProperty(value = "线上视频数", position = 32)
    @Excel(name = "线上视频数", orderNum = "37")
    private Integer olineVideoCount;
    @ApiModelProperty(value = "总视频数", position = 33)
    @Excel(name = "总视频数", orderNum = "38")
    private Integer videoCount;
    @ApiModelProperty(value = "新采集视频数", position = 34)
    @Excel(name = "新采集视频数", orderNum = "39")
    private Integer newCollectVideoCount;
    @ApiModelProperty(value = "有效播放用户", position = 35)
    @Excel(name = "有效播放用户", orderNum = "40")
    private Integer vplayUser;
    @ApiModelProperty(value = "每曝光播放时长", position = 36)
    @Excel(name = "每曝光播放时长", orderNum = "41")
    private Double preShowTime;
    @ApiModelProperty(value = "每播放播放时长", position = 37)
    @Excel(name = "每播放播放时长", orderNum = "42")
    private Double prePlayTime;



    @Excel(name = "日期")
    private String date;
    @Excel(name = "应用", orderNum = "1")
    private String packageName;
    @Excel(name = "版本号", orderNum = "2")
    private String appVersion;
    @Excel(name = "是否新用户", orderNum = "3")
    private String isNew;
    @Excel(name = "父渠道", orderNum = "4")
    private String fatherChannel;
    @Excel(name = "渠道", orderNum = "4")
    private String channel;
    @Excel(name = "分类", orderNum = "5")
    private String catId;
    @Excel(name = "视频来源", orderNum = "5")
    private String videosSource;
}
