package com.miguan.ballvideo.service.dsp.impl;

import com.cgcg.context.util.StringUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.miguan.ballvideo.entity.dsp.PageInfo;
import com.miguan.ballvideo.mapper3.ReportMapper;
import com.miguan.ballvideo.service.dsp.ReportService;
import com.miguan.ballvideo.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 计划组serviceImpl
 */
@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    @Resource
    private ReportMapper reportMapper;

    /**
     * 报表折线图
     *
     * @param leftType      左折线类型，1-曝光量，2-点击量，3-点击率，4-点击均价，5-花费，6-千次展示均价
     * @param rightType     右折线类型，1-曝光量，2-点击量，3-点击率，4-点击均价，5-花费，6-千次展示均价
     * @param reportParamVo
     */
    public PairLineVo getReportLineData(Integer leftType, Integer rightType, ReportParamVo reportParamVo) {

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
        if(StringUtils.isBlank(reportParamVo.getSort())) {
            reportParamVo.setSort("date desc");
        }
        Page<ReportTableVo> pageList = reportMapper.pageReportTableList(reportParamVo);
        return new PageInfo(pageList);
    }
}
