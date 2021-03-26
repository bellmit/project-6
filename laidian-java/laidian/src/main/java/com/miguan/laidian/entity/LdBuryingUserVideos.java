package com.miguan.laidian.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LdBuryingUserVideos {

    public static final String INCOMINGCALLVIDEO = "IncomingCallVideo";//设置来电
    public static final String SMALLVIDEOS = "smallVideos";//浏览小视屏

    private Long id;

    private String deviceId;

    private Integer videosId;

    private String operationType;

    private Date createDay;

    /**
     *  app类型
     */
    private String appType;


}