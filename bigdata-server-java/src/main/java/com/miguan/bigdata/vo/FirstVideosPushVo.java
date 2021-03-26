package com.miguan.bigdata.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class FirstVideosPushVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	private Long id;
    /**
     * 分类id
     */
    private Long catId;

    /**
     * 分类名称
     */
    private String catName;

    /**
     * 标题
     */
    private String title;
    /**
     * 源视频url
     */
    private String url;
    /**
     * 源音频url
     */
    private String urlAudio;
    /**
     * 源图片url
     */
    private String urlImg;
    /**
     * 源头像地址
     */
    private String urlHeadimg;
    /**
     * 本地视频地址
     */
    private String localUrl;
    /**
     * 本地音频地址
     */
    private String localAudioUrl;
    /**
     * 本地图片地址
     */
    private String localImgUrl;
    /**
     * 白山云视频地址
     */
    private String bsyUrl;
    /**
     * 白山云音频地址
     */
    private String bsyAudioUrl;
    /**
     * 白山云图片地址
     */
    private String bsyImgUrl;
    /**
     * 白云山头像地址
     */
    private String bsyHeadUrl;
    /**
     * 视频作者
     */
    private String videoAuthor;
    /**
     * 收藏数
     */
    private Long collectionCount;
    /**
     * 点赞数
     */
    private Long loveCount;
    /**
     * 评论数
     */
    private Long commentCount;
    /**
     * 观看数
     */
    private Long watchCount;
    /**
     * 点击数
     */
    private Long clickCount;
    /**
     * 曝光数
     */
    private Long showCount;
    /**
     * crt值
     */
    private BigDecimal ctr;
    /**
     * 创建时间
     */
    private Date createdAt;
    /**
     * 更新时间
     */
    private Date updatedAt;
    /**
     * 状态 1上线 2预上线。-1入库。 -3下线
     */
    private Integer state;
    /**
     * 时长
     */
    private String videoTime;
    /**
     * 本地头像
     */
    private String localHeadimg;
    /**
     * 举报数
     */
    private Long report;
    /**
     * 分享数
     */
    private Long shareCount;
    /**
     * 假的分享数
     */
    private Long fakeShareCount;
    /**
     * 完整播放数
     */
    private Long playAllCount;
    /**
     * 基础权重
     */
    private Long baseWeight;
    /**
     * videoSize
     */
    private String videoSize;
    /**
     * 真实权重
     */
    private Long realWeight;
    /**
     * 超过30%时长播放数1
     */
    private Long playCount;
    /**
     * gatherId
     */
    private Long gatherId;
    /**
     * 真实观看数
     */
    private Long watchCountReal;
    /**
     * 真实点赞数
     */
    private Long loveCountReal;
    /**
     * 一小时内视频数
     */
    private Integer videoHour;
    /**
     * 是否激励视频。1:是,0:否
     */
    private Integer isIncentive;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * APP包名
     */
    private String packageName;
    /**
     * 简介
     */
    private String brief;
    /**
     * 视频上线状态。0:入库,1:预上线,2:上线
     */
    private Integer onlineState;
    /**
     * 上传类型 0 抓取视频  1用户上传视频
     */
    private Integer updateType;
    /**
     * 状态 0 未审核 1通过 2不通过
     */
    private Integer examState;
    /**
     * 审核时间
     */
    private Date auditAt;
    /**
     * 视频最大可曝光次数
     */
    private Integer exposureNum;
    /**
     * 比较show和最大曝光结构,优化es查询,show>expo 取-1,否则取1,初始值1
     */
    private Integer compareShowExpo;
    /**
     * 是否下线  0 否  1 是
     */
    private Integer offlineState;
    /**
     * 下线时间
     */
    private Date offlineTime;
    /**
     * 宽
     */
    private String width;
    /**
     * 高
     */
    private String height;
    /**
     * 宽*高
     */
    private String widthHeightText;
    /**
     * 审核日期
     */
    private Date verifyDate;
    /**
     * 上线日期
     */
    private Date onlineDate;
    /**
     * 白山云 m3u8地址
     */
    private String bsyM3u8;
    /**
     * 落地页连接（目前只有唔哩视频）
     */
    private String landingPage;
    /**
     * 视频来源。98du，wuli
     */
    private String videosSource;
    /**
     * 安卓视频加密地址
     */
    private String encryptionAndroidUrl;
    /**
     * ios视频加密地址
     */
    private String encryptionIosUrl;
    /**
     * 小程序视频加密地址
     */
    private String encryptionXcxUrl;

    private Date deletedAt;
    /**
     * 分类归属应用：用包名区分，多个用逗号隔开
     */
    private String ascriptionApplication;

    /**
     * 敏感词 -1非敏感词，1:1级敏感词
     */
    private Integer sensitive;
}