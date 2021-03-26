package com.miguan.reportview.service;

import com.miguan.reportview.entity.DwAppVersionChannelDict;

import java.util.List;

/**
 * <p>
 * 广告代码位表 服务类
 * </p>
 *
 * @author zhongli
 * @since 2020-08-03
 */
public interface ICickhouseCommonService {

    /**
     * 机型
     * @return
     */
    List<Object> getModel();

    List<Object> getParentChannelDist(Integer appType);

    List<DwAppVersionChannelDict> getSubChannelDist(String parent,Integer appType);

    List<DwAppVersionChannelDict> getAllSubChannel(Integer appType);
}
