package com.miguan.xuanyuan.service;

import com.miguan.xuanyuan.common.util.PageInfo;
import com.miguan.xuanyuan.vo.*;

import java.util.List;

/**
 * 报表service
 */
public interface ReportService {

    /**
     * 报表折线图
     * @param leftType 左折线类型:1-点击量,2-曝光量,3-曝光用户量,4-点击用户,5-点击率
     * @param rightType 右折线类型:1-点击量,2-曝光量,3-曝光用户量,4-点击用户,5-点击率
     * @param reportParamVo
     */
    PairLineVo getReportLineData(Integer leftType, Integer rightType, ReportParamVo reportParamVo);

    /**
     * 报表表格数据
     *
     * @param reportParamVo
     * @param pageNum       页码
     * @param pageSize      每页记录数
     * @return
     */
    PageInfo<ReportTableVo> pageReportTableList(ReportParamVo reportParamVo, Integer pageNum, Integer pageSize);

    /**
     * 报表表格数据
     *
     * @param reportParamVo
     * @return
     */
    List<ReportTableVo> exportReport(ReportParamVo reportParamVo);

    List<ReportTimeDivisionVo> findTimeDivisionList(Long id, Integer type, String date, String name);
}
