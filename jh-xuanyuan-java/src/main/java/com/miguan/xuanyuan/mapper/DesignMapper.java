package com.miguan.xuanyuan.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.xuanyuan.entity.Design;
import com.miguan.xuanyuan.vo.PlanDesignDataVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 创意管理表 Mapper 接口
 * </p>
 *
 * @author zhangbinglin
 * @since 2021-03-16
 */
public interface DesignMapper extends BaseMapper<Design> {

    /**
     * 获取时间段内创意的曝光数、点击数
     * @param params
     * @return
     */
    @DS("xy_report")
    List<PlanDesignDataVo> staDesignData(Map<String, Object> params);

    List<Long> findIds(Long userId);
}
