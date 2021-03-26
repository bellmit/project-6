package com.miguan.laidian.service;


import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.vo.AdvertCodeVo;
import com.miguan.laidian.vo.AdvertPositionListVo;

import java.util.List;

/**
 * 广告Service
 */
public interface AdvertService {

    /**
     * * 查询广告信息
     * @param commomParams
     * @param postitionType 广告位置类型
     * @param adPermission 是否有IMEI权限：0否 1是
     * @return
     */
    List<AdvertCodeVo> queryAdertList(CommonParamsVo commomParams, String postitionType, String adPermission);

    /**
     * 查询广告位置信息
     * @param commomParams
     * @return
     */
    AdvertPositionListVo advPositionInfo(CommonParamsVo commomParams);
}
