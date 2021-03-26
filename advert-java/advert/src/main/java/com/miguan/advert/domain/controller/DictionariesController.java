package com.miguan.advert.domain.controller;

import com.miguan.advert.common.util.ResultMap;
import com.miguan.advert.domain.service.DictionariesService;
import com.miguan.advert.domain.vo.PageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(value = "系统字典",tags = {"系统字典"})
@RestController
@RequestMapping("/api/dictionaries")
public class DictionariesController {

    @Resource
    private DictionariesService dictionariesService;

    @ApiOperation("获取账户信息")
    @GetMapping("/getList")
    public ResultMap<PageVo> getList(Integer page,Integer page_size) {
        if(page == null || page < 1){
            page = 1;
        }
        if(page_size == null){
            page_size = 2;
        }
        PageVo pageVo = dictionariesService.getList(page, page_size);
        return ResultMap.success(pageVo);
    }

}

