package com.miguan.xuanyuan.controller.sdk;

import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.dto.CreativeParamsDto;
import com.miguan.xuanyuan.service.AdCodeService;
import com.miguan.xuanyuan.service.OriginalityService;
import com.miguan.xuanyuan.vo.CreativeInfoVo;
import com.miguan.xuanyuan.vo.sdk.ConfigureInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @Description 品牌
 * @Date 2021/1/30
 **/
@Api(value = "创意控制层", tags = {"创意接口"})
@RestController
@Slf4j
@RequestMapping("/api/sdk/originality")
public class OriginalityController {

    @Resource
    private OriginalityService service;

    @ApiOperation(value = "获取创意信息", consumes = "none")
    @RequestMapping("/creativeInfo")
    public ResultMap<CreativeInfoVo> creativeInfo(@Valid CreativeParamsDto queueVo) {
        try {
            CreativeInfoVo creativeInfoVo = service.creativeInfo(queueVo);
            return ResultMap.success(creativeInfoVo);
        } catch (Exception e) {
            e.getMessage();
            return ResultMap.error("未知错误,请稍后再试。");
        }
    }
}
