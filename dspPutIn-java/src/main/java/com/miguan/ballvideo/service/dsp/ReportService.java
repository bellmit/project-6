package com.miguan.ballvideo.service.dsp;

import com.miguan.ballvideo.entity.dsp.PageInfo;
import com.miguan.ballvideo.vo.PairLineVo;
import com.miguan.ballvideo.vo.ReportParamVo;
import com.miguan.ballvideo.vo.ReportTableVo;

import java.util.List;

/**
 * 报表service
 */
public interface ReportService {

    /**
     * 报表折线图
     * @param leftType 左折线类型，1-曝光量，2-点击量，3-点击率，4-点击均价，5-花费，6-千次展示均价
     * @param rightType 右折线类型，1-曝光量，2-点击量，3-点击率，4-点击均价，5-花费，6-千次展示均价
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
}
