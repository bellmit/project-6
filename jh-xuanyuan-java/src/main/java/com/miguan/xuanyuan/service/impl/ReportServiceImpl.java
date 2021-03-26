package com.miguan.xuanyuan.service.impl;

import com.cgcg.context.util.StringUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.miguan.xuanyuan.common.constant.PlanConstant;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.util.PageInfo;
import com.miguan.xuanyuan.entity.Design;
import com.miguan.xuanyuan.entity.Plan;
import com.miguan.xuanyuan.mapper.ReportMapper;
import com.miguan.xuanyuan.service.DesignService;
import com.miguan.xuanyuan.service.PlanService;
import com.miguan.xuanyuan.service.ReportService;
import com.miguan.xuanyuan.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 计划组serviceImpl
 */
@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    @Resource
    private ReportMapper reportMapper;


    @Resource
    private PlanService planService;
    @Resource
    private DesignService designService;


    private List<Long> queryPlanOrDesignIds(Long userId, Integer type) {
        if(type == null){
            return Lists.newArrayList();
        }
        if(type == 1){
            //计划
            return planService.findIds(userId);
        } else if (type == 2){
            //创意
            return designService.findIds(userId);
        }
        return Lists.newArrayList();
    }

    /**
     * 报表折线图
     *
     * @param leftType      左折线类型:1-点击量,2-曝光量,3-曝光用户量,4-点击用户,5-点击率
     * @param rightType     右折线类型:1-点击量,2-曝光量,3-曝光用户量,4-点击用户,5-点击率
     * @param reportParamVo
     */
    public PairLineVo getReportLineData(Integer leftType, Integer rightType, ReportParamVo reportParamVo) {
        List<Long> ids = queryPlanOrDesignIds(reportParamVo.getUserId(),reportParamVo.getType());
        if(CollectionUtils.isEmpty(ids)){
            return new PairLineVo();
        }
        reportParamVo.setIds(ids);
        reportParamVo.setLineType(leftType);
        List<LineChartVo> leftLineData = reportMapper.getReportLineData(reportParamVo); //左折线图的数据
        List<LineChartVo> rightLineData = new ArrayList<>();
        if (leftType.intValue() == rightType.intValue()) {
            rightLineData = leftLineData;
        } else {
            reportParamVo.setLineType(rightType);
            rightLineData = reportMapper.getReportLineData(reportParamVo); //右折线图的数据
        }

        PairLineVo pairLineVo = new PairLineVo();
        pairLineVo.setLeft(leftLineData);
        pairLineVo.setRight(rightLineData);
        return pairLineVo;
    }

    /**
     * 报表表格数据
     *
     * @param reportParamVo
     * @param pageNum       页码
     * @param pageSize      每页记录数
     * @return
     */
    public PageInfo<ReportTableVo> pageReportTableList(ReportParamVo reportParamVo, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<ReportTableVo> pageList = baseReportInfo(reportParamVo);
        List<ReportTableVo> results = pageList.getResult();
        //获取（计划或者创意的信息）以及分时数据
        fillTimeDivision(results,reportParamVo.getType());
        return new PageInfo(pageList);
    }

    @Override
    public List<ReportTableVo> exportReport(ReportParamVo reportParamVo) {
        return baseReportInfo(reportParamVo);
    }

    @Override
    public List<ReportTimeDivisionVo> findTimeDivisionList(Long id, Integer type, String date, String name) {
        if(StringUtils.isEmpty(date)){
            return Lists.newArrayList();
        }
        return reportMapper.findTimeDivisionList(id, type, date,XyConstant.REPORT_NO_DESIGN,name);
    }

    private Page<ReportTableVo> baseReportInfo(ReportParamVo reportParamVo) {
        List<Long> ids = queryPlanOrDesignIds(reportParamVo.getUserId(),reportParamVo.getType());
        if(CollectionUtils.isEmpty(ids)){
            return new Page<ReportTableVo>();
        }
        reportParamVo.setIds(ids);
        if(StringUtils.isBlank(reportParamVo.getSort())) {
            reportParamVo.setSort("dt desc");
        }
        Page<ReportTableVo> result = reportMapper.pageReportTableList(reportParamVo);
        if(result != null){
            fillBaseInfo(result.getResult(),reportParamVo.getType());
        }
        return result;
    }

    private void fillBaseInfo(List<ReportTableVo> results, Integer type) {
        if(CollectionUtils.isEmpty(results) || type == null){
            return ;
        }
        results.forEach(result -> {
            if (result.getId() == null) {
                return;
            }
            //广告计划
            if (PlanConstant.ADV_PLAN == type) {
                Plan plan = planService.getById(result.getId());
                if (plan != null) {
                    result.setName(plan.getName());
                }
            } else if (PlanConstant.ADV_DESIGN == type) {
                Design design = designService.getById(result.getId());
                if (design != null) {
                    result.setName(design.getName());
                }
            }
        });
    }

    private void fillTimeDivision(List<ReportTableVo> results, Integer type) {
        if(CollectionUtils.isEmpty(results) || type == null){
            return ;
        }
        for (ReportTableVo result : results) {
            if (result.getId() == null || StringUtils.isEmpty(result.getDate())) {
                continue;
            }
            result.setTimeDivisions(this.findTimeDivisionList(result.getId(), type, result.getDate(), result.getName()));
        }
    }
}
