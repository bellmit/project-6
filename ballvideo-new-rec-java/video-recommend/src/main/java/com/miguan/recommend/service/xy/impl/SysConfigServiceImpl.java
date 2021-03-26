package com.miguan.recommend.service.xy.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.recommend.entity.SysConfig;
import com.miguan.recommend.common.util.CacheUtil;
import com.miguan.recommend.mapper.SysConfigMapper;
import com.miguan.recommend.service.xy.SysConfigService;
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
@DS("xy-db")
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
}