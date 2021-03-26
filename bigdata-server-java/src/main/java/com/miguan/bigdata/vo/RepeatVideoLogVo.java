package com.miguan.bigdata.vo;

import lombok.Data;

@Data
public class RepeatVideoLogVo {

    public RepeatVideoLogVo() {
    }

    public RepeatVideoLogVo(Integer videoId, Integer repeatVideoId, Double titleSimScore, Double imgSimScore, Integer logType) {
        this.videoId = videoId;
        this.repeatVideoId = repeatVideoId;
        this.titleSimScore = titleSimScore;
        this.imgSimScore = imgSimScore;
        this.logType = logType;
    }

    //视频id
    private Integer videoId;

    //相似的视频id
    private Integer repeatVideoId;

    //标题相似度(取值范围：0至1)
    private Double titleSimScore;

    //封面图片相似度(取值范围：0至1)
    private Double imgSimScore;

    //类型，0--根据标题和图像，1--只根据图像，2--只根据标题
    private Integer logType;
}
