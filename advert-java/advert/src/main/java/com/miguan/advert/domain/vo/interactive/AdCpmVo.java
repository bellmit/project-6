package com.miguan.advert.domain.vo.interactive;

import lombok.Data;

/**
 * @Description 代码位cpm
 * @Author zhangbinglin
 * @Date 2020/11/12 11:25
 **/
@Data
public class AdCpmVo {
    /**
     * 日期
     */
    private String dd;

    /**
     * 代码位
     */
    private String adId;

    /**
     * 千展收益
     */
    private Double cpm;
}
