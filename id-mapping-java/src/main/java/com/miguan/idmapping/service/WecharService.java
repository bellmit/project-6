package com.miguan.idmapping.service;


import java.util.Map;

/**
 * @author xy.chen
 * @date 2020/8/10
 */
public interface WecharService {

    /**
     * 微信授权
     *
     * @param code
     */
    Map<String, Object> wecharAuth(String code);
}