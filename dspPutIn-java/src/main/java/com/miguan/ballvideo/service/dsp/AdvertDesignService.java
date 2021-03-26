package com.miguan.ballvideo.service.dsp;

import com.miguan.ballvideo.common.exception.ServiceException;
import com.miguan.ballvideo.common.exception.ValidateException;
import com.miguan.ballvideo.entity.dsp.PageInfo;
import com.miguan.ballvideo.vo.request.AdvertCodeVo;
import com.miguan.ballvideo.vo.request.AdvertDesignModifyVo;
import com.miguan.ballvideo.vo.request.AdvertDesignVo;
import com.miguan.ballvideo.vo.response.AdvertDesignListRes;
import com.miguan.ballvideo.vo.response.AdvertDesignModifyRes;
import com.miguan.ballvideo.vo.response.AdvertDesignRes;
import com.miguan.ballvideo.vo.response.AdvertDesignSimpleRes;

import java.util.List;
import java.util.Map;

/**
 * 广告计划
 */
public interface AdvertDesignService {

    /**
     * 新增或修改广告计划
     */
    void save(AdvertDesignVo advertDesignVo);

    /**
     * 删除广告计划
     * @param id
     */
    void delete(Long id);

    /**
     * 根据广告计划id,查询广告计划
     * @param planId
     */
    List<AdvertDesignVo> getByPlanId(Long planId);

    void updateDesignState(Map<String, Object> params);

    void updateDesignByPlanState(Map<String, Object> params);

    void deleteByPlanId(Long id);

    PageInfo<AdvertDesignListRes> pageList(String keyword, Integer advertUserId, Integer planId, Integer materialShape, String startDay, String endDay, String sort, Integer pageNum, Integer pageSize);

    void saveBatch(AdvertDesignModifyVo advertDesignModifyVo) throws ValidateException, ServiceException;

    AdvertDesignVo findById(Long designId);

    AdvertDesignModifyRes findResById(Long designId);

    void changeState(int state, Long id);

    List<AdvertDesignSimpleRes> getDesignList(Long advertUserId);

    void removeAdvCacheByCodes(List<AdvertCodeVo> codes);
}
