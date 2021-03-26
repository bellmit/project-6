package com.miguan.reportview.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.miguan.reportview.entity.AdPlat;

import java.util.List;

/**
 * <p>
 * 广告平台 服务类
 * </p>
 *
 * @author zhongli
 * @since 2020-08-03
 */
public interface IAdPlatService extends IService<AdPlat> {

    List<AdPlat> getAllPlat();

    /**
     * 根据广告key 获取广告平台名称
     * @param list
     * @param platKey
     * @return
     */
    String getPlatName(List<AdPlat> list, String platKey);
}
