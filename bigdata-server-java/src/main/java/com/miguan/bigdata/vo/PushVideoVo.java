package com.miguan.bigdata.vo;

import lombok.Data;

@Data
public class PushVideoVo {
    /**
     * 视频id
     */
    private Integer videoId;

    /**
     * 观看数(播放数)
     */
    private Integer playNum;

    /**
     * 有效播放数
     */
    private Integer vplayNum;

    /**
     * 完播数
     */
    private Integer playEndNum;
}
