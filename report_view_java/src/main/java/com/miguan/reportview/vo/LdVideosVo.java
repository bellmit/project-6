package com.miguan.reportview.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LdVideosVo implements Serializable {
    private static final long serialVersionUID = 1L;

    //来电秀id
    private Integer id;
    //标题
    private String title;
    //源视频url
    private String url;
    //分类id
    private Integer catId;
    //分类名称
    private String catName;
    //源音频url
    private String urlAudio;
    //源图片url
    private String urlImg;
    //本地视频地址
    private String localUrl;
    //白山云视频地址
    private String bsyUrl;
    //本地图片地址
    private String localImgUrl;
    //白山云图片地址
    private String bsyImgUrl;
    //点赞数量
    private Integer likeCount;
    //分享数量
    private Integer shareCount;
    //用户id
    private Integer userId;
    //用户名称
    private String userName;
    //点击数
    private Integer clickCount;
    //创建时间
    private Date createdAt;
    //
    private Date updatedAt;
    //状态 1开启 2关闭
    private Integer state;
    //用户收藏数量
    private Integer userLikeCount;
    //本地音频地址
    private String localAudioUrl;
    //白山云音频地址
    private String bsyAudioUrl;
    //强制广告：0否 1是
    private Integer forceAdv;
    //来电设置成功数
    private Integer successNum;
    //0不推荐 1推荐
    private Integer recommend;
    //上传视频类型：1自己用 2大家用
    private String type;
    //审批状态（视频类型为大家用时用） 0待审核 1审核不通过 2审核通过
    private String approvalState;
    //时长
    private String videoTime;
    //马甲类型
    private String appType;
    //基础权重值
    private Integer baseWeight;
    //快捷设置：1是 0否
    private Integer quickSetting;
    //宽
    private String width;
    //高
    private String height;
    //视频标签id,以,隔开
    private String videoLabelIds;
    //视频标签名称,多个以,隔开
    private String videoLabelNames;
    //是否配置推送。1是。0否
    private Integer isPush;
}
