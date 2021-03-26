package com.miguan.advert.domain.controller;

import com.cgcg.context.util.StringUtils;
import com.miguan.advert.common.util.ResultMap;
import com.miguan.advert.domain.pojo.AdAdvertCode;
import com.miguan.advert.domain.service.AdAdvertCodeService;
import com.miguan.advert.domain.service.DictionariesService;
import com.miguan.advert.domain.vo.PageVo;
import com.miguan.advert.domain.vo.request.AdAdvertCodeQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "系统字典",tags = {"系统字典"})
@RestController
@RequestMapping("/api/adAdvertCode")
public class AdAdvertCodeController {

    @Resource
    private AdAdvertCodeService adAdvertCodeService;


    @ApiOperation("获取账户信息")
    @PostMapping("/updateOrCreate")
    public ResultMap updateOrCreate(HttpServletRequest request, AdAdvertCode adAdvertCode) {
        if(StringUtils.isEmpty(adAdvertCode.getAd_id())){
            return ResultMap.error("代码位id必填");
        }

        if(StringUtils.isEmpty(adAdvertCode.getPlat_key())){
            return ResultMap.error("请选择所属广告平台");
        }

        if(StringUtils.isEmpty(adAdvertCode.getType_key())){
            return ResultMap.error("请选择广告类型");
        }

        if(StringUtils.isEmpty(adAdvertCode.getApp_package())){
            return ResultMap.error("请选择适用应用");
        }

        if(StringUtils.isEmpty(adAdvertCode.getRender_key())){
            return ResultMap.error("请选择渲染方式");
        }

        if(StringUtils.isEmpty(adAdvertCode.getVersion1()) || StringUtils.isEmpty(adAdvertCode.getVersion2())){
            return ResultMap.error("请填写 作用应用版本");
        }

        //PageVo pageVo = adAdvertCodeService.getList(request.getRequestURL().toString(),page, page_size,query);
        return ResultMap.success();
    }

    @ApiOperation("获取账户信息")
    @PostMapping("/getList")
    public ResultMap<PageVo> getList(HttpServletRequest request, Integer page, Integer page_size, AdAdvertCodeQuery query) {
        if(page == null || page < 1){
            page = 1;
        }
        if(page_size == null){
            page_size = 10;
        }
        PageVo<AdAdvertCode> pageVo = adAdvertCodeService.getList(request.getRequestURL().toString(),page, page_size,query);
        return ResultMap.success(pageVo);
    }

}

