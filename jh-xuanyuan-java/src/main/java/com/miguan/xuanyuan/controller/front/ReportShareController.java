package com.miguan.xuanyuan.controller.front;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.common.security.model.JwtUser;
import com.miguan.xuanyuan.common.util.*;
import com.miguan.xuanyuan.controller.AuthBaseController;
import com.miguan.xuanyuan.dto.request.SetShareSwitchRequest;
import com.miguan.xuanyuan.entity.Plan;
import com.miguan.xuanyuan.entity.XyPlanShare;
import com.miguan.xuanyuan.service.PlanService;
import com.miguan.xuanyuan.service.ReportService;
import com.miguan.xuanyuan.service.XyPlanShareService;
import com.miguan.xuanyuan.service.XyUserExtService;
import com.miguan.xuanyuan.vo.PairLineVo;
import com.miguan.xuanyuan.vo.ReportExcelTableVo;
import com.miguan.xuanyuan.vo.ReportParamVo;
import com.miguan.xuanyuan.vo.ReportTableVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author kangkunhuang
 * @Description 报表(前台)
 * @Date 2021/1/21
 **/
@Api(value = "报表(前台)控制层", tags = {"报表(前台)接口"})
@RestController
@Slf4j
@RequestMapping("/api/front/reportShare")
public class ReportShareController extends AuthBaseController {

    @Resource
    private ReportService reportService;

    @Resource
    private XyUserExtService userExtService;

    @Resource
    private XyPlanShareService planShareService;

    @Resource
    private PlanService planService;

    @Resource
    private XyPlanShareService xyPlanShareService;


    @ApiOperation("轩辕报表双轴折线图")
    @GetMapping("/getExpired")
    public ResultMap getExpired(@ApiParam(value = "分享代码", required = true) String shareCode) {
        Integer planId = null;
        XyPlanShare planShareData = null;
        try {
            planShareData = this.getPlanShare(shareCode);
            planId = planShareData.getPlanId().intValue();
        } catch (ServiceException e) {
            return ResultMap.error(e.getMessage());
        }

        Plan planData = planService.getById(planId);
        if (planData == null || planData.getStatus() == XyConstant.STATUS_CLOSE) {
            return ResultMap.error("广告计划不存在");
        }

        LocalDateTime expiredAt = planShareData.getExpiredAt();
        LocalDateTime curDateTime = LocalDateTime.now();

        if (curDateTime.isAfter(expiredAt)) {
            return ResultMap.error("分享已过期");
        }

        Duration duration = Duration.between(curDateTime, expiredAt);
        long seconds = (long)(duration.toMillis() / 1000);

        Map<String, Object> data = new HashMap<>();
        data.put("expireSeconds", seconds);
        return ResultMap.success(data);

    }



    @ApiOperation("轩辕报表双轴折线图")
    @GetMapping("/getReportLineData")
    public ResultMap<PairLineVo> getReportLineData(@ApiParam(value = "左折线类型:1-点击量,2-曝光量,3-曝光用户量,4-点击用户,5-点击率", required = true) Integer leftType,
                                                   @ApiParam(value = "右折线类型:1-点击量,2-曝光量,3-曝光用户量,4-点击用户,5-点击率", required = true) Integer rightType,
                                                   @ApiParam(value = "分享代码", required = true) String shareCode,
                                                   @Valid ReportParamVo reportParamVo) {

        Integer planId = null;
        try {
            XyPlanShare planShareData = this.getPlanShare(shareCode);
            planId = planShareData.getPlanId().intValue();
        } catch (ServiceException e) {
            return ResultMap.error(e.getMessage());
        }
        reportParamVo.setId(planId);
        reportParamVo.setType(1);

        Plan planData = planService.getById(planId);
        if (planData == null || planData.getStatus() == XyConstant.STATUS_CLOSE) {
            return ResultMap.error("广告计划不存在");
        }
        Long userId = planData.getUserId().longValue();
        reportParamVo.setUserId(userId);

        Integer shareSwitch = userExtService.getUserShareSwitch(userId);
        if (shareSwitch == XyConstant.PLAN_SHARE_SWITCH_CLOSE) {
            return ResultMap.error(ResultCode.REPORT_SHARE_SWITCH_ERROR);
        }

        if(leftType == null){
            return ResultMap.error("必须传入左折现类型");
        }
        if(rightType == null){
            return ResultMap.error("必须传入右折现类型");
        }

        if (reportParamVo != null) {
            //把查询条件的区间转成sql的between
            reportParamVo.setShowNum(this.sectionFormat(reportParamVo.getShowNum()));
            reportParamVo.setClickNum(this.sectionFormat(reportParamVo.getClickNum()));
        }
        return ResultMap.success(reportService.getReportLineData(leftType, rightType, reportParamVo));
    }

    @ApiOperation("分页查询轩辕报表表格数据")
    @GetMapping("/pageReportTableList")
    public ResultMap<PageInfo<ReportTableVo>> pageReportTableList(@Valid ReportParamVo reportParamVo,
                                                                  @ApiParam(value = "分享代码", required = true) String shareCode,
                                                                  @ApiParam(value="页码", required=true) Integer pageNum,
                                                                  @ApiParam(value="每页记录数", required=true) Integer pageSize) {

        Integer planId = null;
        try {
            XyPlanShare planShareData = this.getPlanShare(shareCode);
            planId = planShareData.getPlanId().intValue();
        } catch (ServiceException e) {
            return ResultMap.error(e.getMessage());
        }
        reportParamVo.setId(planId);
        reportParamVo.setType(1);

        Plan planData = planService.getById(planId);
        if (planData == null || planData.getStatus() == XyConstant.STATUS_CLOSE) {
            return ResultMap.error("广告计划不存在");
        }
        Long userId = planData.getUserId().longValue();
        reportParamVo.setUserId(userId);

        Integer shareSwitch = userExtService.getUserShareSwitch(userId);
        if (shareSwitch == XyConstant.PLAN_SHARE_SWITCH_CLOSE) {
            return ResultMap.error(ResultCode.REPORT_SHARE_SWITCH_ERROR);
        }

        pageNum = pageNum == null ? XyConstant.INIT_PAGE : pageNum;
        pageSize = pageSize == null ? XyConstant.INIT_PAGE_SIZE : pageSize;
        if(reportParamVo != null) {
            //把查询条件的区间转成sql的between
            reportParamVo.setShowNum(this.sectionFormat(reportParamVo.getShowNum()));
            reportParamVo.setClickNum(this.sectionFormat(reportParamVo.getClickNum()));
        }
        return ResultMap.success(reportService.pageReportTableList(reportParamVo, pageNum, pageSize));
    }

    @ApiOperation("导出")
    @GetMapping("/export")
    public ResultMap export(HttpServletResponse response,@Valid ReportParamVo reportParamVo, @ApiParam(value = "分享代码", required = true) String shareCode) {

        Integer planId = null;
        try {
            XyPlanShare planShareData = this.getPlanShare(shareCode);
            planId = planShareData.getPlanId().intValue();
        } catch (ServiceException e) {
            return ResultMap.error(e.getMessage());
        }
        reportParamVo.setId(planId);
        reportParamVo.setType(1);

        Plan planData = planService.getById(planId);
        if (planData == null || planData.getStatus() == XyConstant.STATUS_CLOSE) {
            return ResultMap.error("广告计划不存在");
        }
        Long userId = planData.getUserId().longValue();
        reportParamVo.setUserId(userId);

        Integer shareSwitch = userExtService.getUserShareSwitch(userId);
        if (shareSwitch == XyConstant.PLAN_SHARE_SWITCH_CLOSE) {
            return ResultMap.error(ResultCode.REPORT_SHARE_SWITCH_ERROR);
        }


        try {
            if(reportParamVo != null) {
                //把查询条件的区间转成sql的between
                reportParamVo.setShowNum(this.sectionFormat(reportParamVo.getShowNum()));
                reportParamVo.setClickNum(this.sectionFormat(reportParamVo.getClickNum()));
            }
            List<ReportTableVo> result = reportService.exportReport(reportParamVo);
            List<ReportExcelTableVo> list = copyProperty(result);
            String typeName = "";
            if(reportParamVo.getType() == 1){
                typeName = "广告计划";
            } else if (reportParamVo.getType() == 2){
                typeName = "广告创意";
            }
            ExportParams params = new ExportParams(typeName+"轩辕品牌广告报表", "轩辕品牌广告报表", ExcelType.XSSF);
            ExcelUtils.defaultExport(list, ReportExcelTableVo.class, "轩辕品牌广告报表", response, params);
            return ResultMap.success();
        } catch (Exception e){
            log.error("导出报表异常：" + e.getMessage());
            return ResultMap.error("未知异常");
        }
    }

    private List<ReportExcelTableVo> copyProperty(List<ReportTableVo> results) {
        if(CollectionUtils.isEmpty(results)){
            return Lists.newArrayList();
        }
        List<ReportExcelTableVo> list = Lists.newArrayList();
        results.forEach(result -> {
            ReportExcelTableVo table = new ReportExcelTableVo();
            try {
                BeanUtils.copyProperties(table,result);
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
            list.add(table);
        });
        return list;
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

    private XyPlanShare getPlanShare(String shareCode) throws ServiceException {
        if (StringUtils.isEmpty(shareCode)){
            throw new ServiceException("shareCode参数不能为空");
        }

        String planShareIdStr = null;
        try {
            planShareIdStr = AESUtils.decrypt(shareCode);
        } catch (Exception e) {
            throw new ServiceException("shareCode参数错误");
        }
        Long planShareId = Long.parseLong(planShareIdStr);

        XyPlanShare planShareDetail = xyPlanShareService.getById(planShareId);
        if (planShareDetail == null) {
            throw new ServiceException("分享不存在");
        }

        LocalDateTime expiredAt = planShareDetail.getExpiredAt();
        LocalDateTime curDateTime = LocalDateTime.now();

        if (curDateTime.isAfter(expiredAt)) {
            throw new ServiceException("分享已过期");
        }
        return planShareDetail;
    }




}
