package com.miguan.ballvideo.common.constants;

public class RecommendConstant {
    /**
     * Redis统计信息字段名称
     */
    public final static String count_day = "day";
    public final static String count_show = "show";
    public final static String count_play = "play";
    public final static String count_vaild_play = "vaildPlay";
    public final static String count_play_time_r = "playTimeR";
    public final static String count_active = "active";
    public final static String count_new_user = "newUser";

    /**
     * 前缀
     */
    public static final String prefix = "bg_";
    /**
     * 视频点击率统计信息
     * %s -- 日
     * %s -- 类型 show / play
     * %s -- 视频ID
     */
    public static final String video_click_rate_count = prefix + "videoClickRC:%s:%s:%s";

    /**
     * 视频的统计信息
     * %s -- 日
     * %s -- 视频ID
     */
    public static final String video_count_detail = prefix + "videoCount:%s:%s";

    /**
     * 所有视频的播放总数
     * %s -- 日
     */
    public static final String all_video_play = prefix + "videoCount:%s:allVp";
    /**
     * 所有视频的有效播放总数
     * %s -- 日
     */
    public static final String all_video_vaild_play = prefix + "videoCount:%s:allVVp";
    /**
     * 所有视频的有效播放总时长
     */
    public static final String all_video_play_time_r = prefix + "videoCount:%s:allVptr";
    /**
     *
     */
    public static final String key_user_rec_video_list = prefix + "u_rec_li:%s";
}
