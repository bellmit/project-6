package com.miguan.xuanyuan.controller.back;

import com.cgcg.base.core.exception.CommonException;
import com.github.pagehelper.PageInfo;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.log.annotation.LogInfo;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.dto.IdentityBackDto;
import com.miguan.xuanyuan.dto.IdentityListBackDto;
import com.miguan.xuanyuan.dto.request.ResetPasswordBackRequest;
import com.miguan.xuanyuan.entity.XyUser;
import com.miguan.xuanyuan.service.IdentityService;
import com.miguan.xuanyuan.service.XyUserService;
import com.miguan.xuanyuan.vo.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tool.util.StringUtil;

import javax.annotation.Resource;
import javax.validation.Valid;

@Api(value = "媒体账号(后台)", tags = {"媒体账号(后台)接口"})
@RestController
@Slf4j
@RequestMapping("/api/back/identity")
public class IdentityBackController {

    @Resource
    private IdentityService identityService;

    @Resource
    XyUserService xyUserService;

    @ApiOperation("分页查询媒体账号列表")
    @GetMapping("/pageIdentityList")
    public ResultMap<PageInfo<IdentityListBackDto>> pageIdentityList(String phone, String name, Integer status, Integer pageNum, Integer pageSize) {
        PageInfo<IdentityListBackDto> pageList = identityService.pageIdentityList(phone, name, status, pageNum, pageSize);
        return ResultMap.success(pageList);
    }

    @ApiOperation("保存媒体账号（根据id进行新增或修改操作）")
    @PostMapping("/saveIdentity")
    @LogInfo(pathName="后台-媒体账号-保存媒体账号",plat= XyConstant.BACK_PLAT,type = XyConstant.LOG_SAVE)
    public ResultMap saveIdentity(@RequestBody @Validated IdentityBackDto identityBackDto) {
        this.verification(identityBackDto);  //验证
        identityService.saveBackIdentity(identityBackDto);
        return ResultMap.success();
    }

    private void verification(IdentityBackDto identityBackDto) {
        Integer profitRate = identityBackDto.getProfitRate();
        Integer platRate = identityBackDto.getPlatRate();
        if(profitRate.intValue() + platRate.intValue() != 100) {
            throw new CommonException("媒体和平台分成比例总和要等于100%");
        }
    }

    @ApiOperation("根据id查询媒体账号信息")
    @GetMapping("/getIdentity")
    public ResultMap<IdentityBackDto> getIdentity(@ApiParam(value="媒体账号id") Integer identityId, @ApiParam(value="用户id") Integer id) {
        IdentityBackDto identityBackDto = identityService.getBackIdentity(identityId, id);
        return ResultMap.success(identityBackDto);
    }


    @ApiOperation("重置密码")
    @PostMapping(value = {"/resetPassword"})
    @LogInfo(pathName="后台-媒体账号-重置密码",plat= XyConstant.BACK_PLAT,type = XyConstant.LOG_UPDATE)
    public ResultMap resetPassword(@RequestBody @Valid ResetPasswordBackRequest resetPasswordBackRequest) {

        String password = resetPasswordBackRequest.getPassword();
        String passwordRe = resetPasswordBackRequest.getPasswordRe();
        if (!password.equals(passwordRe)) {
            return ResultMap.error("确认密码不一致");
        }

        XyUser xyUser = xyUserService.getById(resetPasswordBackRequest.getUserId());
        if (xyUser == null || xyUser.getStatus() != XyConstant.STATUS_NORMAL) {
            return ResultMap.error("用户不存在");
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        password = passwordEncoder.encode(password);
        xyUser.setPassword(password);

        try {
            xyUserService.updateById(xyUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error("保存数据错误");
        }

        UserVo vo = new UserVo();
        BeanUtils.copyProperties(xyUser, vo);
        return ResultMap.success(vo);
    }
}
