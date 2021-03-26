package com.miguan.xuanyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.miguan.xuanyuan.dto.DesignDto;
import com.miguan.xuanyuan.dto.DesignListDto;
import com.miguan.xuanyuan.entity.Design;

import java.util.List;

/**
 * <p>
 * 创意管理表 服务类
 * </p>
 *
 * @author zhangbinglin
 * @since 2021-03-16
 */
public interface DesignService extends IService<Design> {

    /**
     * 批量新增广告创意
     * @param list 创意列表
     * @param planId 计划id
     * @param userId 用户id
     */
    void saveBatchDesign(List<DesignDto> list, Integer planId, Integer userId);

    /**
     * 查询计划下的创意列表
     * @param planId 计划id
     * @return
     */
    List<DesignDto> listDesignByPlanId(Integer planId);

    /**
     * 获取计划下的列表
     * @param startDate 开始时间yyyy-MM-dd
     * @param endDate  结束时间yyyy-MM-dd
     * @param planId  计划
     * @return
     */
    List<DesignListDto> listDesign(String startDate, String endDate, Integer planId);

    /**
     * 根据创意id修改创意的状态
     * @param status  状态：1启用，0未启用
     * @param designId  创意id
     */
    void updateStatusById(Integer status, Integer designId);

    /**
     * 批量修改创意权重值
     * @param list
     */
    void updateBatchWeightById(List<DesignListDto> list);

    List<Design> getlist(Long planId, Long userId);

    List<Long> findIds(Long userId);

    /**
     * 判断创意名称是否存在
     * @param designId 创意id
     * @param userId 用户id
     * @param name 创意名称
     * @return
     */
    boolean isExistDesignName(Integer designId, Integer userId, String name);
}
