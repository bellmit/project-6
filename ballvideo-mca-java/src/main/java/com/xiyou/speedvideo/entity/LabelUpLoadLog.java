package com.xiyou.speedvideo.entity;

import lombok.Data;

/**
 * @Description TODO
 * @Author zhangbinglin
 * @Date 2021/1/29 16:01
 **/
@Data
public class LabelUpLoadLog {

    private Integer videoId;  //视频id

    private String bsyUrl;  //视频url

    private Integer type;  //类型：1--百度云AI，2--算法解析

    private String param;  //接口调用参数

    private String result; //接口请求结果

    public LabelUpLoadLog(Integer videoId, String bsyUrl, Integer type, String param, String result) {
        this.videoId = videoId;
        this.bsyUrl = bsyUrl;
        this.type = type;
        this.param = param;
        this.result = result;
    }
}
