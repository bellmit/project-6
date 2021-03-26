package com.miguan.advert.domain.controller;

import com.miguan.advert.common.util.ResultMap;
import com.miguan.advert.domain.service.UserService;
import com.miguan.advert.domain.vo.result.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(value = "用户信息",tags = {"用户信息"})
@RestController
@RequestMapping("/api/adminUser")
public class UserInfoController {

    @Resource
    private UserService userService;

    //@LoginAuth
    @ApiOperation("获取用户信息")
    @GetMapping("/getData")
    public ResultMap<UserListVo> getData(HttpServletRequest request,
                                         @ApiParam("当前页面") @RequestParam Integer page,
                                         @ApiParam("页面数量") @RequestParam Integer page_size) {
        UserListVo result = userService.getData(request, page, page_size);
        return ResultMap.success(result);
    }
}

