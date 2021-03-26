package com.miguan.xuanyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.entity.XyPlanPositionCode;

/**
 * <p>
 * 轩辕品牌广告代码位表 服务类
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-18
 */
public interface XyPlanPositionCodeService extends IService<XyPlanPositionCode> {

    void addPositionPlanCodeId(long positionId) throws ServiceException;

    XyPlanPositionCode getPlanPositionCode(Long positionId);
}
