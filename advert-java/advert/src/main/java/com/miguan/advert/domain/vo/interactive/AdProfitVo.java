package com.miguan.advert.domain.vo.interactive;

import lombok.Data;

import javax.persistence.Column;

/**
 * @Description 代码位收益
 **/
@Data
public class AdProfitVo {
    /**
     * 日期
     */
    @Column(name="ad_space_id")
    private String adId;

    /**
     * 代码位
     */
    private Double profit;
}
