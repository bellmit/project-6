package com.miguan.laidian.service;


import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.vo.Advert;

import java.util.List;

/**
 * 广告Service
 */
public interface AdvertOldService {

    /**
     * 查询广告类型
     *
     * @param postitionType 广告位置类型，如果为空，则查询全部
     * @param adPermission  是否有IMEI权限：0否 1是
     * @return
     */
    List<Advert> queryAdertList(CommonParamsVo commomParams, String postitionType, String adPermission);
}
