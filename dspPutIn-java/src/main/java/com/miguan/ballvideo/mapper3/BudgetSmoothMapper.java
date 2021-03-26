package com.miguan.ballvideo.mapper3;

import com.miguan.ballvideo.vo.BudgetAccountVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 预算平滑mapper
 */
public interface BudgetSmoothMapper {

    /**
     * 查询出预算平滑的计划id
     * @return
     */
    List<Integer> findBugeSmoothPlanIds();

    /**
     * 查询出需要做预算平滑的计划列表
     * @return
     */
    List<BudgetAccountVo> findBugeSmoothPlan();

    void initAdvertAcount(@Param("planIds") List<String> planIds);

    void initPlanSmoothDate(@Param("planIds") List<String> planIds);

    List<Map<String, Object>> findPlanAccountList(@Param("planIds") List<String> planIds);

    /**
     * 修改计划组剩余预算
     * @param prams
     */
    void updateGroupRemainAccount(Map<String, Object> prams);

    /**
     * 统计98平台的计划昨天的平均点击率
     * @return
     */
    double staYesPreClickRate();
}
