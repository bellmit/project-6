package com.miguan.xuanyuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cgcg.context.util.StringUtils;
import com.miguan.xuanyuan.common.util.NumCalculationUtil;
import com.miguan.xuanyuan.dto.DesignDto;
import com.miguan.xuanyuan.dto.DesignListDto;
import com.miguan.xuanyuan.entity.Design;
import com.miguan.xuanyuan.entity.Plan;
import com.miguan.xuanyuan.mapper.DesignMapper;
import com.miguan.xuanyuan.service.DesignService;
import com.miguan.xuanyuan.vo.PlanDesignDataVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 创意管理表 服务实现类
 * </p>
 *
 * @author zhangbinglin
 * @since 2021-03-16
 */
@Service
@Slf4j
public class DesignServiceImpl extends ServiceImpl<DesignMapper, Design> implements DesignService {

    @Resource
    private DesignMapper designMapper;

    /**
     * 批量新增广告创意
     * @param list 创意列表
     * @param planId 计划id
     * @param userId 用户id
     */
    public void saveBatchDesign(List<DesignDto> list, Integer planId, Integer userId) {
        if(CollectionUtils.isEmpty(list)) {
            log.error("广告创意为空,planId:{}", planId);
            return;
        }

        List<Design> listEntity = new ArrayList<>();
        for(DesignDto dto : list) {
            Design design = new Design();
            BeanUtils.copyProperties(dto, design);
            design.setUserId(userId);
            design.setPlanId(planId);
            listEntity.add(design);
        }
        this.saveOrUpdateBatch(listEntity);  //批量新增创意
    }

    /**
     * 查询计划下的创意列表
     * @param planId 计划id
     * @return
     */
    public List<DesignDto> listDesignByPlanId(Integer planId) {
        QueryWrapper<Design> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plan_id", planId);
        queryWrapper.eq("is_del", 0);
        List<Design> list = this.list(queryWrapper);

        List<DesignDto> dtoList = new ArrayList<>();
        for(Design design : list) {
            DesignDto designDto = new DesignDto();
            BeanUtils.copyProperties(design, designDto);
            dtoList.add(designDto);
        }
        return dtoList;
    }

    /**
     * 获取计划下的列表
     * @param startDate 开始时间yyyy-MM-dd
     * @param endDate  结束时间yyyy-MM-dd
     * @param planId  计划
     * @return
     */
    public List<DesignListDto> listDesign(String startDate, String endDate, Integer planId) {
        List<DesignDto> list = listDesignByPlanId(planId);
        List<DesignListDto> designList = new ArrayList<>();
        if(CollectionUtils.isEmpty(list)) {
            return designList;
        }
        List<Integer> designIds = list.stream().map(r->r.getId()).collect(Collectors.toList()); //获取创意id列表
        Map<String, Object> params = new HashMap<>();
        params.put("designIds", designIds);
        if(StringUtils.isNotBlank(startDate)) {
            params.put("startDate", Integer.parseInt(startDate.replace("-", "")));
        }
        if(StringUtils.isNotBlank(startDate)) {
            params.put("endDate", Integer.parseInt(endDate.replace("-", "")));
        }
        List<PlanDesignDataVo> designDataList = designMapper.staDesignData(params);
        Map<Integer, PlanDesignDataVo> designMap = new HashMap<>();
        for(PlanDesignDataVo planDesignDataVo : designDataList) {
            designMap.put(planDesignDataVo.getDesignId(), planDesignDataVo);
        }

        for(DesignDto design : list) {
            DesignListDto designListDto = new DesignListDto();
            BeanUtils.copyProperties(design, designListDto);
            PlanDesignDataVo designDataVo = designMap.get(design.getId());
            if(designDataVo != null) {
                Integer cilck = designDataVo.getClick();
                Integer show = designDataVo.getShow();
                designListDto.setClick(cilck);  //点击数
                designListDto.setShow(show);  //曝光数
                designListDto.setClickRate(NumCalculationUtil.divide(cilck, show, true)); //点击率
            }
            designList.add(designListDto);
        }
        return designList;
    }

    /**
     * 根据创意id修改创意的状态
     * @param status  状态：1启用，0未启用
     * @param designId  创意id
     */
    public void updateStatusById(Integer status, Integer designId) {
        Design design = new Design();
        design.setId(designId);
        design.setStatus(status);
        this.updateById(design);
    }

    /**
     * 批量修改创意权重值
     * @param list
     */
    public void updateBatchWeightById(List<DesignListDto> list) {
        List<Design> designList = new ArrayList<>();
        for(DesignListDto dto : list) {
            Design design = new Design();
            BeanUtils.copyProperties(dto, design);
            designList.add(design);
        }
        this.updateBatchById(designList);
    }

    @Override
    public List<Design> getlist(Long planId, Long userId) {
        QueryWrapper<Design> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("is_del", 0);
        if(planId != null){
            queryWrapper.eq("plan_id", planId);
        }
        return this.list(queryWrapper);
    }

    @Override
    public List<Long> findIds(Long userId) {
        return designMapper.findIds(userId);
    }

    /**
     * 判断创意名称是否存在
     * @param designId 创意id
     * @param userId 用户id
     * @param name 创意名称
     * @return
     */
    public boolean isExistDesignName(Integer designId, Integer userId, String name) {
        QueryWrapper<Design> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("name", name);
        queryWrapper.ne("name", "");
        queryWrapper.eq("is_del", 0);
        if(designId != null) {
            queryWrapper.ne("id", designId);
        }
        List<Design> list = designMapper.selectList(queryWrapper);
        return list != null && !list.isEmpty();
    }
}
