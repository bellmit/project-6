package com.miguan.advert.domain.controller;

import com.cgcg.context.util.StringUtils;
import com.miguan.advert.common.exception.ServiceException;
import com.miguan.advert.common.util.IpUtil;
import com.miguan.advert.common.util.ResultMap;
import com.miguan.advert.domain.service.AbFlowGroupService;
import com.miguan.advert.domain.vo.interactive.AbLayer;
import com.miguan.advert.domain.vo.interactive.AppInfo;
import com.miguan.advert.domain.vo.request.AbFlowGroupParam;
import com.miguan.advert.domain.vo.result.ABFlowGroupVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @program: advert-java
 * @description: 流量控制
 * @author: kangkh
 * @create: 2020-10-27 18:42
 **/
@Api(value = "流量控制",tags = {"ab流量控制接口"})
@RestController
@RequestMapping("/api/abflow/group")
public class AbFlowGroupController {

    @Resource
    private AbFlowGroupService abflowGroupService;

    @ApiOperation("保存实验分组")
    @PostMapping("/saveAbflow")
    public ResultMap<ABFlowGroupVo> saveAbflow(@ApiParam("添加的实验信息") AbFlowGroupParam abFlowGroupParam,@ApiParam("实验id,修改时必传") String flowId, HttpServletRequest request) {
        try {
            //校验请求参数的值
            String msg = abFlowGroupParam.check();
            if(StringUtils.isNotEmpty(msg)){
                return ResultMap.error(msg);
            }
            abFlowGroupParam.init().buildInfo();
            return abflowGroupService.saveFlowInfo(abFlowGroupParam,flowId,IpUtil.getIp(request));
        } catch (ServiceException e) {
            e.printStackTrace();
            return ResultMap.error(e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("查询实验分组")
    @PostMapping("/findAbflow")
    public ResultMap<AbFlowGroupParam> findAbflow(@ApiParam("广告位id") Integer positionId,@ApiParam("流量的id") String flowId,
                                                  @ApiParam("app马甲包")String appType, HttpServletRequest request) {
        try {
            if(positionId == null){
                return ResultMap.error("必须传入代码位id");
            }
            if(StringUtils.isEmpty(flowId)){
                return ResultMap.error("必须传入实验组id");
            }
            return ResultMap.success(abflowGroupService.findAbflow(positionId,flowId,appType,IpUtil.getIp(request)));
        } catch (ServiceException e) {
            e.printStackTrace();
            return ResultMap.error(e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("删除实验分组")
    @PostMapping("/deleteAbflow")
    public ResultMap deleteAbflow(@ApiParam("流量的id") String flowId,
                                                  @ApiParam("app马甲包")String appType, HttpServletRequest request) {
        try {
            if(appType == null){
                return ResultMap.error("必须传入马甲包");
            }
            if(StringUtils.isEmpty(flowId)){
                return ResultMap.error("必须传入实验组id");
            }
            abflowGroupService.deleteAbflow(flowId,appType,IpUtil.getIp(request));
            return ResultMap.success();
        } catch (ServiceException e) {
            e.printStackTrace();
            return ResultMap.error(e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("运行实验分组")
    @PostMapping("/sendEditState")
    public ResultMap sendEditState(@ApiParam("状态") Integer state,
                                   @ApiParam("马甲包")String app_type,
                                  @ApiParam("实验分组id")String ab_flow_id, HttpServletRequest request) {
        try {
            if(state == null){
                return ResultMap.error("必须传入状态");
            }
            if(StringUtils.isEmpty(app_type)){
                return ResultMap.error("必须传入马甲包");
            }
            if(StringUtils.isEmpty(ab_flow_id)){
                return ResultMap.error("必须传入实验组id");
            }
            abflowGroupService.sendEditState(app_type,state,ab_flow_id,IpUtil.getIp(request));
            return ResultMap.success();
        } catch (ServiceException e) {
            e.printStackTrace();
            return ResultMap.error(e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("获取分层信息")
    @GetMapping("/getLayerInfo")
    public ResultMap<List<AbLayer>> getLayerInfo(@ApiParam("马甲包") String appType,@ApiParam("实验id") Integer exp_id) {
        try {
            if(appType == null){
                return ResultMap.error("必须传入马甲包");
            }
            return ResultMap.success(abflowGroupService.getLayerInfo(appType,exp_id));
        } catch (ServiceException e) {
            e.printStackTrace();
            return ResultMap.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("获取APP信息")
    @GetMapping("/appList")
    public ResultMap<List<AppInfo>> appList() {
        try {
            return ResultMap.success(abflowGroupService.appList());
        } catch (ServiceException e) {
            e.printStackTrace();
            return ResultMap.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("获取版本信息")
    @GetMapping("/getVersionInfo")
    public ResultMap<List<String>> getVersionInfo(@ApiParam("马甲包") String appType) {
        try {
            if(appType == null){
                return ResultMap.error("必须传入马甲包");
            }
            return abflowGroupService.getVersionInfo(appType);
        }  catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

}
