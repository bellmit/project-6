package com.miguan.xuanyuan.vo;

/**
 * @Description TODO
 * @Author zhangbinglin
 * @Date 2021/3/5 14:39
 **/

import lombok.Data;

@Data
public class ThirdAdPostionVo {
    //代码位id
    private String adId;

    //广告位名称
    private String totalName;

    //代码位阶梯价
    private Double price;

    //广告类型code
    private String adTypeCode;

    //广告类型name
    private String adTypeName;

    //平台code
    private String platKey;
}
