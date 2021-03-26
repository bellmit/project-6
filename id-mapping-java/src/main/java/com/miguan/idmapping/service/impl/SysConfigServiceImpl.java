package com.miguan.idmapping.service.impl;

import com.miguan.idmapping.common.utils.CacheUtil;
import com.miguan.idmapping.mapper.SysConfigMapper;
import com.miguan.idmapping.service.SysConfigService;
import com.miguan.idmapping.vo.SysConfigVo;
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
	public void initSysConfig() {
		List<SysConfigVo> sysConfigs = findAll();
		CacheUtil.initSysConfig(sysConfigs);
	}
	@Override
	public List<SysConfigVo> findAll() {
		return sysConfigMapper.findAll();
	}
}