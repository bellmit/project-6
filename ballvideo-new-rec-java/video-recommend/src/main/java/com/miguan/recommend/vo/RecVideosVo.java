package com.miguan.recommend.vo;

import lombok.Data;

import java.util.Date;

@Data
public class RecVideosVo {

    //主键
    private Long id;
    //视频标题
    private String title;
    //分类id
    private Long catId;
    //合集ID
    private Long gatherId;
    //敏感词 -3疑似敏感,-1未处理,-2非敏感词,1级敏感词
    private Integer sensitive;
    //是否激励视频 1:是,0:否
    private Integer incentiveVideo;
    //状态 1上线 2预上线。-1入库。 -3下线
    private int state;
    //上传类型 0 抓取视频  1用户上传视频
    private int updateType;
    //视频时长
    private String videoTime;
    // 上线事件
    private Date onlineDate;
    private String videosSource;
    //视频url
    private String videoUrl;
    //背景图片url
    private String bsyImgUrl;
    //视频大小，单位：M
    private String videoSize;
}
