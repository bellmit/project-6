package com.miguan.xuanyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.xuanyuan.entity.XyPlanPositionCode;

/**
 * <p>
 * 轩辕品牌广告代码位表 Mapper 接口
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-18
 */
public interface XyPlanPositionCodeMapper extends BaseMapper<XyPlanPositionCode> {

    XyPlanPositionCode getPlanPositionCode(Long positionId);
}
