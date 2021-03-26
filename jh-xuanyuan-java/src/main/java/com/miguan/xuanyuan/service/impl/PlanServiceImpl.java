package com.miguan.xuanyuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cgcg.base.core.exception.CommonException;
import com.cgcg.context.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.common.util.NumCalculationUtil;
import com.miguan.xuanyuan.dto.*;
import com.miguan.xuanyuan.entity.Plan;
import com.miguan.xuanyuan.mapper.PlanMapper;
import com.miguan.xuanyuan.service.DesignService;
import com.miguan.xuanyuan.service.PlanService;
import com.miguan.xuanyuan.service.XyPlanPositionCodeService;
import com.miguan.xuanyuan.vo.PlanDesignDataVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 计划管理表 服务实现类
 * </p>
 *
 * @author zhangbinglin
 * @since 2021-03-16
 */
@Service
public class PlanServiceImpl extends ServiceImpl<PlanMapper, Plan> implements PlanService {

    @Resource
    private PlanMapper planMapper;

    @Resource
    private DesignService designService;

    @Resource
    private XyPlanPositionCodeService planPositionCodeService;

    public PageInfo<PlanListDto> pagePlanList(Integer userId, Integer status, String keyword, String startDate, String endDate, Integer pageNum, Integer pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("status", status);
        params.put("keyword", keyword);
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        PageHelper.startPage(pageNum, pageSize);
        PageInfo<PlanListDto> pageInfos = planMapper.pagePlanList(params).toPageInfo();

        //计划的报表数据
        List<PlanListDto> list = pageInfos.getList();
        List<Integer> planIds = list.stream().map(r->r.getId()).collect(Collectors.toList());
        params = new HashMap<>();
        params.put("planIds", planIds);
        if(StringUtils.isNotBlank(startDate)) {
            params.put("startDate", Integer.parseInt(startDate.replace("-", "")));
        }
        if(StringUtils.isNotBlank(startDate)) {
            params.put("endDate", Integer.parseInt(endDate.replace("-", "")));
        }
        List<PlanDesignDataVo> planDataList = planMapper.staPlanData(params);
        Map<Integer, PlanDesignDataVo> designMap = new HashMap<>();
        for(PlanDesignDataVo planDesignDataVo : planDataList) {
            designMap.put(planDesignDataVo.getPlanId(), planDesignDataVo);
        }

        for(PlanListDto planDto : list) {
            PlanDesignDataVo planDataVo = designMap.get(planDto.getId());
            planDto.setPutInState(getPutInState(planDto.getPutTimeType(), planDto.getStartDate(), planDto.getEndDate()));
            if(planDataVo != null) {
                Integer cilck = planDataVo.getClick();
                Integer show = planDataVo.getShow();
                planDto.setClick(cilck);  //点击数
                planDto.setShow(show);  //曝光数
                planDto.setClickRate(NumCalculationUtil.divide(cilck, show, true)); //点击率
            }
        }
        return pageInfos;
    }

    private Integer getPutInState(Integer putTimeType, Date startDate, Date endDate) {
        long now = System.currentTimeMillis();
        if (startDate == null) {
            startDate = DateUtil.valueOf("1940-01-01");
        }
        if (endDate == null) {
            endDate = DateUtil.valueOf("2090-12-31");
        }
        if (startDate.getTime() > now) {
            return 0;  //未投放
        } else if ((startDate.getTime() <= now && now <= endDate.getTime()) || (putTimeType == 0 && startDate.getTime() <= now)) {
            return 1;  //投放中
        } else if (now > endDate.getTime()) {
            return 2;  //投放结束
        }
        return 0;
    }

    /**
     * 保存广告计划、创意
     *
     * @param planDto 计划dto
     * @param userId  用户id
     */
    @Transactional
    public void saveAdvertPlan(PlanDto planDto, Integer userId) throws ServiceException {
        this.verificationPlan(planDto, userId);  //校验计划名称或创意名称是否存在
        Plan planEntity = new Plan();
        BeanUtils.copyProperties(planDto, planEntity);
        planEntity.setUserId(userId);
        if(planEntity.getEndDate() == null) {
            planEntity.setEndDate(DateUtil.valueOf("2090-12-31"));
        }
        this.saveOrUpdate(planEntity); //新增或修改广告计划

        Integer planId = planEntity.getId(); //计划id
        this.savePlanPosition(planDto.getPositionList(), planId);  //保存计划和广告位的关系表
        designService.saveBatchDesign(planDto.getDesignList(), planId, userId);
    }

    private void verificationPlan(PlanDto planDto, Integer userId) {
        String planName = planDto.getName();
        if(this.isExistPlanName(planDto.getId(), userId, planDto.getName())) {
            //计划名称已经存在
            throw new CommonException("计划名称'" + planName + "'已存在，请重新输入!");
        }
        List<DesignDto> designList = planDto.getDesignList();
        for(DesignDto designDto : designList) {
            String designName = designDto.getName();
            if(designService.isExistDesignName(designDto.getId(), userId, designName)) {
                throw new CommonException("创意名称'" + designName + "'已存在，请重新输入!");
            }
        }
    }

    /**
     * 保存计划和广告位的关系表
     *
     * @param positionList 广告位id列表
     * @param planId       计划id
     */
    private void savePlanPosition(List<Integer> positionList, Integer planId) throws ServiceException {
        List<PlanPositionDto> list = new ArrayList<>();
        for (Integer positionId : positionList) {
            PlanPositionDto dto = new PlanPositionDto(planId, positionId, "");
            list.add(dto);
            //广告位对应的品牌广告代码位
            planPositionCodeService.addPositionPlanCodeId(positionId.longValue());
        }
        if (CollectionUtils.isNotEmpty(list)) {
            planMapper.deletePlanPosition(planId);
            planMapper.savePlanPosition(list);
        }
    }

    /**
     * 根据计划id查询计划信息以及对应的创意信息
     * @param planId  计划id
     * @param isCopy  是否复制操作。1-是，0-否(复制操作的话，把计划id和创意id置空)
     * @return
     */
    public PlanDto getPlan(Integer planId, Integer isCopy) {
        PlanDto planDto = new PlanDto();
        Plan plan = this.getById(planId);  //根据id查询计划
        BeanUtils.copyProperties(plan, planDto);

        List<Integer> positionList = planMapper.queryPlanPositionId(planId); //根据计划id查询出绑定的广告位列表
        List<DesignDto> designDtoList = designService.listDesignByPlanId(planId);  //查询计划下的创意列表
        if(isCopy != null && isCopy == XyConstant.PLAN_COPY) {
            planDto.setId(null); //把计划id设置成null
            designDtoList.forEach(design -> { design.setId(null); }); //把创意的id设置成null
        }
        planDto.setPositionList(positionList);
        planDto.setDesignList(designDtoList);
        planDto.setPutInState(getPutInState(planDto.getPutTimeType(), planDto.getStartDate(), planDto.getEndDate()));
        return planDto;
    }

    /**
     * 根据广告类型查询广告位列表
     * @param adType 广告类型
     * @param userId 用户id
     * @return
     */
    public List<AppPositionDto> listAppPositionByAdType(String adType, Integer userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("adType", adType);
        params.put("userId", userId);
        return planMapper.listAppPositionByAdType(params);
    }

    /**
     * 计划批量操作上线/下线
     * @param type 状态修改类型：1-上线，0-下线
     * @param planIds 计划id（多个逗号分隔）
     */
    public void updatePlanStatus(Integer type, String planIds) {
        if(StringUtils.isBlank(planIds)) {
            return;
        }
        List<String> planList = Arrays.asList(planIds.split(","));
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("planIds", planList);
        planMapper.updatePlanStatus(params);
    }

    @Override
    public List<Plan> getlist(Long userId) {
        QueryWrapper<Plan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.list(queryWrapper);
    }

    @Override
    public List<Long> findIds(Long userId) {
        return planMapper.findIds(userId);
    }

    /**
     * 判断计划名称是否存在
     * @param planId 计划id
     * @param userId 用户id
     * @param name 计划名称
     * @return true 存在
     */
    private boolean isExistPlanName(Integer planId, Integer userId, String name) {
        QueryWrapper<Plan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("name", name);
        if(planId != null) {
            queryWrapper.ne("id", planId);
        }
        List<Plan> list = planMapper.selectList(queryWrapper);
        return list != null && !list.isEmpty();
    }

    /**
     * 根据广告位获取广告计划信息
     *
     * @param positionId
     * @return
     */
    public Plan getPlanByPositionId(Long positionId) {
        PlanMapper mapper = this.getBaseMapper();
        return mapper.getPlanByPositionId(positionId);
    }


}
