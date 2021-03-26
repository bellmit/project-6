package com.miguan.idmapping.service;

import com.miguan.idmapping.vo.SysConfigVo;

import java.util.List;

/**
 * 系统参数Service
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-06-20 10:48:24
 */
public interface SysConfigService {
    List<SysConfigVo> findAll();
    void initSysConfig();

}