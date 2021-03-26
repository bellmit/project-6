package com.miguan.xuanyuan.controller.front;

import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.log.annotation.LogInfo;
import com.miguan.xuanyuan.common.security.model.JwtUser;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.controller.AuthBaseController;
import com.miguan.xuanyuan.dto.IdentityFrontDto;
import com.miguan.xuanyuan.dto.request.ResetPasswordFrontRequest;
import com.cgcg.base.core.exception.CommonException;
import com.miguan.xuanyuan.entity.Identity;
import com.miguan.xuanyuan.entity.User;
import com.miguan.xuanyuan.entity.XyUser;
import com.miguan.xuanyuan.service.IdentityService;
import com.miguan.xuanyuan.service.UserService;
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
import java.util.List;

@Api(value = "媒体账号(前台)", tags = {"媒体账号(前台)接口"})
@RestController
@Slf4j
@RequestMapping("/api/front/identity")
public class IdentityFrontController extends AuthBaseController {

    @Resource
    private IdentityService identityService;
    @Resource
    private UserService userService;

    @Resource
    XyUserService xyUserService;

    @ApiOperation("保存媒体账号（根据id进行新增或修改操作）")
    @PostMapping("/saveIdentity")
    @LogInfo(pathName="前台-媒体账号-保存媒体账号",plat= XyConstant.FRONT_PLAT,type = XyConstant.LOG_SAVE)
    public ResultMap saveIdentity(@RequestBody @Validated IdentityFrontDto identityFrontDto) {
        Integer userId = (identityFrontDto.getUserId() == null ? getCurrentUser().getUserId().intValue() : identityFrontDto.getUserId());
        identityFrontDto.setUserId(userId);

        Identity identity = new Identity();
        BeanUtils.copyProperties(identityFrontDto, identity);

        // 必填项如果有修改的话，修改成“审核中”
        if(identityFrontDto.getId() != null) {
            Identity oldIdentity = identityService.getIdentityByUserId(userId);
            String name = identityFrontDto.getName();
            String certificateImg = identityFrontDto.getCertificateImg();
            String oldName = oldIdentity.getName();
            String oldCertificateImg = oldIdentity.getCertificateImg();
            if(!name.equals(oldName) || !certificateImg.equals(oldCertificateImg)) {
                identity.setStatus(2);  //状态修改成 “审核中”
            }
        }
        if(identity.getId() == null) {
            identity.setStatus(2);  //新增后状态修改成"审核中"
        }
        identityService.saveIdentity(identity);
        return ResultMap.success();
    }

    @ApiOperation("根据用户id查询媒体账号信息")
    @GetMapping("/getIdentity")
    public ResultMap<IdentityFrontDto> getIdentity() {
        Integer userId = getCurrentUser().getUserId().intValue();
        User user = userService.getUserById(userId);
        IdentityFrontDto identityFrontDto = new IdentityFrontDto();
        Identity identity = identityService.getIdentityByUserId(userId);
        if(identity != null) {
            BeanUtils.copyProperties(identity, identityFrontDto);
        }
        identityFrontDto.setUsername(user.getUsername());
        identityFrontDto.setUserId(userId);
        identityFrontDto.setPhone(user.getPhone());
        return ResultMap.success(identityFrontDto);
    }

    @ApiOperation("重置密码")
    @PostMapping(value = {"/resetPassword"})
    @LogInfo(pathName="前台-媒体账号-重置密码",plat= XyConstant.FRONT_PLAT,type = XyConstant.LOG_UPDATE)
    public ResultMap resetPassword(@RequestBody @Valid ResetPasswordFrontRequest resetPasswordFrontRequest) {

        JwtUser userInfo = getCurrentUser();
        if (userInfo == null) {
            return ResultMap.error("用户未登录");
        }
        Long userId = userInfo.getUserId();

        String originalPassword = resetPasswordFrontRequest.getOriginalPassword();
        String password = resetPasswordFrontRequest.getPassword();
        String passwordRe = resetPasswordFrontRequest.getPasswordRe();

        if (!password.equals(passwordRe)) {
            return ResultMap.error("确认密码不一致");
        }

        if (originalPassword.equals(password)) {
            return ResultMap.error("不能与原密码一样");
        }

        XyUser xyUser = xyUserService.getById(userId);
        if (xyUser == null || xyUser.getStatus() != XyConstant.STATUS_NORMAL) {
            return ResultMap.error("用户不存在");
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        boolean passwordMatch = passwordEncoder.matches(originalPassword,xyUser.getPassword());
        if (!passwordMatch) {
            return ResultMap.error("原密码输入错误");
        }

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
