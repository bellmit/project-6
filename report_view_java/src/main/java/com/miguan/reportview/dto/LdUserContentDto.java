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
public class LdUserContentDto {
    @ApiModelProperty(value = "详情页播放次数", position = 1)
    @Excel(name = "详情页播放次数", orderNum = "6")
    private Integer detailPlayCount;

    @ApiModelProperty(value = "详情页播放用户", position = 2)
    @Excel(name = "详情页播放用户", orderNum = "7")
    private Integer detailPlayUser;

    @ApiModelProperty(value = "设置次数", position = 3)
    @Excel(name = "设置次数", orderNum = "8")
    private Integer setCount;

    @ApiModelProperty(value = "设置用户", position = 4)
    @Excel(name = "设置用户", orderNum = "9")
    private Integer setUser;

    @ApiModelProperty(value = "成功设置次数", position = 5)
    @Excel(name = "成功设置次数", orderNum = "10")
    private Integer setConfirmCount;

    @ApiModelProperty(value = "成功设置用户", position = 6)
    @Excel(name = "成功设置用户", orderNum = "11")
    private Integer setConfirmUser;

    @ApiModelProperty(value = "来电秀曝光量", position = 7)
    @Excel(name = "来电秀曝光量", orderNum = "12")
    private Integer videoShowCount;

    @ApiModelProperty(value = "曝光用户", position = 8)
    @Excel(name = "曝光用户", orderNum = "13")
    private Integer videoShowUser;

    @ApiModelProperty(value = "来电详情播放率", position = 9)
    @Excel(name = "来电详情播放率", orderNum = "14")
    private Double ldPlayRate;

    @ApiModelProperty(value = "用户平均播放数", position = 10)
    @Excel(name = "用户平均播放数", orderNum = "15")
    private Double prePlaySpeed;

    @ApiModelProperty(value = "用户转化率", position = 11)
    @Excel(name = "用户转化率", orderNum = "16")
    private Double userConversionRate;

    @ApiModelProperty(value = "设置成功率", position = 12)
    @Excel(name = "设置成功率", orderNum = "17")
    private Double setConfirmRate;

    @ApiModelProperty(value = "人均曝光量", position = 13)
    @Excel(name = "人均曝光量", orderNum = "18")
    private Double preShowUser;

    @ApiModelProperty(value = "收藏量", position = 14)
    @Excel(name = "收藏量", orderNum = "19")
    private Integer videoCollectCount;

    @ApiModelProperty(value = "收藏用户", position = 15)
    @Excel(name = "收藏用户", orderNum = "20")
    private Integer videoCollectUser;

    @ApiModelProperty(value = "分享量", position = 16)
    @Excel(name = "分享量", orderNum = "21")
    private Integer shareCount;

    @ApiModelProperty(value = "分享用户", position = 17)
    @Excel(name = "分享用户", orderNum = "22")
    private Integer shareUser;

    @ApiModelProperty(value = "分享率", position = 18)
    @Excel(name = "分享率", orderNum = "23")
    private Double shareRate;

    @ApiModelProperty(value = "设置来电秀成功数", position = 19)
    @Excel(name = "设置来电秀成功数", orderNum = "24")
    private Integer setPhoneCount;

    @ApiModelProperty(value = "设置来电秀成功率", position = 20)
    @Excel(name = "设置来电秀成功率", orderNum = "25")
    private Double setPhoneRate;

    @ApiModelProperty(value = "设置锁屏成功数", position = 21)
    @Excel(name = "设置锁屏成功数", orderNum = "26")
    private Integer setLockScreenCount;

    @ApiModelProperty(value = "设置锁屏成功率", position = 22)
    @Excel(name = "设置锁屏成功率", orderNum = "27")
    private Double setLockScreenRate;

    @ApiModelProperty(value = "设置壁纸成功数", position = 23)
    @Excel(name = "设置壁纸成功数", orderNum = "28")
    private Integer setWallpaperCount;

    @ApiModelProperty(value = "设置壁纸成功率", position = 24)
    @Excel(name = "设置壁纸成功率", orderNum = "29")
    private Double setWallpaperRate;

    @ApiModelProperty(value = "设置皮肤成功数", position = 25)
    @Excel(name = "设置皮肤成功数", orderNum = "30")
    private Integer setSkinCount;

    @ApiModelProperty(value = "设置皮肤成功率", position = 26)
    @Excel(name = "设置皮肤成功率", orderNum = "31")
    private Double setSkinRate;

    @ApiModelProperty(value = "铃声试听数", position = 27)
    @Excel(name = "铃声试听数", orderNum = "32")
    private Integer ringAuditionCount;

    @ApiModelProperty(value = "铃声试听用户数", position = 28)
    @Excel(name = "铃声试听用户数", orderNum = "33")
    private Integer ringAuditionUser;

    @ApiModelProperty(value = "试听转化率", position = 29)
    @Excel(name = "试听转化率", orderNum = "34")
    private Double ringAuditionRate;

    @ApiModelProperty(value = "铃声设置成功转化", position = 30)
    @Excel(name = "铃声设置成功转化", orderNum = "35")
    private Double setRingConfirmRate;

    @ApiModelProperty(value = "点击设铃声次数", position = 31)
    @Excel(name = "点击设铃声次数", orderNum = "36")
    private Integer clickSetRingCount;

    @ApiModelProperty(value = "点击设铃声用户", position = 32)
    @Excel(name = "点击设铃声用户", orderNum = "37")
    private Integer clickSetRingUser;

    @ApiModelProperty(value = "成功设置铃声次数", position = 33)
    @Excel(name = "成功设置铃声次数", orderNum = "38")
    private Integer setRingConfirmCount;

    @ApiModelProperty(value = "成功设置铃声用户", position = 34)
    @Excel(name = "成功设置铃声用户", orderNum = "39")
    private Integer setRingConfirmUser;

    @ApiModelProperty(value = "人均启动次数", position = 35)
    @Excel(name = "人均启动次数", orderNum = "40")
    private Double preAppStart;

    @ApiModelProperty(value = "人均在线时长", position = 36)
    @Excel(name = "人均在线时长", orderNum = "41")
    private Double prePlayTime;



    @Excel(name = "日期")
    private String date;

    @Excel(name = "版本号", orderNum = "1")
    private String appVersion;

    @Excel(name = "是否新用户", orderNum = "2")
    private String isNewApp;

    @Excel(name = "父渠道", orderNum = "3")
    private String fatherChannel;

    @Excel(name = "渠道", orderNum = "4")
    private String channel;

    @Excel(name = "分类", orderNum = "5")
    private String videoType;
}
