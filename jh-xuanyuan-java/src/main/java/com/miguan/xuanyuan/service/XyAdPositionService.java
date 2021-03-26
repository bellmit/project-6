package com.miguan.xuanyuan.service;


import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.common.util.PageInfo;
import com.miguan.xuanyuan.dto.AdPositionDetailDto;
import com.miguan.xuanyuan.entity.XyAdPosition;
import com.miguan.xuanyuan.vo.OptionsVo;
import com.miguan.xuanyuan.vo.XyAdPositionSimpleVo;
import com.miguan.xuanyuan.vo.XyAdPositionVo;

import java.util.List;

/**
 * @Author kangkunhuang
 * @Description 广告位
 * @Date 2021/1/21
 **/
public interface XyAdPositionService {
    void save(XyAdPosition xyAdPosition) throws ValidateException;
    PageInfo<XyAdPositionVo> pageList(int plat, Long userId, String username, Integer type, String keyword, Integer clientType, Integer status, String adType, Integer pageNum, Integer pageSize);
    XyAdPosition findById(Long id);
    void deleteById(Long id);
    void deleteByAppId(Long appId);
    List<XyAdPositionSimpleVo> findList(int plat, Long userId, Long appId);

    List<OptionsVo> linkageSelection(int plat, Long userId);

    AdPositionDetailDto getPositionDetail(Long positionId);

    List<OptionsVo> thirdlinkageSelection(int plat);
}
