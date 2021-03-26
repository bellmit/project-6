package com.miguan.advert.domain.controller;

import cn.jiguang.common.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.miguan.advert.common.exception.ServiceException;
import com.miguan.advert.common.util.IpUtil;
import com.miguan.advert.common.util.ResultMap;
import com.miguan.advert.common.util.StringUtil;
import com.miguan.advert.domain.mapper.PositionInfoMapper;
import com.miguan.advert.domain.service.FlowGroupService;
import com.miguan.advert.domain.vo.interactive.AbLayer;
import com.miguan.advert.domain.vo.result.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @program: advert-java
 * @description: 流量分组controller
 * @author: suhj
 * @create: 2020-09-25 18:42
 **/
@Api(value = "流量分组controller",tags = {"流量分组 接口"})
@RestController
@RequestMapping("/api/flow/group")
public class FlowGroupController {

    @Resource
    private FlowGroupService flowGroupService;

    @Resource
    private PositionInfoMapper positionInfoMapper;


    //@LoginAuth
    @ApiOperation("获取流量分组列表")
    @PostMapping("/getLst")
    public ResultMap<List<ABFlowGroupVo>> getLst(
            @ApiParam("广告位置ID") @RequestParam String position_id) {
        try {
            return flowGroupService.getLst(position_id);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }

    }

    //@LoginAuth
    @ApiOperation("新增流量分组")
    @PostMapping("/insertFlow")
    public ResultMap<ABFlowGroupVo> insertFlowGroup(
            @ApiParam("广告位置ID") @RequestParam String position_id,
            @ApiParam("流量分组名称") @RequestParam String name,
            @ApiParam("实验分组ID") String ab_flow_id,
            @ApiParam("分组类型：1：默认分组,2:手动分组") @RequestParam String type, HttpServletRequest request) {
        try {
            return flowGroupService.insertFlowGroup(position_id,name,ab_flow_id,type,IpUtil.getIp(request));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }


    //@LoginAuth
    @ApiOperation("修改流量分组名称")
    @PostMapping("/updateFlowName")
    public ResultMap<ABFlowGroupVo> updateFlowName(
            @ApiParam("流量分组ID") @RequestParam String flow_id,
            @ApiParam("实验分组ID") String ab_flow_id,
            @ApiParam("流量分组名称") @RequestParam String name, HttpServletRequest request) {
        try {
            return flowGroupService.updateFlowName(flow_id,ab_flow_id,name,IpUtil.getIp(request));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    //@LoginAuth
    @ApiOperation("删除流量分组")
    @PostMapping("/deleteFlow")
    public ResultMap deleteFlowGroup(
            @ApiParam("流量分组ID") @RequestParam String flow_id,HttpServletRequest request) {
        try {
            flowGroupService.deleteFlowGroup(flow_id,IpUtil.getIp(request));
            return ResultMap.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    //@LoginAuth
    @ApiOperation("新增、编辑测试分组")
    @PostMapping("/addEditTest")
    public ResultMap<ABFlowGroupVo> insertTestGroup(
            @ApiParam("流量分组ID") @RequestParam String flow_id,
            @ApiParam("测试组状态：0-关闭，1开启") @RequestParam String test_state,
            @ApiParam("实验分组ID,多个以逗号隔开") @RequestParam String ab_test_ids,HttpServletRequest request) {
        try {
            List<String> testIdLst = Arrays.asList(ab_test_ids.split(","));
            Set<String> testIdSet = Sets.newHashSet(testIdLst);
            if(testIdSet.size() < testIdLst.size()){
                return ResultMap.error("存在重复的实验分组ID");
            }
            return flowGroupService.addEditTestGroup(flow_id,testIdLst,test_state,IpUtil.getIp(request));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    //@LoginAuth
    @ApiOperation("保存流量分组")
    @PostMapping("/saveFlow")
    public ResultMap saveFlowTestGroup(@ApiParam("保存实验组入参") @RequestParam String testArr,HttpServletRequest request) {
        try {
            List<TestInVo> testLst = JSONObject.parseArray(testArr,TestInVo.class);
            flowGroupService.saveFlowTestGroup(testLst,IpUtil.getIp(request));
            return ResultMap.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("获取广告代码位排序")
    @GetMapping("/getAdOrder")
    public ResultMap<List<AdvCodeInfoVo>> getAdOrder(
            @ApiParam("广告位置ID") @RequestParam(value="position_id",required=true) String positionId,
            @ApiParam("渠道") @RequestParam(value="channel_id",required=true, defaultValue = "") String channelId,
            @ApiParam("新老客") @RequestParam(value="user_type",required=true, defaultValue = "0") int userType,
            @ApiParam("城市") @RequestParam(value="city",required=true, defaultValue = "") String city
    ) {
        try {
            AppAdPositionVo positionVo = positionInfoMapper.getPositionById(Integer.parseInt(positionId));
            if (positionVo == null) {
                return ResultMap.error("广告位不存在");
            }
            String packageName = positionVo.getApp_package();
            channelId = StringUtils.isEmpty(channelId) ? "-1" : channelId;
            city = StringUtils.isEmpty(city) ? "-1" : city;
            userType = userType == 2 ? 0 : userType;

            List<AdvCodeInfoVo> result = flowGroupService.getAdOrder(positionId, packageName, channelId, userType, city);
            return ResultMap.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }
}
