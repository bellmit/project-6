package com.miguan.ballvideo.controller;

import com.alibaba.fastjson.JSON;
import com.cgcg.context.util.StringUtils;
import com.miguan.ballvideo.common.constants.DefaultConstant;
import com.miguan.ballvideo.common.constants.MaterialShapeConstants;
import com.miguan.ballvideo.common.constants.TypeConstant;
import com.miguan.ballvideo.common.exception.ServiceException;
import com.miguan.ballvideo.common.exception.ValidateException;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.common.util.TimeConfigFormat;
import com.miguan.ballvideo.common.util.ValidatorUtil;
import com.miguan.ballvideo.entity.dsp.PageInfo;
import com.miguan.ballvideo.service.dsp.AdvertPlanService;
import com.miguan.ballvideo.vo.request.AdvertDesignVo;
import com.miguan.ballvideo.vo.request.AdvertPlanVo;
import com.miguan.ballvideo.vo.response.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@Api(value="Dsp广告计划 Controller",tags={"Dsp广告计划接口"})
@RestController
@RequestMapping("/api/idea/AdvertPlan")
public class AdvertPlanController {

    @Autowired
    private AdvertPlanService advertPlanService;

    @ApiOperation("分页查询广告计划列表")
    @PostMapping("/pageList")
    public ResultMap<PageInfo<AdvertPlanListRes>> pageList(@ApiParam("广告主id") Integer advertUserId,
                                                           @ApiParam("广告计划id/名称") String keyword,
                                                           @ApiParam("投放方式：1-标准投放,2-快速投放") Integer putInType,
                                                           @ApiParam("状态 状态：0-暂停，1-投放中") Integer state,
                                                           @ApiParam("开始日期，格式：yyyy-MM-dd") String startDay,
                                                           @ApiParam("结束日期，格式：yyyy-MM-dd") String endDay,
                                                           @ApiParam("排序字段，字段+排序方式，例如：consume asc(花费按升序)，consume desc(花费按降序)") String sort,
                                                           @ApiParam(value="页码", required=true) Integer pageNum,
                                                           @ApiParam(value="每页记录数", required=true) Integer pageSize) {
        return ResultMap.success(advertPlanService.pageList(keyword,advertUserId, state, putInType, startDay, endDay, sort, pageNum, pageSize));
    }

    @ApiOperation("推广管理-分页查询广告计划列表")
    @PostMapping("/extPageList")
    public ResultMap<PageInfo<AdvertPlanListExt>> extPageList(@ApiParam("广告计划id/名称") String keyword,
                                                              @ApiParam("广告主名称") String advertUserName,
                                                              @ApiParam("状态 状态：0-暂停，1-投放中") Integer state,
//                                                              @ApiParam("排序字段，字段+排序方式，例如：consume asc(花费按升序)，consume desc(花费按降序)") String sort,
                                                              @ApiParam(value="页码", required=true) Integer pageNum,
                                                              @ApiParam(value="每页记录数", required=true) Integer pageSize) {
        return ResultMap.success(advertPlanService.extPageList(keyword,advertUserName, state, pageNum, pageSize));
    }

    @ApiOperation("推广管理-指定投放代码位")
    @PostMapping("/saveRelationCode")
    public ResultMap saveRelationCode(@ApiParam("广告计划id") Long planId,
                                      @ApiParam("广告位,多个用,号隔开") String codeIds) {
        if(planId == null){
            return ResultMap.error("必须传入计划id");
        }
        if(StringUtils.isEmpty(codeIds) || codeIds.split(",").length == 0){
            return ResultMap.error("必须传入代码位id");
        }
        advertPlanService.saveRelationCode(planId,codeIds);
        return ResultMap.success();
    }

    @ApiOperation("推广管理-查询代码位信息")
    @GetMapping("/getCodeByPlanId")
    public ResultMap<List<AdvertCodeSimpleRes>> getCodeByPlanId(@ApiParam("广告计划id") Long planId) {
        return ResultMap.success(advertPlanService.getCodeByPlanId(planId));
    }

    @ApiOperation("获取广告计划下拉列表")
    @GetMapping("/getPlanList")
    public ResultMap<List<AdvertPlanSimpleRes>> getPlanList(@ApiParam("广告主id") Long advertUserId, @ApiParam("计划组id") Long groupId) {
        // todo advert_user_id 校验还原
//        if(advertUserId == null){
//            ResultMap.error("必须传入广告主");
//        }
//        if(groupId == null){
//            ResultMap.error("必须传入计划组");
//        }
        return ResultMap.success(advertPlanService.getPlanList(advertUserId,groupId));
    }

    @ApiOperation("新增、编辑广告计划")
    @PostMapping("/AddPlan")
    public ResultMap<AdvertPlanVo> AddPlan(@ApiParam("广告计划实体类") @RequestBody AdvertPlanVo planVo) {
        try {
            //校验数据
            planVo.validate();
            //填充默认数据
            planVo.init();
            //添加计划
            advertPlanService.save(planVo);
            return ResultMap.success(planVo);
        } catch (ValidateException e) {
            return ResultMap.error(e.getMessage());
        } catch (ServiceException e) {
            return ResultMap.error(e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("查询广告计划信息")
    @GetMapping("/PlanInfo")
    public ResultMap<AdvertPlanRes> PlanInfo(@ApiParam("广告计划的id") Long planId) {
        try {
            if(planId == null){
                return ResultMap.error("必须传入广告计划的主键");
            }
            AdvertPlanRes advertPlanRes = advertPlanService.getPlanInfo(planId);
            return ResultMap.success(advertPlanRes);
        } catch (ServiceException e) {
            return ResultMap.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("删除广告计划")
    @GetMapping("/delete")
    public ResultMap delete(@ApiParam("广告计划的id") Long planId) {
        try {
            if(planId == null){
                return ResultMap.error("必须传入广告计划的主键");
            }
            advertPlanService.delete(planId);
            return ResultMap.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("广告计划批量上线或下线")
    @PostMapping("/batchOnlineAndUnderline")
    public ResultMap batchOnlineAndUnderline(@ApiParam("类型，1--批量上线，0--批量下线") int state,
                                        @ApiParam("计划计划id，多个逗号分隔") String ids) {
        if(StringUtils.isEmpty(ids)){
            return ResultMap.error("请传入计划id");
        }
        advertPlanService.batchOnlineAndUnderline(state, ids);
        return ResultMap.success();
    }

}
