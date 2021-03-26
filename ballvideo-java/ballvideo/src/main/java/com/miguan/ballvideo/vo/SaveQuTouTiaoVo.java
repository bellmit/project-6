package com.miguan.ballvideo.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SaveQuTouTiaoVo {

    private String cid;             // 广告创意id
    private String imei;            // 安卓唯一标识
    private Integer os;             // 客户端类型,0-Android,1-IOS,2-WP,3-Others
    private String timestamp;       //时间戳
    private String plan;            //计划 ID 原值
    private String unit;            //单元 ID 原值
    private String adid;            // 广告计划 id
    private String idfa;            // ios唯一标识
    private String callback_url;        //回调参数
    private String imeimd5;           //用户客户端的 IMEI 号的 MD5 码 取md5sum摘要
    private String tsms;             //客户端触发监控的时间
    private String androidid;       // 安卓 ID 原值
    private String uid;             //用户 ID 原值

}
