package com.miguan.xuanyuan.vo;

import lombok.Data;

/**
 * @Description TODO
 * @Author zhangbinglin
 * @Date 2021/3/3 16:37
 **/
@Data
public class ThirdPlatVo {

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 平台id
     */
    private Integer platId;

    /**
     * 平台key
     */
    private String platKey;

    /**
     * 是否开启三方包名api，1开通，0未开通
     */
    private Integer openReportapi;

    /**
     * 第三方账号id
     */
    private String appId;

    /**
     * 第三方账号的appSecret
     */
    private String appSecret;
}
