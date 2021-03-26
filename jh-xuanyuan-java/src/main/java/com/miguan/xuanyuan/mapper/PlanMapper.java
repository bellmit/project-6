package com.miguan.xuanyuan.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.miguan.xuanyuan.dto.AppPositionDto;
import com.miguan.xuanyuan.dto.PlanListDto;
import com.miguan.xuanyuan.dto.PlanPositionDto;
import com.miguan.xuanyuan.entity.Plan;
import com.miguan.xuanyuan.vo.PlanDesignDataVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 计划管理表 Mapper 接口
 * </p>
 *
 * @author zhangbinglin
 * @since 2021-03-16
 */
public interface PlanMapper extends BaseMapper<Plan> {

    Page<PlanListDto> pagePlanList(Map<String, Object> params);

    /**
     * 查询计划报表数据
     * @param params
     * @return
     */
    @DS("xy_report")
    List<PlanDesignDataVo> staPlanData(Map<String, Object> params);

    /**
     * 删除计划和广告位的对应关系
     * @param planId 计划id
     */
    void deletePlanPosition(@Param("planId") Integer planId);

    /**
     * 保存计划和广告位的关系表
     * @param list
     */
    void savePlanPosition(@Param("list") List<PlanPositionDto> list);

    /**
     * 根据计划id查询出绑定的广告位列表
     * @param planId 计划id
     * @return
     */
    List<Integer> queryPlanPositionId(@Param("planId") Integer planId);

    /**
     * 根据广告类型查询广告位列表
     * @param params
     * @return
     */
    List<AppPositionDto> listAppPositionByAdType(Map<String, Object> params);

    /**
     * 计划批量操作上线/下线
     * @param params
     */
    void updatePlanStatus(Map<String, Object> params);

    List<Long> findIds(Long userId);

    Plan getPlanByPositionId(Long positionId);
}
