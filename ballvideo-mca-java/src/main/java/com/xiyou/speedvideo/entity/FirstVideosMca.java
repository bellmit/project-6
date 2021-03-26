package com.xiyou.speedvideo.entity;

import lombok.Data;

import java.util.Date;

/**
 * description:
 *
 * @author huangjx
 * @date 2020/10/14 9:54 下午
 */
@Data
public class FirstVideosMca {

    /**
     * 下载中
     */
    public static final Integer STATE_DOWNLOADING = 0;

    /**
     * 下载完成
     */
    public static final Integer STATE_DOWNLOAD_COMPLETE = 1;

    /**
     * 下载失败
     */
    public static final Integer STATE_DOWNLOAD_ERROR = 10;

    /**
     * 视频倍速完成
     */
    public static final Integer STATE_SPEED_COMPLETE = 2;

    private Integer videoId;
    private String bsyUrl;
    private String videoTime;
    private Date createAt;
    private Date updateAt;
    private Integer state;
    private String localPath;

}
