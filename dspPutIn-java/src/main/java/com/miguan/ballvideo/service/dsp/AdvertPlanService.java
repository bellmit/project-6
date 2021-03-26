package com.miguan.ballvideo.service.dsp;

import com.miguan.ballvideo.common.exception.ServiceException;
import com.miguan.ballvideo.entity.dsp.PageInfo;
import com.miguan.ballvideo.vo.request.AdvertCodeVo;
import com.miguan.ballvideo.vo.request.AdvertPlanVo;
import com.miguan.ballvideo.vo.response.*;

import java.util.List;

/**
 * 广告计划
 */
public interface AdvertPlanService {

    /**
     * 新增或修改广告计划
     */
    void save(AdvertPlanVo advertPlanVo) throws ServiceException;

    /**
     * 删除广告计划
     * @param id
     */
    void delete(Long id);

    /**
     * 根据计划组id,查询广告计划
     * @param groupId
     */
    List<AdvertPlanVo> getByGroupId(Long groupId);

    /**
     * 广告计划批量上线 和 批量下线
     * @param state 类型，1--批量上线，0--批量下线
     * @param ids 计划组id，多个逗号分隔
     */
    void batchOnlineAndUnderline(int state, String ids);

    AdvertPlanRes getPlanInfo(Long planId) throws ServiceException;

    PageInfo<AdvertPlanListRes> pageList(String keyword, Integer advertUserId, Integer state, Integer putInType, String startDay, String endDay, String sort, Integer pageNum, Integer pageSize);

    PageInfo<AdvertPlanListExt> extPageList(String keyword, String advertUserName, Integer state, Integer pageNum, Integer pageSize);

    List<AdvertPlanSimpleRes> getPlanList(Long advertUserId, Long groupId);

    void updateMaterial(Long planId, Integer materialType, Integer materialShape);

    AdvertPlanVo findSimpleById(Long plan_id);

    List<AdvertCodeSimpleRes> getCodeByPlanId(Long planId);

    void saveRelationCode(Long planId, String codeIds);
}
