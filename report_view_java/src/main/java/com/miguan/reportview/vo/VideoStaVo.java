package com.miguan.reportview.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统计视频进文量和下线量(video_sta)实体类
 *
 * @author zhongli
 * @since 2020-08-07 20:35:51
 * @description 
 */
@Data
@NoArgsConstructor
public class VideoStaVo implements Serializable {
    /**
     * 分类id
     */
    private String catId;
    /**
     * 视频量
     */
    private Long num;
    /**
     * 0=下线 1=进文2=汇总各分类视频总数3=新上线视频数4=新下线视频数5=线上视频数6=新采集视频数
     */
    private int type;
    /**
     * 日期
     */
    private String date;

}