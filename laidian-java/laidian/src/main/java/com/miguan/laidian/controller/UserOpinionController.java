package com.miguan.laidian.controller;

import com.github.pagehelper.Page;
import com.miguan.laidian.common.annotation.CommonParams;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.*;
import com.miguan.laidian.common.util.file.AWSUtil;
import com.miguan.laidian.common.util.file.UploadFileModel;
import com.miguan.laidian.service.ClUserOpinionService;
import com.miguan.laidian.vo.ClUserOpinionVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 意见反馈Controller
 *
 * @author laiyd
 * @version 1.8.0
 * @date 2019-11-04
 */
@Api(value = "意见反馈controller", tags = {"意见反馈接口"})
@RestController
@RequestMapping("/api/user")
public class UserOpinionController {

    @Resource
    private ClUserOpinionService clUserOpinionService;

    @ApiOperation(value = "意见反馈提交")
    @PostMapping("/userOpinionSubmit")
    public ResultMap userOpinionSubmit(ClUserOpinionVo clUserOpinionVo) {
        int num = 0;
        if (clUserOpinionVo.getDeviceId() != null || clUserOpinionVo.getUserId() != null) {
            if (StringUtils.isEmpty(clUserOpinionVo.getAppType())){
                clUserOpinionVo.setAppType(Constant.appXld);
            }
            //默认未处理状态
            clUserOpinionVo.setState(ClUserOpinionVo.UNTREATED);
            num = clUserOpinionService.saveClUserOpinion(clUserOpinionVo);
        }
        if (num == 1) {
            return ResultMap.success();
        } else {
            return ResultMap.error();
        }
    }


    @ApiOperation(value = "意见反馈查看列表")
    @PostMapping("/findUserOpinionList")
    public ResultMap findUserOpinionList(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                         @ModelAttribute ClUserOpinionVo clUserOpinionVo) {
        int currentPage = commomParams.getCurrentPage();
        int pageSize = commomParams.getPageSize();
        Page<ClUserOpinionVo> clUserOpinionList = clUserOpinionService.findClUserOpinionList(clUserOpinionVo, currentPage, pageSize);
        Map<String, Object> result = new HashMap<>();
        result.put("page", new RdPage(clUserOpinionList));
        result.put("data", clUserOpinionList);
        return ResultMap.success(result);
    }

    @ApiOperation(value = "意见反馈查看详情")
    @PostMapping("/findUserOpinionInfo")
    public ResultMap findUserOpinionInfo(@ApiParam("用户反馈id") String id) {
        ClUserOpinionVo clUserOpinionById = clUserOpinionService.getClUserOpinionById(id);
        return ResultMap.success(clUserOpinionById);
    }

    @ApiOperation(value = "上传反馈图片到白山云返回URL")
    @PostMapping("/getOpinionImgUrl")
    public ResultMap getImgUrl(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                               @RequestParam(value = "opinionImageFile") MultipartFile opinionImageFile) {
        if(opinionImageFile==null)return ResultMap.error("请上传图片");
        String deviceId = commomParams.getDeviceId();
        String userId = commomParams.getUserId();
        if (StringUtils.isNotEmpty(userId) && StringUtils.isEmpty(deviceId)) {
            deviceId = userId;
        }
        String appType = commomParams.getAppType();
        Map<String, Object> result = new HashMap<>();
        String appEnvironment = Global.getValue("app_environment",appType);
        String folder = "dev-opinionimg-laidian";
        if ("prod".equals(appEnvironment)) {
            folder = "pro-opinionimg-laidian";
        }
        Date currDate = DateUtil.getNow();
        UploadFileModel activeModel = AWSUtil.upload(opinionImageFile, deviceId, folder, "opinion", currDate);
        result.put("bsyOpinionImgUrl", activeModel.getResPath());
        return ResultMap.success(result);
    }
}
