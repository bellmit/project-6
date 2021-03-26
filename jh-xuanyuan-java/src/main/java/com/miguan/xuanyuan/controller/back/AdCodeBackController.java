package com.miguan.xuanyuan.controller.back;

import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.enums.OperationEnum;
import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.common.log.annotation.LogInfo;
import com.miguan.xuanyuan.common.security.model.JwtUser;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.controller.AuthBaseController;
import com.miguan.xuanyuan.dto.request.AdCodeRequest;
import com.miguan.xuanyuan.service.XyAdCodeService;
import com.miguan.xuanyuan.vo.AdCodeVo;
import com.miguan.xuanyuan.vo.StrategyCodeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@Api(value = "代码位", tags = {"代码位"})
@Slf4j
@RestController
@RequestMapping("/api/back/code")
public class AdCodeBackController extends AuthBaseController {

    @Resource
    XyAdCodeService XyAdCodeService;


    @ApiOperation("添加广告源")
    @PostMapping(value = {"/add"})
    @LogInfo(pathName="后台-策略聚合管理-添加广告源",plat= XyConstant.BACK_PLAT,type = XyConstant.LOG_UPDATE)
    public ResultMap addCode(@ApiParam("代码位数据") @RequestBody @Valid AdCodeRequest adCodeRequest) throws Exception{
        //todo
        Long userId = 1L;

        try {
            adCodeRequest.check();
            adCodeRequest.setUserId(userId);
            StrategyCodeVo vo = XyAdCodeService.addCode(adCodeRequest);
            return ResultMap.success(vo);
        } catch (ValidateException e) {
            return ResultMap.error(e.getMessage());
        } catch (ServiceException e) {
            return ResultMap.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("更改广告源")
    @PostMapping(value = {"/edit"})
    @LogInfo(pathName="后台-策略聚合管理-更改广告源",plat= XyConstant.BACK_PLAT,type = XyConstant.LOG_UPDATE)
    public ResultMap editCode(@ApiParam("代码位数据") @RequestBody AdCodeRequest adCodeRequest) throws Exception{
        //todo
        Long userId = 1L;
        Long adCodeId = adCodeRequest.getId();
        if (adCodeId == null) {
            return ResultMap.error("id错误");
        }

        AdCodeVo adCodeVo = XyAdCodeService.findById(adCodeId);
        if (adCodeVo == null) {
            return ResultMap.error("广告源不存在");
        }

        try {
            adCodeRequest.check();
            adCodeRequest.setUserId(userId);
            StrategyCodeVo vo = XyAdCodeService.editCode(adCodeRequest);
            return ResultMap.success(vo);
        } catch (ValidateException e) {
            return ResultMap.error(e.getMessage());
        } catch (ServiceException e) {
            return ResultMap.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("获取分组列表")
    @GetMapping(value = {"/detail"})
    public ResultMap detail(@ApiParam("广告源id") @RequestParam Long id) throws Exception{
        if (id == null || id <= 0l) {
            ResultMap.error("参数id错误");
        }
        AdCodeVo adCodeVo = XyAdCodeService.findById(id);
        return ResultMap.success(adCodeVo);
    }

    @ApiOperation("删除广告源")
    @GetMapping(value = {"/del"})
    @LogInfo(pathName="后台-策略聚合管理-删除广告源",plat= XyConstant.BACK_PLAT,type = XyConstant.LOG_DELETE)
    public ResultMap del(@ApiParam("广告源id") @RequestParam Long id) throws Exception{
        //todo
        Long userId = 1L;

        if (id == null || id <= 0l) {
            return ResultMap.error("参数id错误");
        }
        AdCodeVo adCodeVo = XyAdCodeService.findById(id);
        if (adCodeVo == null) {
            return ResultMap.error("广告源不存在");
        }

//        if (adCodeVo.getUserId() != userId) {
//            return ResultMap.error("不能删除他人广告源");
//        }

        AdCodeRequest adCodeRequest = new AdCodeRequest();
        adCodeRequest.setId(id);
        adCodeRequest.setIsDel(XyConstant.DEL_STATUS);
        try {
            XyAdCodeService.update(adCodeRequest);
            return ResultMap.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("查询第三方应用id")
    @GetMapping(value = {"/findSourceAppId"})
    public ResultMap findSourceAppId(@ApiParam("广告源id") Long appId, String platKey) {
        if(appId == null){
            return ResultMap.error("应用id不能为空");
        }
        if(StringUtils.isEmpty(platKey)){
            return ResultMap.error("请先选择平台类型");
        }
        String sourceAppId = XyAdCodeService.findSourceAppId(appId,platKey);
        return ResultMap.success(sourceAppId);
    }
}
