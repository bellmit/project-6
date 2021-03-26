package com.miguan.xuanyuan.controller.back;

import com.alibaba.fastjson.JSONArray;
import com.cgcg.context.util.StringUtils;
import com.github.pagehelper.PageInfo;
import com.miguan.xuanyuan.common.util.Global;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.dto.IdentityListBackDto;
import com.miguan.xuanyuan.service.IdentityService;
import com.miguan.xuanyuan.service.third.ChuanShanJiaService;
import com.miguan.xuanyuan.service.third.GuanDianTongService;
import com.miguan.xuanyuan.service.third.KuaiShouService;
import com.miguan.xuanyuan.service.third.ThirdPlatApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.Date;

@Api(value = "第三方广告平台数据接口", tags = {"第三方广告平台数据接口"})
@RestController
@Slf4j
@RequestMapping("/api/back/third")
public class ThirdPlatApiController {

    @Resource
    private ThirdPlatApiService thirdPlatApiService;

    @ApiOperation("调用第三方广告api接口，导入第三方广告数据")
    @GetMapping("/syncThirdPlatData")
    public ResultMap syncThirdPlatData(@ApiParam("日期，格式：yyyy-MM-dd") String date,
                                       @ApiParam("是否覆盖数据，true：是") Boolean isCover) {
        if(StringUtils.isBlank(date)) {
            date = DateUtil.dateStr2(DateUtil.rollDay(new Date(),-1));
        }
        isCover = (isCover == null ? true : isCover);
        thirdPlatApiService.syncThirdPlatAdvData(date, isCover);
        return ResultMap.success();
    }

    @ApiOperation("按天保存广告配置信息log")
    @GetMapping("/syncAdConfigLog")
    public ResultMap syncAdConfigLog(@ApiParam("日期，格式：yyyy-MM-dd") String date) {
        if(StringUtils.isBlank(date)) {
            date = DateUtil.dateStr2(DateUtil.rollDay(new Date(),-1));
        }
        thirdPlatApiService.syncAdConfigLog(date);
        return ResultMap.success();
    }
}
