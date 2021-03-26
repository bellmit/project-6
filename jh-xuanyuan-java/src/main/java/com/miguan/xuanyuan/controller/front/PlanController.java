package com.miguan.xuanyuan.controller.front;


import com.alibaba.fastjson.JSON;
import com.cgcg.base.format.Result;
import com.github.pagehelper.PageInfo;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.common.security.model.JwtUser;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.common.util.TimeConfigFormat;
import com.miguan.xuanyuan.controller.AuthBaseController;
import com.miguan.xuanyuan.dto.AppPositionDto;
import com.miguan.xuanyuan.dto.PlanDto;
import com.miguan.xuanyuan.dto.PlanListDto;
import com.miguan.xuanyuan.entity.Plan;
import com.miguan.xuanyuan.service.PlanService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "计划管理", tags = {"计划管理接口"})
@RestController
@Slf4j
@RequestMapping("/api/front/plan")
public class PlanController  extends AuthBaseController {

    @Resource
    private PlanService planService;

    @ApiOperation("保存计划（根据id进行新增或修改操作）")
    @PostMapping("/savePlan")
    public ResultMap savePlan(@RequestBody @Validated PlanDto planDto) throws Exception{
        Integer userId = getCurrentUser().getUserId().intValue();
        String timesConfig = TimeConfigFormat.analysisTimeConfig(planDto.getTimeSetting(), planDto.getTimesConfig());
        planDto.setTimesConfig(timesConfig);
        planService.saveAdvertPlan(planDto, userId);
        return ResultMap.success();
    }

    @ApiOperation("查询所有的计划")
    @GetMapping("/list")
    public ResultMap<List<Plan>> list() {
        JwtUser userInfo = getCurrentUser();
        if (userInfo == null) {
            return ResultMap.error("用户未登录");
        }
        Long userId = userInfo.getUserId();
        return ResultMap.success(planService.getlist(userId));
    }

    @ApiOperation("分页查询计划列表")
    @GetMapping("/pagePlanList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", dataType = "int", value = "状态：1启用，0未启用", paramType = "query"),
            @ApiImplicitParam(name = "keyword", dataType = "String", value = "广告计划id/名称", paramType = "query"),
            @ApiImplicitParam(name = "startDate", dataType = "String", value = "开始时间(yyyy-MM-dd)", paramType = "query"),
            @ApiImplicitParam(name = "endDate", dataType = "String", value = "结束时间(yyyy-MM-dd)", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", dataType = "int", value = "分页页码", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", dataType = "int", value = "每页记录数", paramType = "query")
    })
    public ResultMap<PageInfo<PlanListDto>> pagePlanList(Integer status, String keyword, String startDate, String endDate, Integer pageNum, Integer pageSize) {
        pageNum = pageNum == null ? XyConstant.INIT_PAGE : pageNum;
        pageSize = pageSize == null ? XyConstant.INIT_PAGE_SIZE : pageSize;
        Integer userId = getCurrentUser().getUserId().intValue();
        PageInfo<PlanListDto> pages = planService.pagePlanList(userId, status, keyword, startDate, endDate, pageNum, pageSize);
        return ResultMap.success(pages);
    }

    @ApiOperation("根据计划id查询计划信息以及对应的创意信息")
    @GetMapping("/getPlan")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "planId", dataType = "int", value = "计划id", paramType = "query"),
            @ApiImplicitParam(name = "isCopy", dataType = "int", value = "是否复制操作。1-是，0-否", paramType = "query")
    })
    public ResultMap<PlanDto> getPlan(Integer planId, Integer isCopy) {
        PlanDto planDto = planService.getPlan(planId, isCopy);
        Integer timeSetting = planDto.getTimeSetting();
        if(XyConstant.TIME_SETTING_APPOINT_MANY == timeSetting) {
            String timesConfig = JSON.toJSONString(TimeConfigFormat.reanalysisTimeConfig(planDto.getTimeSetting(), planDto.getTimesConfig()));
            planDto.setTimesConfig(timesConfig);
        }
        return ResultMap.success(planDto);
    }

    @ApiOperation("计划批量操作上线/下线")
    @PostMapping("/updatePlanStatus")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", dataType = "int", value = "状态修改类型：1-上线，0-下线", paramType = "query"),
            @ApiImplicitParam(name = "planIds", dataType = "String", value = "计划id（多个逗号分隔）", paramType = "query")
    })
    public ResultMap updatePlanStatus(Integer type, String planIds) {
        this.planService.updatePlanStatus(type, planIds);
        return ResultMap.success();
    }

    @ApiOperation("根据广告类型查询广告位列表")
    @GetMapping("/listAppPositionByAdType")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "adType", dataType = "String", value = "广告类型,如：interaction、open_screen", paramType = "query")
    })
    public ResultMap<List<AppPositionDto>> listAppPositionByAdType(String adType) {
        Integer userId = getCurrentUser().getUserId().intValue();
        List<AppPositionDto> list = planService.listAppPositionByAdType(adType, userId);
        return ResultMap.success(list);
    }
}
