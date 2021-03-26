package com.miguan.reportview.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhongli
 * @date 2020-08-05 
 *
 */
@Setter
@Getter
public class ChannelDataVo {
    /**
     * 新增用户
     */
    private double newUser;
    /**
     * 注册用户
     */
    private double regUser;
    /**
     * 播放视频用户数
     */
    private double playUser;
    /**
     * 有效播放视频用户数
     */
    private double vplayUser;
    /**
     * 首页浏览用户
     */
    private double indexPageUser;
    /**
     * 广告点击用户
     */
    private double adclickUser;

    /**
     * 广告点击总次数
     */
    private double adClick;
    /**
     * 视频播放总时长
     */
    private double playTime;
    /**
     * 播放视频总数
     */
    private double playCount;
    /**
     * 有效播放视频数
     */
    private double vplayCount;
    /**
     * 有效行为用户
     */
    private double vbUser;

    /**
     * 日活用户
     */
    private double user;
    private String dd;

    private String packageName;
    private String appVersion;
    private String isNew;
    private String fatherChannel;
    private String channel;
    /**
     * 存量用户
     */

    private double newStockUser;
}
