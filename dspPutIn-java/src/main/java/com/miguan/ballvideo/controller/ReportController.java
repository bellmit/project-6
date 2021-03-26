package com.miguan.ballvideo.controller;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.miguan.ballvideo.common.util.ExcelUtils;
import com.miguan.ballvideo.entity.dsp.PageInfo;
import com.miguan.ballvideo.service.dsp.ReportService;
import com.miguan.ballvideo.vo.PairLineVo;
import com.miguan.ballvideo.vo.ReportParamVo;
import com.miguan.ballvideo.vo.ReportTableVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@Slf4j
@Api(value="Dsp自投平台报表Controller",tags={"报表接口"})
@RestController
public class ReportController {

    @Resource
    private ReportService reportService;

    @ApiOperation("DSP报表双轴折线图")
    @PostMapping("/api/dsp/report/getReportLineData")
    public PairLineVo getReportLineData(@ApiParam(value = "左折线类型:1-曝光量,2-点击量,3-点击率,4-点击均价,5-花费,6-千次展示均价", required = true) Integer leftType,
                                        @ApiParam(value = "右折线类型:1-曝光量,2-点击量,3-点击率,4-点击均价,5-花费,6-千次展示均价", required = true) Integer rightType,
                                        ReportParamVo reportParamVo) {
        if (reportParamVo != null) {
            //把查询条件的区间转成sql的between
            reportParamVo.setSpend(this.sectionFormat(reportParamVo.getSpend()));
            reportParamVo.setExposure(this.sectionFormat(reportParamVo.getExposure()));
            reportParamVo.setValidClick(this.sectionFormat(reportParamVo.getValidClick()));
        }
        return reportService.getReportLineData(leftType, rightType, reportParamVo);
    }

    @ApiOperation("分页查询DSP报表表格数据")
    @PostMapping("/api/dsp/report/pageReportTableList")
    public PageInfo<ReportTableVo> pageReportTableList(ReportParamVo reportParamVo,
                                                       @ApiParam(value="页码", required=true) Integer pageNum,
                                                       @ApiParam(value="每页记录数", required=true) Integer pageSize) {
        if(reportParamVo != null) {
            //把查询条件的区间转成sql的between
            reportParamVo.setSpend(this.sectionFormat(reportParamVo.getSpend()));
            reportParamVo.setExposure(this.sectionFormat(reportParamVo.getExposure()));
            reportParamVo.setValidClick(this.sectionFormat(reportParamVo.getValidClick()));
        }
        return reportService.pageReportTableList(reportParamVo, pageNum, pageSize);
    }

    @ApiOperation("导出")
    @GetMapping("/api/dsp/report/export")
    public void export(HttpServletResponse response, ReportParamVo reportParamVo) throws IOException {
        if(reportParamVo != null) {
            //把查询条件的区间转成sql的between
            reportParamVo.setSpend(this.sectionFormat(reportParamVo.getSpend()));
            reportParamVo.setExposure(this.sectionFormat(reportParamVo.getExposure()));
            reportParamVo.setValidClick(this.sectionFormat(reportParamVo.getValidClick()));
        }
        List<ReportTableVo> list = reportService.pageReportTableList(reportParamVo, 1, 50000).getData();
        String typeName = "";
        if(reportParamVo.getType() == 1 ){
            typeName = "计划组";
        } else if (reportParamVo.getType() == 2){
            typeName = "广告计划";
        } else if (reportParamVo.getType() == 3){
            typeName = "广告创意";
        }
        ExportParams params = new ExportParams(typeName+"DSP报表", "DSP报表", ExcelType.XSSF);
        if(reportParamVo.getType().intValue() != 2) {
            //不是按计划统计的，没有出价列
            params.setExclusions(new String[]{"出价"});
        }
        ExcelUtils.defaultExport(list, ReportTableVo.class, "DSP报表", response, params);
    }

    /**
     * 把查询条件的区间转成sql的between，例如50-100 转成between 50 and 100
     * @param section
     * @return
     */
    private String sectionFormat(String section) {
        if(section == null) {
            return null;
        }
        if(section.contains("-")) {
            section = "between " + section.replace("-", " and ");
        }
        return section;
    }
}
