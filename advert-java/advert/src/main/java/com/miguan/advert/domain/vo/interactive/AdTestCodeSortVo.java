package com.miguan.advert.domain.vo.interactive;

import lombok.Data;

/**
 * @Description 代码位自动排序vo
 * @Author zhangbinglin
 * @Date 2020/11/12 11:19
 **/
@Data
public class AdTestCodeSortVo {

    public AdTestCodeSortVo() {
        cpm = 0D;
    }

    /**
     * 主键id
     */
    private Long id;

    /**
     * 配置id（ad_advert_test_config）
     */
    private String configId;

    /**
     * 代码位id（ad_advert_code）
     */
    private Long codeId;

    /**
     * 代码位
     */
    private String adId;

    /**
     * 排序值
     */
    private Integer orderNum;

    /**
     * 代码位cpm
     */
    private Double cpm;
}
