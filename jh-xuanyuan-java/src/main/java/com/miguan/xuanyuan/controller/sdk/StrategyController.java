package com.miguan.xuanyuan.controller.sdk;


import com.miguan.xuanyuan.common.aop.AbTestAdvParams;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.dto.AdDataDto;
import com.miguan.xuanyuan.dto.AdvertCodeParamDto;
import com.miguan.xuanyuan.dto.AdvertPositionParamDto;
import com.miguan.xuanyuan.dto.common.AbTestAdvParamsDto;
import com.miguan.xuanyuan.service.AdCodeService;
import com.miguan.xuanyuan.vo.sdk.AdPositionVo;
import com.miguan.xuanyuan.vo.sdk.ConfigureInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @Description 策略
 * @Date 2021/1/30
 **/
@Api(value = "策略控制层", tags = {"策略接口"})
@RestController
@Slf4j
@RequestMapping("/api/sdk/strategy")
public class StrategyController {

    @Resource
    private AdCodeService adCodeService;

    @ApiOperation(value = "获取应用配置", consumes = "none")
    @RequestMapping("/configure")
    public ResultMap<ConfigureInfoVo> configure(String appKey,String secretKey) {
        if(StringUtils.isEmpty(appKey)){
            return ResultMap.error("缺少应用KEY");
        }
        if(StringUtils.isEmpty(secretKey)){
            return ResultMap.error("缺少应用AppSecret");
        }
        ConfigureInfoVo vo = null;
        try {
            vo = adCodeService.configureInfo(appKey,secretKey);
        } catch (ValidateException e) {
            e.getMessage();
            ResultMap.error(e.getMessage());
        } catch (Exception e) {
            e.getMessage();
            ResultMap.error("未知错误,请稍后再试。");
        }
        return ResultMap.success(vo);
    }

    @ApiOperation(value = "查找广告位的规则", consumes = "none")
    @RequestMapping("/findPositionCustomRule")
    public ResultMap<AdPositionVo> findPositionCustomRule(@AbTestAdvParams AbTestAdvParamsDto queueVo,
                                                          @Valid AdvertPositionParamDto paramDto) {
        if (paramDto == null || StringUtils.isEmpty(paramDto.getPositionKey())|| StringUtils.isBlank(paramDto.getAppKey())
                || StringUtils.isBlank(paramDto.getMobileType())) {
            return ResultMap.success();
        }
        log.debug("findPositionCustomRule:上报的paramDto:{}", paramDto);
        log.debug("infoList:AbTestAdvParamsDto:{}", queueVo);

        AdPositionVo adPositionVo = adCodeService.findPositionCustomRule(queueVo, paramDto);
        return ResultMap.success(adPositionVo);
    }

    @ApiOperation(value = "广告代码位信息列表接口", consumes = "none")
    @RequestMapping("/infoList")
    public ResultMap<AdDataDto> advCodeInfoList(@AbTestAdvParams AbTestAdvParamsDto queueVo,
                                                @Valid AdvertCodeParamDto paramDto) {
        if (paramDto == null || StringUtils.isEmpty(paramDto.getPositionKey()) || StringUtils.isBlank(paramDto.getAppKey())
                || StringUtils.isBlank(paramDto.getMobileType())) {
            return ResultMap.success();
        }
        log.debug("infoList:上报的paramDto:{}", paramDto);
        log.debug("infoList:AbTestAdvParamsDto:{}", queueVo);

        AdDataDto adDataDto = adCodeService.adCodeInfoList(queueVo, paramDto);
        return ResultMap.success(adDataDto);
    }
}
