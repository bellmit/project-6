package com.miguan.laidian.service.impl;

import com.miguan.laidian.entity.AuthoritySettingErrlog;
import com.miguan.laidian.repositories.SettingLogDao;
import com.miguan.laidian.service.SettingLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 权限设置日志ServiceImpl
 * @author xy.chen
 * @date 2019-06-24
 **/

@Service("SettingLogService")
public class SettingLogServiceImpl implements SettingLogService {

	@Resource
	private SettingLogDao settingLogDao;

	/**
	 * 新增权限设置异常日志信息
	 * @param authoritySettingErrlog
	 * @return
	 */
	@Override
	public AuthoritySettingErrlog addSettingErrLogInfo (AuthoritySettingErrlog authoritySettingErrlog){
		authoritySettingErrlog.setCreateTime(new Date());
		authoritySettingErrlog = settingLogDao.save(authoritySettingErrlog);
		return authoritySettingErrlog;
	}
}