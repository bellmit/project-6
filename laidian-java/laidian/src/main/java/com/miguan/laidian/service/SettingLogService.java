package com.miguan.laidian.service;


import com.miguan.laidian.entity.AuthoritySettingErrlog;

/**
 * 广告Service
 */
public interface SettingLogService {

    /**
     * 新增权限设置异常日志信息
     *
     * @param authoritySettingErrlog
     * @return
     */
    AuthoritySettingErrlog addSettingErrLogInfo (AuthoritySettingErrlog authoritySettingErrlog);
}
