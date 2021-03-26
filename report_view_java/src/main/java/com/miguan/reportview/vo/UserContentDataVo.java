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
public class UserContentDataVo {
    /**
     * 视频播放数
     */
    private double playCount;
    /**
     * 视频爆光数
     */
    private double showCount;
    /**
     * 视频有效播放数
     */
    private double vplayCount;
    /**
     * 视频播放时长
     */
    private long playTime;
    /**
     * 视频实际播放时长
     */
    private long playTimeReal;

    /**
     * 视频总时长
     */
    private long videoTime;
    /**
     * 完整播放数
     */
    private double allPlayCount;
    /**
     * 评论数
     */
    private double reviewCount;
    /**
     * 点赞数
     */
    private double likeCount;
    /**
     * 收藏数
     */
    private double favCount;
    /**
     * 播放用户数
     */
    private double playUser;
    /**
     * 爆光用户数
     */
    private double showUser;
    /**
     * 评论用户数
     */
    private double reviewUser;
    /**
     * 点赞用户数
     */
    private double likeUser;
    /**
     * 收藏用户数
     */
    private double favUser;
    /**
     * 有效播放用户数
     */
    private Integer vplayUser;
    /**
     * 日活
     */
    private double activeUser;

    /**
     * 日期
     */
    private String dd;
    private String packageName;
    private String appVersion;
    private String isNew;
    private String fatherChannel;
    private String channel;
    private String catid;
    private String videosSource;
}
