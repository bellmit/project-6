package com.miguan.xuanyuan.controller.front;

import com.cgcg.context.util.StringUtils;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.common.log.annotation.LogInfo;
import com.miguan.xuanyuan.common.security.model.JwtUser;
import com.miguan.xuanyuan.common.util.IpUtil;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.controller.AuthBaseController;
import com.miguan.xuanyuan.dto.AdPositionDetailDto;
import com.miguan.xuanyuan.dto.StrategyGroupDto;
import com.miguan.xuanyuan.dto.ab.AbFlowGroupParam;
import com.miguan.xuanyuan.dto.ab.AbItem;
import com.miguan.xuanyuan.dto.request.AbStrategyCodeRequest;
import com.miguan.xuanyuan.dto.request.AbTestRequest;
import com.miguan.xuanyuan.dto.request.AbTestStatusRequest;
import com.miguan.xuanyuan.entity.XyAdCode;
import com.miguan.xuanyuan.entity.XyStrategyGroup;
import com.miguan.xuanyuan.service.AbExpService;
import com.miguan.xuanyuan.service.AbParamConvertService;
import com.miguan.xuanyuan.service.StrategyGroupService;
import com.miguan.xuanyuan.service.XyAdPositionService;
import com.miguan.xuanyuan.vo.StrategyGroupVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "策略管理", tags = {"策略管理"})
@Slf4j
@RestController
@RequestMapping("/api/front/strategy/")
public class StrategyGroupController extends AuthBaseController {

    @Resource
    StrategyGroupService strategyGroupService;

    @Resource
    XyAdPositionService xyAdPositionService;

    @Resource
    AbParamConvertService abParamConvertService;

    @Resource
    AbExpService abExpService;


    @ApiOperation("获取分组列表")
    @GetMapping(value = {"/group"})
    public ResultMap getGroupList(@ApiParam(value="广告位id", required=true)Long posId) throws Exception{

        if (posId == null) {
            return ResultMap.error("参数错误");
        }
        if (posId <= 0) {
            return ResultMap.error("pos_id参数错误");
        }

        try {
            Map<String, Object> data = strategyGroupService.getGroupDetail(posId);
            return ResultMap.success(data);
        } catch (ServiceException e) {
            return ResultMap.error(e.getMessage());
        }

    }

    @ApiOperation("获取分组详情")
    @GetMapping(value = {"/detail"})
    public ResultMap detail(@ApiParam(value="分组id", required=true)Long strategyGroupId) throws Exception{

        JwtUser userInfo = getCurrentUser();
        if (userInfo == null) {
            return ResultMap.error("用户未登录");
        }
        Long userId = userInfo.getUserId();

        if (strategyGroupId == null ) {
            return ResultMap.error("strategyGroupId参数错误");
        }
        if (strategyGroupId <= 0) {
            return ResultMap.error("strategyGroupId参数错误");
        }

        XyStrategyGroup xyStrategyGroup = strategyGroupService.getDataById(strategyGroupId);
        if (xyStrategyGroup == null) {
            return ResultMap.error("策略分组不存在");
        }

        AdPositionDetailDto adPositionDetailDto = xyAdPositionService.getPositionDetail(xyStrategyGroup.getPositionId());
        if (adPositionDetailDto.getUserId() != userId) {
            return ResultMap.error("没权限查看");
        }

        StrategyGroupVo strategyGroupVo = strategyGroupService.getStrategyDetail(strategyGroupId);
        return ResultMap.success(strategyGroupVo);
    }


    @ApiOperation("修改ab实验状态")
    @PostMapping(value = {"/abExp/status"})
    @LogInfo(pathName="前台-策略聚合管理-修改ab实验状态",plat= XyConstant.FRONT_PLAT,type = XyConstant.LOG_UPDATE)
    public ResultMap changeAbExpStatus(@RequestBody AbTestStatusRequest abTestStatusRequest) {

        Long strategyGroupId = abTestStatusRequest.getStrategyGroupId();
        if (strategyGroupId == null) {
            return ResultMap.error("strategyGroupId参数错误");
        }

        XyStrategyGroup xyStrategyGroup = strategyGroupService.getDataById(strategyGroupId);
        if (xyStrategyGroup == null) {
            return ResultMap.error("策略分组不存在");
        }
        try {
            strategyGroupService.changeAbStatus(abTestStatusRequest);
        } catch (ServiceException e) {
            return ResultMap.error(e.getMessage());
        } catch (ParseException e) {
            return ResultMap.error();
        }

        try {
            Map<String, Object> data = strategyGroupService.getGroupDetail(xyStrategyGroup.getPositionId());
            return ResultMap.success(data);
        } catch (ServiceException e) {
            return ResultMap.error(e.getMessage());
        }
    }

    @ApiOperation("保存策略代码位")
    @PostMapping(value = {"/code"})
    @LogInfo(pathName="前台-策略聚合管理-保存策略代码位",plat= XyConstant.FRONT_PLAT,type = XyConstant.LOG_UPDATE)
    public ResultMap saveStragetyCode(@RequestBody AbStrategyCodeRequest abStrategyCodeRequest) {
        Long strategyGroupId = abStrategyCodeRequest.getStrategyGroupId();
        if (strategyGroupId == null) {
            return ResultMap.error("strategyGroupId参数错误");
        }

        StrategyGroupVo strategyGroupVo = null;

        try {
            strategyGroupVo = strategyGroupService.getStrategyDetail(strategyGroupId);
            if (strategyGroupVo == null) {
                return ResultMap.error("策略分组ID(strategyGroupId)不存在");
            }
            strategyGroupService.saveStragetyCode(strategyGroupVo, abStrategyCodeRequest);
        } catch (ServiceException e) {
            return ResultMap.error(e.getMessage());
        } catch (ParseException e) {
            return ResultMap.error();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
        return ResultMap.success();
    }


    @ApiOperation("新增ab实验")
    @PostMapping(value = {"/abExp/add"})
    @LogInfo(pathName="前台-策略聚合管理-新增ab实验",plat= XyConstant.FRONT_PLAT,type = XyConstant.LOG_ADD)
    public ResultMap addAbExp(@RequestBody AbTestRequest abTestRequest,  HttpServletRequest request) {

        AbFlowGroupParam abFlowGroupParam = null;
        try {
            strategyGroupService.addAbExpCheck(abTestRequest);
            abFlowGroupParam = abParamConvertService.convertToAbFlowGroupParam(abTestRequest);
            String msg = abFlowGroupParam.check();
            if(StringUtils.isNotEmpty(msg)){
                return ResultMap.error(msg);
            }
            abFlowGroupParam.init().buildInfo();
        } catch (ServiceException e) {
            return ResultMap.error(e.getMessage());
        }

        String ip = IpUtil.getIp(request);

        try {
            Map<String, Object> result = abExpService.saveFlowInfo(abFlowGroupParam, abTestRequest, ip);
            Long expId = (Long) result.get("abId");
            String abExpCode = (String)result.get("abExpCode");
            List<AbItem> abItemList = (List<AbItem>) result.get("abItemList");
            strategyGroupService.addAbExp(abTestRequest, expId, abExpCode, abItemList);
        } catch (ServiceException e) {
            return ResultMap.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }

        try {
            Map<String, Object> data = strategyGroupService.getGroupDetail(abTestRequest.getPositionId());
            return ResultMap.success(data);
        } catch (ServiceException e) {
            return ResultMap.error(e.getMessage());
        }
    }

    @ApiOperation("编辑ab实验")
    @PostMapping(value = {"/abExp/edit"})
    @LogInfo(pathName="前台-策略聚合管理-编辑ab实验",plat= XyConstant.FRONT_PLAT,type = XyConstant.LOG_UPDATE)
    public ResultMap editAbExp(@RequestBody AbTestRequest abTestRequest, HttpServletRequest request) {

        AbFlowGroupParam abFlowGroupParam = null;
        try {
            strategyGroupService.editAbExpCheck(abTestRequest);
            abFlowGroupParam = abParamConvertService.convertToAbFlowGroupParam(abTestRequest);
            String msg = abFlowGroupParam.check();
            if(StringUtils.isNotEmpty(msg)){
                return ResultMap.error(msg);
            }
            abFlowGroupParam.init().buildInfo();
        } catch (ServiceException e) {
            return ResultMap.error(e.getMessage());
        }

        String ip = IpUtil.getIp(request);

        try {
            Map<String, Object> result = abExpService.saveFlowInfo(abFlowGroupParam, abTestRequest, ip);
            Long expId = (Long) result.get("abId");
            String abExpCode = (String)request.getParameter("abExpCode");
            List<AbItem> abItemList = (List<AbItem>) result.get("abItemList");

            strategyGroupService.editAbExp(abTestRequest, expId , abItemList);
        } catch (ServiceException e) {
            return ResultMap.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }

        try {
            Map<String, Object> data = strategyGroupService.getGroupDetail(abTestRequest.getPositionId());
            return ResultMap.success(data);
        } catch (ServiceException e) {
            return ResultMap.error(e.getMessage());
        }
    }

    @ApiOperation("获取ab实验详情")
    @GetMapping(value = {"/abExp/detail"})
    public ResultMap abExpDetail(Long strategyGroupId) {
        try {
            Map<String, Object> data = strategyGroupService.getAbExpDetail(strategyGroupId);
            return ResultMap.success(data);
        } catch (ServiceException e) {
            return ResultMap.error(e.getMessage());
        }
    }


}