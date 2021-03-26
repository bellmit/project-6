package com.miguan.bigdata.vo;

import lombok.Data;

/**
 * 激励视频出库、入库
 */
@Data
public class IncentiveVideoVo {

    //激励视频id
    private Long videoId;

    //分类id
    private Long catId;

    //播放率
    private Double playRate;

    //有效播放率
    private Double vplayRate;

    //视频完播放率
    private Double allPlayRate;

    private Integer isNew = 0;
}
