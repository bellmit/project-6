package com.miguan.report.vo.mongo;

import lombok.Data;

/**
 * @Description 同步数据到banner_data_ext时使用
 * @Author zhangbinglin
 * @Date 2020/6/30 11:10
 **/
@Data
public class BannerExtVo {

    /**
     * 代码位（mongodb使用）
     */
    private String _id;

    /**
     * 代码位（mysql使用,因为mysql使用_id无法映射过来）
     */
    private String adId;

    /**
     * 统计数（例如：错误数，请求数）
     */
    private Integer value;
}
