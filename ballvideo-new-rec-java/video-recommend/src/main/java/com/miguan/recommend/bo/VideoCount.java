package com.miguan.recommend.bo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VideoCount {

    private String videoId;
    /**
     * 曝光数
     */
    private long show;
    /**
     * 播放数
     */
    private long play;
    /**
     * 有效播放数
     */
    private long vaildPlay;

    /**
     * 超过10秒的播放数
     */
    private long playOver;

    private long playTimeR;
    /**
     * 创建日期：yyyy-MM-DD
     */
    private String createDay;

    public VideoCount(){

    }

    public VideoCount(long show, long play, long vaildPlay, long playTimeR){
        this.show = show;
        this.play = play;
        this.vaildPlay = vaildPlay;
        this.playTimeR = playTimeR;
    }

    public VideoCount(long show, long play, long vaildPlay, long playTimeR, long playOver){
        this.show = show;
        this.play = play;
        this.vaildPlay = vaildPlay;
        this.playTimeR = playTimeR;
        this.playOver = playOver;
    }

    public VideoCount(String videoId, long show, long play, long vaildPlay, long playTimeR, long playOver){
        this.videoId = videoId;
        this.show = show;
        this.play = play;
        this.vaildPlay = vaildPlay;
        this.playTimeR = playTimeR;
        this.playOver = playOver;
    }

    public String getRedisValue() {
        return show + "," + play + "," + vaildPlay + "," + playTimeR+ "," + playOver;
    }
}
