package com.miguan.idmapping.service;

import com.miguan.idmapping.dto.RegAnonymousDto;

import java.util.Map;

/**
 * @author zhongli
 * @date 2020-07-21 
 *
 */
public interface IAnonymousRegister {


    /**
     * 注册匿名用户
     * @param deviceInfo
     * @return
     */
    Map<String,String> register(RegAnonymousDto deviceInfo);
}
