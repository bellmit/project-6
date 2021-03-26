package com.miguan.bigdata.service.impl;

import com.miguan.bigdata.common.util.CacheUtil;
import com.miguan.bigdata.entity.SysConfig;
import com.miguan.bigdata.mapper.SysConfigMapper;
import com.miguan.bigdata.service.SysConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统参数ServiceImpl
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-06-20 10:48:24
 */
@Service(value = "sysConfigService")
public class SysConfigServiceImpl implements SysConfigService {

    @Resource
    private SysConfigMapper sysConfigMapper;

    @Override
    public List<SysConfig> findAll() {
        return sysConfigMapper.findAll();
    }

    @Override
    public void initSysConfig() {
        List<SysConfig> sysConfigs = findAll();
        CacheUtil.initSysConfig(sysConfigs);
    }

    public SysConfig getConfig(String code) {
        return sysConfigMapper.getConfig(code);
    }
}