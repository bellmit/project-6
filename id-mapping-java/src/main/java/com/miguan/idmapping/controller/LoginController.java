package com.miguan.idmapping.controller;

import com.cgcg.base.format.Result;
import com.miguan.idmapping.service.ClUserService;
import com.miguan.idmapping.service.WecharService;
import com.miguan.idmapping.vo.ClUserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Api(value = "用户登录注册controller", tags = {"用户登录注册"})
@RestController
public class LoginController {

    @Autowired
    private ClUserService clUserService;

    @Resource
    private WecharService wecharService;

    @GetMapping("/api/test")
    public String test() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * 登录
     * @param request
     * @param clUserVo
     * @param vcode
     */
    @ApiOperation(value = "用户登录")
    @PostMapping("/api/user/login")
    public Result login(HttpServletRequest request,
                        @ApiParam("用户实体") @ModelAttribute ClUserVo clUserVo,
                        @RequestHeader(value = "Public-Info") String publicInfo,
                        @RequestHeader(value = "Common-Attr") String commonAttr,
                        @ApiParam("手机验证码") String vcode) {
        if (clUserVo == null || StringUtils.isEmpty(clUserVo.getLoginName()) || StringUtils.isBlank(clUserVo.getUuid())) {
            return Result.error(400, "参数异常！");
        }
        Map<String, Object> result = clUserService.login(publicInfo, commonAttr, request, clUserVo, vcode);
        String success = result.get("success").toString();
        if ("0".equals(success)) {
            //登录成功
            Result resultS = Result.success(result);
            resultS.setMessage("登录成功");
            return resultS;
        } else {
            //登录失败
            Result resultF = new Result(result);
            resultF.setCode(400);
            resultF.setMessage("登录失败");
            return resultF;
        }
    }

//    @ApiOperation(value = "用户注销")
//    @PostMapping("/api/user/logoff")
//    public Result logoff(@ApiParam("用户Id") Long userId) {
//        if (userId == null || userId < 0) {
//            return Result.error(400, "参数异常");
//        }
//        clUserService.deleteByUserId(userId);
//        return Result.success("用户信息注销成功");
//    }
//
//    @ApiOperation("微信授权")
//    @GetMapping("/wecharAuth")
//    public ResultMap<Map> wecharAuth(@ApiParam("(必传)授权码") String code){
//        Map<String, Object> restMap = new HashMap<>();
//        try{
//            restMap = wecharService.wecharAuth(code);
//        }catch (Exception e){
//            ResultMap.error(restMap);
//        }
//        return ResultMap.success(restMap);
//    }

    @ApiOperation("微信登录")
    @PostMapping("/api/wecharLogin")
    public Result wecharLogin(HttpServletRequest request,
                              @ApiParam("用户实体") @ModelAttribute ClUserVo clUserVo,
                              @RequestHeader(value = "Public-Info") String publicInfo,
                              @RequestHeader(value = "Common-Attr") String commonAttr,
                              @ApiParam("(必传)昵称") @RequestParam(required = false) String nickName,
                              @ApiParam("(必传)headPic") @RequestParam(required = false) String headPic) {
        if (clUserVo == null || StringUtils.isEmpty(clUserVo.getOpenId()) || StringUtils.isBlank(clUserVo.getUuid())) {
            return Result.error(400, "参数异常！");
        }
        Map<String, Object> result = clUserService.wecharLogin(request, publicInfo, commonAttr, clUserVo, nickName, headPic);
        String success = result.get("success").toString();
        if ("0".equals(success)) {
            //登录成功
            Result resultS = Result.success(result);
            resultS.setMessage("登录成功");
            return resultS;
        } else {
            //登录失败
            Result resultF = new Result(result);
            resultF.setCode(400);
            resultF.setMessage("登录失败");
            return resultF;
        }
    }

    @ApiOperation("苹果登录")
    @PostMapping("/api/appleLogin")
    public Result appleLogin(HttpServletRequest request,
                                      @RequestHeader(value = "Public-Info") String publicInfo,
                                      @RequestHeader(value = "Common-Attr") String commonAttr,
                                     @ApiParam("用户实体") @ModelAttribute ClUserVo clUserVo) {
        if (clUserVo == null || StringUtils.isEmpty(clUserVo.getAppleId()) || StringUtils.isBlank(clUserVo.getUuid())) {
            return Result.error(400, "参数异常！");
        }
        Map<String, Object> result = clUserService.appleLogin(request, publicInfo, commonAttr, clUserVo);
        String success = result.get("success").toString();
        if ("0".equals(success)) {
            //登录成功
            Result resultS = Result.success(result);
            resultS.setMessage("登录成功");
            return resultS;
        } else {
            //登录失败
            Result resultF = new Result(result);
            resultF.setCode(400);
            resultF.setMessage("登录失败");
            return resultF;
        }
    }
}
