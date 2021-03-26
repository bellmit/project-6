package com.miguan.reportview.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LdUserContentDataVo {
    private String dd;   //日期
    private String appVersion;   //版本
    private String isNewApp;   //新老用户
    private String fatherChannel;   //父渠道
    private String channel;   //渠道
    private String videoType;   //分类
    private Integer activeUser;   //活跃用户数
    private Integer newUser;   //新增用户数
    private Integer registerUser;   //注册用户数
    private Integer detailPlayCount;   //详情页播放次数
    private Integer detailPlayUser;   //详情页播放用户
    private Integer setCount;   //设置次数
    private Integer setUser;   //设置用户
    private Integer setConfirmCount;   //成功设置次数
    private Integer setConfirmUser;   //成功设置用户
    private Integer videoShowCount;   //来电秀曝光量
    private Integer videoShowUser;   //曝光用户
    private Integer videoCollectCount;   //收藏量
    private Integer videoCollectUser;   //收藏用户
    private Integer shareCount;   //分享量
    private Integer shareUser;   //分享用户
    private Integer setPhoneCount;   //设置来电秀成功数
    private Integer setPhoneUser;   //设置来电秀成功用户数
    private Integer setLockScreenCount;   //设置锁屏成功数
    private Integer setLockScreenUser;   //设置锁屏成功用户数
    private Integer setWallpaperCount;   //设置壁纸成功数
    private Integer setWallpaperUser;   //设置壁纸用户数
    private Integer setSkinCount;   //设置皮肤成功数
    private Integer setSkinUser;   //设置皮肤成功用户数
    private Integer ringAuditionCount;   //铃声试听数
    private Integer ringAuditionUser;   //铃声试听用户数
    private Integer browseRingCount;   //浏览铃声页面用户数
    private Integer clickSetRingCount;   //点击设铃声次数
    private Integer clickSetRingUser;   //点击设铃声用户
    private Integer setRingConfirmCount;   //成功设置铃声次数
    private Integer setRingConfirmUser;   //成功设置铃声用户
    private Integer appStartCount;   //app启动次数
    private Long playTime;   //使用总时长
    private Integer browseHomePageUser;   //首页浏览用户
    private Integer adClickUser;   //广告点击用户
    private Integer adClickCount;   //广告点击次数
    private Integer adShowCount;   //广告曝光数
    private Integer adShowUser;   //广告点曝光用户
    private Integer newStockUser; //新增存量用户
}
