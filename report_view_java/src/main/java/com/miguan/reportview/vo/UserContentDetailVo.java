package com.miguan.reportview.vo;

import lombok.Data;

/**
 * @Description TODO
 * @Author zhangbinglin
 * @Date 2020/9/14 20:00
 **/
@Data
public class UserContentDetailVo {


    //日期
    private String dd;

    //上线日期
    private String onlineDate;

    //进文日期
    private String jwDate;

    //分类
    private String catName;

    //视频标题
    private String videoTitle;

    //是否激励视频
    private String isIncentive;

    //曝光次数
    private Integer showNum;

    //曝光区间
    private String section;

    //分类曝光次数
    private Integer catShowNum;

    //天分类曝光次数
    private Integer ddShowNum;

    //播放次数
    private Integer playNum;

    //分类播放次数
    private Integer catPlayNum;

    //天播放次数
    private Integer ddPlayNum;

    //曝光用户数
    private Integer showUser;

    //播放用户数
    private Integer playUser;

    //播放时长
    private double playTimeR;

    //有效播放次数
    private Integer vplayNum;

    //有效播放用户
    private Integer vplayUser;

    //完播次数
    private Integer allPlayNum;

    //完播用户数
    private Integer allPlayUser;

    //点赞数
    private Integer likeNum;

    //点赞用户数
    private Integer likeUser;

    //分享数
    private Integer shareNum;

    //分享用户数
    private Integer shareUser;

    //收藏数
    private Integer favNum;

    //收藏用户数
    private Integer favUser;
}
