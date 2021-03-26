package com.miguan.ballvideo.service.dsp;

import com.miguan.ballvideo.vo.request.AdvertAreaVo;
import com.miguan.ballvideo.vo.response.DistrictRes;

import java.util.List;

/**
 * 广告区域表
 */
public interface AdvertAreaService {
    /**
     * 获取省份列表
     * @return
     */
    List<DistrictRes> getProvinceList();

    /**
     * 获取城市列表
     * @return
     */
    List<DistrictRes> getCityListByProvince(Long pid);

    /**
     * 新增关联表
     */
    void insert(AdvertAreaVo advertAreaVo);

    /**
     * 根据广告计划删除区域表
     * @param planId
     */
    void deleteByPlanId(Long planId);

    AdvertAreaVo findByPlanId(Long planId);

    List<DistrictRes> findAreaInfo(AdvertAreaVo areaVo);
}
