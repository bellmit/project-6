package com.miguan.advert.domain.controller;

import com.miguan.advert.common.util.ResultMap;
import com.miguan.advert.common.util.StringUtil;
import com.miguan.advert.domain.service.AccountService;
import com.miguan.advert.domain.vo.request.AccountAddVo;
import com.miguan.advert.domain.vo.request.AccountUpdateVo;
import com.miguan.advert.domain.vo.result.AccountDetailInfoVo;
import com.miguan.advert.domain.vo.result.AccountListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(value = "账户信息",tags = {"账户信息"})
@RestController
@RequestMapping("/api/ad_account")
public class AccountController {

    @Resource
    private AccountService accountService;

    //@LoginAuth
    @ApiOperation("获取账户信息")
    @GetMapping("/getData")
    public ResultMap<AccountListVo> getData(HttpServletRequest request,
                                            @ApiParam("当前页面") @RequestParam Integer page,
                                            @ApiParam("页面数量") @RequestParam Integer page_size,
                                            @ApiParam("账号名称") String name,
                                            @ApiParam("公司名称") String company_name,
                                            @ApiParam("状态：0：关闭，1：开启") Integer status) {
        AccountListVo result = accountService.getData(request, page, page_size, name, company_name, status);
        return ResultMap.success(result);
    }

    //@LoginAuth
    @ApiOperation("获取账户详情")
    @GetMapping("/getInfo")
    public ResultMap<AccountDetailInfoVo> getInfo(@ApiParam("账户Id") @RequestParam Integer id) {
        AccountDetailInfoVo result = accountService.getInfo(id);
        return ResultMap.success(result);
    }

    //@LoginAuth
    @ApiOperation("新增账户信息")
    @PostMapping("/getAdd")
    public ResultMap getAdd(HttpServletRequest request,
                            @Validated AccountAddVo accountAddVo) {
        if (!StringUtil.isPhone(accountAddVo.getPhone_num())) {
            return ResultMap.error("手机号不合法");
        }
        int result = accountService.getAdd(request, accountAddVo);
        if (result > 0) {
            return ResultMap.success(true, "新增成功");
        } else {
            return ResultMap.error("新增失败");
        }
    }

    //@LoginAuth
    @ApiOperation("更新账户信息")
    @PostMapping("/update")
    public ResultMap updateAccount(HttpServletRequest request,
                                   @Validated AccountUpdateVo updateVo) {
        if (!StringUtil.isPhone(updateVo.getPhone_num())) {
            return ResultMap.error("手机号不合法");
        }
        int result = accountService.updateAccount(request, updateVo);
        if (result > 0) {
            return ResultMap.success(true, "更新成功");
        } else {
            return ResultMap.error("更新失败");
        }
    }

    //@LoginAuth
    @ApiOperation("更新账户状态")
    @PostMapping("/changeStatus")
    public ResultMap changeStatus(HttpServletRequest request,
                                  @ApiParam("账户Id") @RequestParam Integer id,
                                  @ApiParam("状态") @RequestParam Integer status) {
        AccountUpdateVo updateVo = new AccountUpdateVo();
        updateVo.setId(id);
        updateVo.setStatus(status);
        int result = accountService.updateAccount(request, updateVo);
        if (result > 0) {
            return ResultMap.success(true, "更新成功");
        } else {
            return ResultMap.error("更新失败");
        }
    }

}

