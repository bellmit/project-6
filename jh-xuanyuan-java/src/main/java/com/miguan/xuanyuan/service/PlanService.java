package com.miguan.xuanyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.dto.AppPositionDto;
import com.miguan.xuanyuan.dto.PlanDto;
import com.miguan.xuanyuan.dto.PlanListDto;
import com.miguan.xuanyuan.entity.Plan;

import java.util.List;

/**
 * <p>
 * 计划管理表 服务类
 * </p>
 *
 * @author zhangbinglin
 * @since 2021-03-16
 */
public interface PlanService extends IService<Plan> {

    /**
     * 分页查询计划列表
     * @param userId 用户id
     * @param status 状态
     * @param keyword 计划id
     * @param startDate
     * @param endDate
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<PlanListDto> pagePlanList(Integer userId, Integer status, String keyword, String startDate, String endDate, Integer pageNum, Integer pageSize);

    /**
     * 保存广告计划、创意
     *
     * @param planDto 计划dto
     * @param userId  用户id
     */
    void saveAdvertPlan(PlanDto planDto, Integer userId) throws ServiceException;

    /**
     * 根据计划id查询计划信息以及对应的创意信息
     * @param planId  计划id
     * @param isCopy  是否复制操作。1-是，0-否(复制操作的话，把计划id和创意id置空)
     * @return
     */
    PlanDto getPlan(Integer planId, Integer isCopy);

    /**
     * 根据广告类型查询广告位列表
     * @param adType 广告类型
     * @param userId 用户id
     * @return
     */
    List<AppPositionDto> listAppPositionByAdType(String adType, Integer userId);

    /**
     * 计划批量操作上线/下线
     * @param type 状态修改类型：1-上线，0-下线
     * @param planIds 计划id（多个逗号分隔）
     */
    void updatePlanStatus(Integer type, String planIds);

    List<Plan> getlist(Long userId);

    List<Long> findIds(Long userId);

    Plan getPlanByPositionId(Long positionId);
}
