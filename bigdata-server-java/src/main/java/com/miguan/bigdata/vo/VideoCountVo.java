package com.miguan.bigdata.vo;

import lombok.Data;

@Data
public class VideoCountVo {
    /**
     * 视频ID
     */
    Integer videoId;
    /**
     * 分类ID
     */
    Integer catid;
    /**
     * 有效播放数
     */
    Integer vplayCount;
    /**
     * 有效播放率
     */
    Double vplayRate;
}
