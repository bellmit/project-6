package com.xiyou.speedvideo.constant;

/**
 * description: 视频倍速脚本
 *
 * @author huangjx
 * @date 2020/10/15 9:57 上午
 */
public class FFMpegConstant {

    /**
     * 8倍速
     */
    public static String FFMPEG_8X = "ffmpeg -i %s -filter_complex \"[0:v]setpts=0.125*PTS[v];[0:a]atempo=8.0[a]\" -map \"[v]\" -map \"[a]\" %s";

    /**
     * 4倍速
     */
    public static String FFMPEG_4X = "ffmpeg -i %s -filter_complex \"[0:v]setpts=0.25*PTS[v];[0:a]atempo=4.0[a]\" -map \"[v]\" -map \"[a]\" %s";

    /**
     * 2倍速
     */
    public static String FFMPEG_2X = "ffmpeg -i %s -filter_complex \"[0:v]setpts=0.5*PTS[v];[0:a]atempo=2.0[a]\" -map \"[v]\" -map \"[a]\" %s";


}
