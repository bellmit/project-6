package com.miguan.xuanyuan.controller.back;

import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.common.log.annotation.LogInfo;
import com.miguan.xuanyuan.common.util.PageInfo;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.entity.XyApp;
import com.miguan.xuanyuan.service.XyAppService;
import com.miguan.xuanyuan.service.XySourceAppService;
import com.miguan.xuanyuan.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author kangkunhuang
 * @Description 应用(后台)
 * @Date 2021/1/21
 **/
@Api(value = "应用(后台)控制层", tags = {"应用(后台)接口"})
@RestController
@Slf4j
@RequestMapping("/api/back/xyApp")
public class XyAppBackController {

    @Resource
    private XyAppService service;

    @Resource
    XySourceAppService xySourceAppService;


    @ApiOperation("分页查询应用列表")
    @GetMapping("/pageList")
    public ResultMap<PageInfo<XyAppVo>> pageList(@ApiParam("媒体账号名称") String username,
                                                       @ApiParam("类型:1.应用名称,2.应用id") Integer type,
                                                       @ApiParam("内容：类型(type)对应的值") String keyword,
                                                       @ApiParam("操作系统:1.Android，2.ios") Integer clientType,
                                                       @ApiParam("状态，0已启用，1待审核，2已禁用") Integer status,
                                                       @ApiParam(value="页码", required=true) Integer pageNum,
                                                       @ApiParam(value="每页记录数", required=true) Integer pageSize) {
        pageNum = pageNum == null ? XyConstant.INIT_PAGE : pageNum;
        pageSize = pageSize == null ? XyConstant.INIT_PAGE_SIZE : pageSize;
        return ResultMap.success(service.pageList(XyConstant.BACK_PLAT,null,username,type,keyword, clientType ,status , pageNum, pageSize));
    }

    @ApiOperation("查询应用列表(已启用)")
    @GetMapping("/list")
    public ResultMap<List<XyAppSimpleVo>> list(@ApiParam("媒体账号的id") Long userId) {
        return ResultMap.success(service.findList(XyConstant.BACK_PLAT,userId));
    }

    @ApiOperation("新增、编辑应用")
    @PostMapping("/save")
    @LogInfo(pathName="后台-应用管理-新增、编辑应用",plat= XyConstant.BACK_PLAT,type = XyConstant.LOG_SAVE)
    public ResultMap save(@ApiParam("应用实体类") @RequestBody @Validated XyApp xyApp) {
        try {
            //todo
            xyApp.setUserId(1L);
            init(xyApp);
            service.save(xyApp);
            return ResultMap.success();
        } catch (ValidateException e) {
            return ResultMap.error(e.getMessage());
        } catch (ServiceException e) {
            return ResultMap.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    private void init(XyApp xyApp) {
        //todo 目前没有定义黑盒和白盒,定义完后。需要根据特定的媒体角色类型,修改对应的状态。
        if(xyApp.getStatus() == 1){
            xyApp.setStatus(0);
        }
        //todo appKey和appSecret需要找技术部拿接口。

        //暂时用生成的方式
        xyApp.setAppKey("");
        xyApp.setAppSecret("");
    }

    @ApiOperation("查询应用信息")
    @GetMapping("/info")
    public ResultMap<XyAppDetailVo> info(@ApiParam("应用实体类") Long appId) {
        try {
            if(appId == null){
                return ResultMap.error("必须传入应用主键");
            }
            return ResultMap.success(service.findById(appId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("审核应用")
    @GetMapping("/examine")
    @LogInfo(pathName="后台-应用管理-审核应用",plat= XyConstant.BACK_PLAT,type = XyConstant.LOG_UPDATE)
    public ResultMap examine(@ApiParam("应用主键") Long appId,@ApiParam("状态，0已启用，1待审核，2已禁用") Integer status) {
        try {
            if(appId == null){
                return ResultMap.error("必须传入应用主键");
            }
            if(status == null){
                return ResultMap.error("必须传入应用状态");
            }
            service.updateStatus(appId, status);
            return ResultMap.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("删除应用")
    @GetMapping("/delete")
    @LogInfo(pathName="后台-应用管理-删除应用",plat= XyConstant.BACK_PLAT,type = XyConstant.LOG_DELETE)
    public ResultMap delete(@ApiParam("应用主键") Long appId) {
        try {
            if(appId == null){
                return ResultMap.error("必须传入应用主键");
            }

            service.deleteById(appId);
            return ResultMap.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("关联的应用信息")
    @GetMapping("/relateAppInfo")
    public ResultMap relateAppInfo(@ApiParam("应用主键") Long appId) {
        try {
            if(appId == null){
                return ResultMap.error("必须传入应用主键");
            }
            List<RelateAppInfoVo> result = xySourceAppService.relateAppInfo(appId);
            return ResultMap.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("保存关联应用")
    @PostMapping("/saveRelateApp")
    @LogInfo(pathName="后台-应用管理-保存关联应用",plat= XyConstant.BACK_PLAT,type = XyConstant.LOG_UPDATE)
    public ResultMap saveRelateApp(@RequestBody List<RelateAppInfoVo> relateAppInfos) {
        try {
            if(relateAppInfos == null){
                return ResultMap.error("必须传入关联信息");
            }
            xySourceAppService.saveRelateApp(relateAppInfos);
            return ResultMap.success();
        } catch (ValidateException e) {
            return ResultMap.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }
}
