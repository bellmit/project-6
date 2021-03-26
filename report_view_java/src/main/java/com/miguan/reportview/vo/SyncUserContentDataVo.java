package com.miguan.reportview.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 同步用户运营数据vo
 */
@Data
public class SyncUserContentDataVo {

    /**
     * 视频播放数
     */
    private int playCount;
    /**
     * 视频爆光数
     */
    private int showCount;
    /**
     * 视频有效播放数
     */
    private int vplayCount;
    /**
     * 视频总时长
     */
    private long playTime;
    /**
     * 视频实际播放时长
     */
    private long playTimeReal;
    /**
     * 完整播放数
     */
    private int allPlayCount;
    /**
     * 评论数
     */
    private int reviewCount;
    /**
     * 点赞数
     */
    private int likeCount;
    /**
     * 收藏数
     */
    private int favCount;
    /**
     * 播放用户数
     */
    private int playUser;
    /**
     * 爆光用户数
     */
    private int showUser;
    /**
     * 评论用户数
     */
    private int reviewUser;
    /**
     * 点赞用户数
     */
    private int likeUser;
    /**
     * 收藏用户数
     */
    private int favUser;
    /**
     * 有效播放用户数
     */
    private int vplayUser;
    /**
     * 日活
     */
    private int activeUser;

    /**
     * 有效行为用户数
     */
    private int vbUser;

    /**
     * 广告点击总次数
     */
    private int adClick;

    /**
     * 广告点击用户
     */
    private int adclickUser;

    /**
     * 首页浏览用户数
     */
    private int indexPageUser;

    /**
     * 新增用户数
     */
    private int newUser;

    /**
     * 注册用户数
     */
    private int regUser;

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


    public void setIsNew(String isNew) {
        if("0".equals(isNew)) {
            isNew = "";
        }
        if("1".equals(isNew)) {
            isNew = "0";
        }
        if("2".equals(isNew)) {
            isNew = "1";
        }
        this.isNew = isNew;
    }

    public void setCatid(String catid) {
        if("0".equals(catid)) {
            catid = "";
        }
        if("-1".equals(catid)) {
            catid = "0";
        }
        this.catid = catid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SyncUserContentDataVo) {
            SyncUserContentDataVo bqd = (SyncUserContentDataVo)obj;
            String one = bqd.getDd() + "-" + bqd.getPackageName() + "-" + bqd.getAppVersion() + "-" + bqd.getIsNew() + "-" + bqd.getFatherChannel() + "-" +bqd.getChannel() + "-" + bqd.getCatid();
            String two = dd + "-" + packageName + "-" + appVersion + "-" + isNew + "-" + fatherChannel + "-" + channel + "-" + catid;
            return one.equals(two);
        }
        return false;
    }

}
