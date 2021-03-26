package com.miguan.laidian.service;

import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.vo.SysVersionVo;

import java.util.List;

/**
 * @author chenwf
 * @date 2020/5/19
 */
public interface ToolMofangService {
    /**
     * 获取系统版本配置信息
     * @param appPackage
     * @param appVersion
     * @return
     */
    List<SysVersionVo> findUpdateVersionSet(String appPackage, String appVersion);

    /**
     * 上报版本更新人数
     * @param commonParams
     * @return
     */
    int updateSysVersionInfo(CommonParamsVo commonParams);
}
