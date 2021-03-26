package com.miguan.laidian.controller;

import com.miguan.laidian.common.annotation.CommonParams;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.DateUtil;
import com.miguan.laidian.common.util.Global;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.common.util.file.AWSUtil;
import com.miguan.laidian.common.util.file.UploadFileModel;
import com.miguan.laidian.service.ClUserOpinionService;
import com.miguan.laidian.vo.ClUserOpinionVo;
import com.miguan.laidian.vo.ShareVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Api(value = "临时controll", tags = {"临时接口(兼容旧版本)"})
@RestController
public class TemporaryController {

    @Resource
    private ClUserOpinionService clUserOpinionService;

    @ApiOperation(value = "上传头像图片到白山云返回URL")
    @RequestMapping(value = "/api/video/getHeadImgUrl", method = RequestMethod.POST)
    public ResultMap getHeadImgUrl(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                   @RequestParam MultipartFile headImageFile) {
        String appType = commomParams.getAppType();
        String userId = commomParams.getUserId();
        Map<String, Object> result = new HashMap<>();
        if (headImageFile != null) {
            String appEnvironment = Global.getValue("app_environment", appType);
            String folder = "";
            if ("prod".equals(appEnvironment)) {
                folder = "pro-headimg-laidian";
            } else {
                folder = "dev-headimg-laidian";
            }
            Date currDate = DateUtil.getNow();
            UploadFileModel activeModel = AWSUtil.upload(headImageFile, userId, folder, "head", currDate);
            result.put("bsyHeadImgUrl", activeModel.getResPath());
            return ResultMap.success(result);
        }
        return ResultMap.error("请上传图片");
    }

    @ApiOperation(value = "上传反馈图片到白山云返回URL")
    @RequestMapping(value = "/api/video/getOpinionImgUrl", method = RequestMethod.POST)
    public ResultMap getImgUrl(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                               @RequestParam(value = "opinionImageFile") MultipartFile opinionImageFile) {
        String appType = commomParams.getAppType();
        String userId = commomParams.getUserId();
        Map<String, Object> result = new HashMap<>();
        if (opinionImageFile != null) {
            String appEnvironment = Global.getValue("app_environment", appType);
            String folder = "";
            if ("prod".equals(appEnvironment)) {
                folder = "pro-opinionimg-laidian";
            } else {
                folder = "dev-opinionimg-laidian";
            }
            Date currDate = DateUtil.getNow();
            UploadFileModel activeModel = AWSUtil.upload(opinionImageFile, userId, folder, "opinion", currDate);
            result.put("bsyOpinionImgUrl", activeModel.getResPath());
            return ResultMap.success(result);
        }
        return ResultMap.error("请上传图片");
    }

    @ApiOperation(value = "意见反馈提交")
    @PostMapping("/api/video/userOpinionSubmit")
    public ResultMap userOpinionSubmit(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                       ClUserOpinionVo clUserOpinionVo) {
        int num = 0;
        if (clUserOpinionVo.getId() == null && clUserOpinionVo.getUserId() != null) {
            String appType = commomParams.getAppType();
            clUserOpinionVo.setAppType(appType);
            clUserOpinionVo.setState("0");//默认未处理状态
            num = clUserOpinionService.saveClUserOpinion(clUserOpinionVo);
        }
        if (num == 1) {
            return ResultMap.success();
        } else {
            return ResultMap.error();
        }
    }

    @ApiOperation(value = "分享接口")
    @PostMapping("/api/laidian/shareInfo")
    public ResultMap<ShareVo> shareInfo(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                        @ApiParam("视频id（不传的话表示是ios的分享）") String videoId) {
        String shareTitle = ""; //分享标题
        String shareContent = ""; //分享内容
        String shareUrl = "";  //分享链接地址

        if (StringUtils.isNotBlank(videoId)) {
            //Android的分享
            shareUrl = Global.getValue("android_share_url", commomParams.getAppType());
            shareTitle = Global.getValue("android_share_title", commomParams.getAppType());
            shareContent = Global.getValue("android_share_content", commomParams.getAppType());
            shareUrl = shareUrl + "?videoId=" + videoId;
        } else {
            //ios的分享
            shareUrl = Global.getValue("ios_share_url", commomParams.getAppType());
            shareTitle = Global.getValue("ios_share_title", commomParams.getAppType());
            shareContent = Global.getValue("ios_share_content", commomParams.getAppType());
        }
        String logoUrl = Global.getValue("logo_url", commomParams.getAppType()); //logo地址
        ShareVo shareVo = new ShareVo();
        shareVo.setShareTitle(shareTitle);
        shareVo.setShareContent(shareContent);
        shareVo.setShareUrl(shareUrl);
        shareVo.setLogoUrl(logoUrl);
        return ResultMap.success(shareVo);
    }
}
