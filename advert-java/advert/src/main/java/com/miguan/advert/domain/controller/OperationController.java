package com.miguan.advert.domain.controller;

import com.miguan.advert.common.util.ResultMap;
import com.miguan.advert.domain.service.AdOperationLogService;
import com.miguan.advert.domain.vo.PageVo;
import com.miguan.advert.domain.vo.request.AdOperationLogQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(value = "操作日志",tags = {"操作日志"})
@RestController
@RequestMapping("/api/Operation")
public class OperationController {

    @Resource
    private AdOperationLogService operationService;

    @ApiOperation("获取账户信息")
    @GetMapping("/Log")
    public ResultMap<PageVo> Log(Integer page, Integer page_size, AdOperationLogQuery adOperationLogQuery) {
        if(page == null || page < 1){
            page = 1;
        }
        if(page_size == null){
            page_size = 10;
        }
        PageVo pageVo = operationService.findPage(page, page_size,adOperationLogQuery);
        return ResultMap.success(pageVo);
    }

}

