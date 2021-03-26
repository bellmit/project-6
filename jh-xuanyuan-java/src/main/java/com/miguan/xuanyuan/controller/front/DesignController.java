package com.miguan.xuanyuan.controller.front;


import com.github.pagehelper.PageInfo;
import com.miguan.xuanyuan.common.security.model.JwtUser;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.controller.AuthBaseController;
import com.miguan.xuanyuan.dto.AppPositionDto;
import com.miguan.xuanyuan.dto.DesignListDto;
import com.miguan.xuanyuan.dto.PlanDto;
import com.miguan.xuanyuan.dto.PlanListDto;
import com.miguan.xuanyuan.entity.Design;
import com.miguan.xuanyuan.service.DesignService;
import com.miguan.xuanyuan.service.PlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "广告创意管理", tags = {"广告创意接口"})
@RestController
@Slf4j
@RequestMapping("/api/front/design")
public class DesignController extends AuthBaseController {

    @Resource
    private DesignService designService;


    @ApiOperation("根据计划id查询创意列表")
    @GetMapping("/listDesign")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startDate", dataType = "String", value = "开始日期，格式：yyyy-MM-dd", paramType = "query"),
            @ApiImplicitParam(name = "endDate", dataType = "String", value = "结束日期，格式：yyyy-MM-dd", paramType = "query"),
            @ApiImplicitParam(name = "planId", dataType = "int", value = "计划id", paramType = "query")
    })
    public ResultMap<List<DesignListDto>> listDesign(String startDate, String endDate, Integer planId) {
        List<DesignListDto> list = designService.listDesign(startDate, endDate, planId);
        return ResultMap.success(list);
    }

    @ApiOperation("查询所有的创意")
    @GetMapping("/list")
    public ResultMap<List<Design>> list(Long planId) {
        JwtUser userInfo = getCurrentUser();
        if (userInfo == null) {
            return ResultMap.error("用户未登录");
        }
        Long userId = userInfo.getUserId();
        return ResultMap.success(designService.getlist(planId,userId));
    }

    @ApiOperation("根据创意id修改创意状态")
    @PostMapping("/updateStatusById")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", dataType = "int", value = "状态：1启用，0未启用", paramType = "query"),
            @ApiImplicitParam(name = "designId", dataType = "int", value = "创意id", paramType = "query")
    })
    public ResultMap updateStatusById(Integer status, Integer designId) {
        designService.updateStatusById(status, designId);
        return ResultMap.success();
    }

    @ApiOperation(value = "批量修改创意权重值", notes = "id和weight必填，其他字段可以不传")
    @PostMapping("/updateBatchWeightById")
    public ResultMap updateBatchWeightById(@RequestBody List<DesignListDto> list) {
        designService.updateBatchWeightById(list);
        return ResultMap.success();
    }
}
