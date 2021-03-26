package com.miguan.ballvideo.entity;

import lombok.Data;

/**
 * 价格阶梯广告位
 * @Author shixh
 * @Date 2020/4/2
 **/
@Data
public class BannerPriceLadderVo {
    private long id;
    private long bannerPositionId;
    private String versionStart;
    private String versionEnd;
    private String keywordMobileType;
    private String appPackage;
}
