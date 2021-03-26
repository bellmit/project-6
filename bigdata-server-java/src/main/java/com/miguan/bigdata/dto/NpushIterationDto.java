package com.miguan.bigdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NpushIterationDto {

    /**
     * 时间戳
     */
    private Long timestamp;
    /**
     * 包名
     */
    private String appPackage;


    /**
     * 项目：来电-1，视频-2，百步赚-3
     */
    private Integer projectType;

    /**
     * 用户激活日总数(小于等于总数的激活日用户都需要进行推送)
     */
    private Integer usrActCnt;

    /**
     * 批次(今日第几次推送)
     */
    private Integer batchNum;

    /**
     * 最大批次(一共推送几次)
     */
    private Integer maxBatchNum;
}
