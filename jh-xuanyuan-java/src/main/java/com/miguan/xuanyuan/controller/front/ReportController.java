package com.miguan.xuanyuan.controller.front;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.common.security.model.JwtUser;
import com.miguan.xuanyuan.common.util.AESUtils;
import com.miguan.xuanyuan.common.util.ExcelUtils;
import com.miguan.xuanyuan.common.util.PageInfo;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.controller.AuthBaseController;
import com.miguan.xuanyuan.dto.request.SetShareSwitchRequest;
import com.miguan.xuanyuan.entity.Plan;
import com.miguan.xuanyuan.entity.XyUserExt;
import com.miguan.xuanyuan.service.*;
import com.miguan.xuanyuan.vo.PairLineVo;
import com.miguan.xuanyuan.vo.ReportParamVo;
import com.miguan.xuanyuan.vo.ReportExcelTableVo;
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
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
@RequestMapping("/api/front/report")
public class ReportController extends AuthBaseController {

    @Resource
    private ReportService reportService;

    @Resource
    private XyUserExtService userExtService;

    @Resource
    private XyPlanShareService planShareService;

    @Resource
    private PlanService planService;

    @ApiOperation("轩辕报表双轴折线图")
    @GetMapping("/getReportLineData")
    public ResultMap<PairLineVo> getReportLineData(@ApiParam(value = "左折线类型:1-点击量,2-曝光量,3-曝光用户量,4-点击用户,5-点击率", required = true) Integer leftType,
                                                   @ApiParam(value = "右折线类型:1-点击量,2-曝光量,3-曝光用户量,4-点击用户,5-点击率", required = true) Integer rightType,
                                                   @Valid ReportParamVo reportParamVo) {
        if(leftType == null){
            return ResultMap.error("必须传入左折现类型");
        } else if (leftType < 1){
            return ResultMap.error("左折现类型不能小于1");
        } else if (leftType > 5){
            return ResultMap.error("左折现类型不能大于5");
        }
        if(rightType == null){
            return ResultMap.error("必须传入右折现类型");
        } else if (rightType < 1){
            return ResultMap.error("右折现类型不能小于1");
        } else if (rightType > 5){
            return ResultMap.error("右折现类型不能大于5");
        }
        try{
            validateDate(reportParamVo.getStartDay(),reportParamVo.getEndDay());
        } catch (ValidateException e) {
            return ResultMap.error(e.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
            return ResultMap.error("日期解析异常。格式错误");
        }
        JwtUser userInfo = getCurrentUser();
        Long userId = userInfo.getUserId();
        reportParamVo.setUserId(userId);
        if (reportParamVo != null) {
            //把查询条件的区间转成sql的between
            reportParamVo.setShowNum(this.sectionFormat(reportParamVo.getShowNum()));
            reportParamVo.setClickNum(this.sectionFormat(reportParamVo.getClickNum()));
        }
        return ResultMap.success(reportService.getReportLineData(leftType, rightType, reportParamVo));
    }

    private void validateDate(String startDay, String endDay) throws ValidateException, ParseException {
        if(StringUtils.isEmpty(startDay) || StringUtils.isEmpty(endDay)) {
            return ;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date start = sdf.parse(startDay);
        Date end = sdf.parse(endDay);
        if(start.after(end)){
            throw new ValidateException("开始时间不能大于结束时间！");
        }

    }

    @ApiOperation("分页查询轩辕报表表格数据")
    @GetMapping("/pageReportTableList")
    public ResultMap<PageInfo<ReportTableVo>> pageReportTableList(@Valid ReportParamVo reportParamVo,
                                                       @ApiParam(value="页码", required=true) Integer pageNum,
                                                       @ApiParam(value="每页记录数", required=true) Integer pageSize) {
        pageNum = pageNum == null ? XyConstant.INIT_PAGE : pageNum;
        pageSize = pageSize == null ? XyConstant.INIT_PAGE_SIZE : pageSize;
        if(reportParamVo != null) {
            //把查询条件的区间转成sql的between
            reportParamVo.setShowNum(this.sectionFormat(reportParamVo.getShowNum()));
            reportParamVo.setClickNum(this.sectionFormat(reportParamVo.getClickNum()));
        }
        try{
            validateDate(reportParamVo.getStartDay(),reportParamVo.getEndDay());
        } catch (ValidateException e) {
            return ResultMap.error(e.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
            return ResultMap.error("日期解析异常。格式错误");
        }
        JwtUser userInfo = getCurrentUser();
        if (userInfo == null) {
            return ResultMap.error("用户未登录");
        }
        Long userId = userInfo.getUserId();
        reportParamVo.setUserId(userId);
        return ResultMap.success(reportService.pageReportTableList(reportParamVo, pageNum, pageSize));
    }

    @ApiOperation("导出")
    @GetMapping("/export")
    public ResultMap export(HttpServletResponse response,@Valid ReportParamVo reportParamVo) {
        try {
            try{
                    validateDate(reportParamVo.getStartDay(),reportParamVo.getEndDay());
            } catch (ValidateException e) {
                return ResultMap.error(e.getMessage());
            } catch (ParseException e) {
                e.printStackTrace();
                return ResultMap.error("日期解析异常。格式错误");
            }
            if(reportParamVo != null) {
                //把查询条件的区间转成sql的between
                reportParamVo.setShowNum(this.sectionFormat(reportParamVo.getShowNum()));
                reportParamVo.setClickNum(this.sectionFormat(reportParamVo.getClickNum()));
            }
            JwtUser userInfo = getCurrentUser();
            if (userInfo == null) {
                return ResultMap.error("用户未登录");
            }
            Long userId = userInfo.getUserId();
            reportParamVo.setUserId(userId);
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

    @ApiOperation("设置数据报表分享开关")
    @PostMapping("/setShareSwitch")
    public ResultMap setShareSwitch(@RequestBody @Valid SetShareSwitchRequest shareSwitchRequest) {
        JwtUser userInfo = getCurrentUser();
        if (userInfo == null) {
            return ResultMap.error("用户未登录");
        }
        Long userId = userInfo.getUserId();

        Integer shareSwitch = shareSwitchRequest.getShareSwitch();
        if (shareSwitch == null || (shareSwitch != XyConstant.PLAN_SHARE_SWITCH_CLOSE && shareSwitch != XyConstant.PLAN_SHARE_SWITCH_OPEN)) {
            return ResultMap.error("shareSwitch参数错误");
        }

        try {
            userExtService.setShareSwitch(userId, shareSwitch);
        } catch (ServiceException e) {
            e.printStackTrace();
            return ResultMap.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error("操作错误");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("shareSwitch", shareSwitch);
        return ResultMap.success(data);
    }

    @ApiOperation("生成分享链接")
    @GetMapping("/getShareCode")
    public ResultMap getShareCode(@RequestParam("planId") Long planId){
        JwtUser userInfo = getCurrentUser();
        if (userInfo == null) {
            return ResultMap.error("用户未登录");
        }
        Long userId = userInfo.getUserId();

        if (planId == null) {
            return ResultMap.error("shareSwitch参数错误");
        }

        Plan planData = planService.getById(planId);
        if (planData == null || planData.getStatus() == XyConstant.STATUS_CLOSE) {
            return ResultMap.error("广告计划不存在");
        }

        String shareCode = null;
        try {
            shareCode = planShareService.generateShareCode(planId, userId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error("操作失败");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("shareCode", shareCode);
        return ResultMap.success(data);
    }

    @ApiOperation("获取数据报表分享开关")
    @GetMapping("/getShareSwitch")
    public ResultMap getShareSwitch() {
        JwtUser userInfo = getCurrentUser();
        if (userInfo == null) {
            return ResultMap.error("用户未登录");
        }
        Long userId = userInfo.getUserId();

        Integer shareSwitch = userExtService.getUserShareSwitch(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("shareSwitch", shareSwitch);
        return ResultMap.success(data);
    }


}
