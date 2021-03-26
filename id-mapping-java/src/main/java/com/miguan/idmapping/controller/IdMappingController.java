package com.miguan.idmapping.controller;

import com.cgcg.base.format.Result;
import com.miguan.idmapping.dto.RegAnonymousDto;
import com.miguan.idmapping.service.IAnonymousRegister;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author zhongli
 * @date 2020-07-21 
 *
 */
@Api(value = "匿名用户发号器", tags = {"匿名用户发号器"})
@RestController
public class IdMappingController {

    @Autowired
    private IAnonymousRegister anonymousRegister;


    @ApiOperation(value = "注册匿名用户")
    @PostMapping("/api/register/anonymous")
    public Result getAnonymousId(@ApiParam("匿名注册信息") RegAnonymousDto regAnonymousDto) {
        Map<String, String> map = anonymousRegister.register(regAnonymousDto);
        return Result.success(map);
    }
}
