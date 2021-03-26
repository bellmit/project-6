package com.miguan.advert.domain.vo.interactive;

import lombok.Data;

/**
 * @Description 代码位收益
 **/
@Data
public class AdMultiDimenVo {

    /**
     * 代码位
     */
    private String adId;

    /**
     * 包名
     */
    private String packageName;

    /**
     * 渠道
     */
    private String channel;

    /**
     * 是否新用户
     *
     */
    private int isNew;

    /**
     * 城市
     *
     */
    private String city;

    /**
     * 自监测展示数
     *
     */
    private int show;

    /**
     * 自监测点击数
     *
     */
    private int click;

    /**
     * 有效展示数
     */
    private int exposure;

    /**
     * 有效点击数
     */
    private int validClick;

}
