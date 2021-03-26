package com.miguan.reportview.vo;

import lombok.Data;

/**
 * @Description 人均广告点击折线图
 * @Author zhangbinglin
 * @Date 2020/10/26 19:10
 **/
@Data
public class AdClickNumVo {

    /**
     * 小时
     */
    private String hh;

    /**
     * 渠道
     */
    private String channel;

    /**
     * 人均广告点击
     */
    private double preAdClick;
}
