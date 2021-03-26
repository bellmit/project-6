package com.miguan.laidian.controller;

import com.github.pagehelper.Page;
import com.miguan.laidian.common.annotation.CommonParams;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.*;
import com.miguan.laidian.entity.Video;
import com.miguan.laidian.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频Controller
 *
 * @Author xy.chen
 * @Date 2019/9/11
 **/
@Api(value = "上传视频controller", tags = {"上传视频接口"})
@Slf4j
@RestController
@RequestMapping("/api/video")
public class UploadVideosController {

    @Resource
    private VideoService videosService;

    @ApiOperation(value = "保存视频信息")
    @RequestMapping(value = "/saveUploadVideo", method = RequestMethod.POST)
    public ResultMap saveUploadVideo(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                     @ModelAttribute Video videosVo) {
        String appType = commomParams.getAppType();
        int i = videosService.saveUploadVideos(videosVo);
        if (i == 1) {
            //保存视频地址
            Map<String, Object> params = new HashMap<>();
            params.put("bsyImgUrl", videosVo.getBsyImgUrl());
            params.put("appType", appType);
            Page<Video> videosList = videosService.findVideosList(params, 1, 1);
            if (videosList != null && videosList.size() > 0) {
                videosVo = videosList.getResult().get(0);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("uploadVideoInfo", videosVo);
            return ResultMap.success(result, "保存成功");
        } else {
            return ResultMap.error("保存失败");
        }
    }


    /**
     * 上传视频列表
     *
     * @param commomParams
     * @param type
     * @return
     */
    @ApiOperation(value = "上传视频列表")
    @PostMapping("/uploadVideosList")
    public ResultMap videosList(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                @ApiParam("视频类型：1-自己用 2-大家用") String type) {
        String appType = commomParams.getAppType();
        String userId = commomParams.getUserId();
        int currentPage = commomParams.getCurrentPage();
        int pageSize = commomParams.getPageSize();
        Map<String, Object> params = new HashMap<>();
        Page<Video> page = new Page<>();
        if (userId != null && !"".equals(userId)&& !"0".equals(userId)) {
            params.put("userId", userId);
            params.put("tempUserId", userId);
            params.put("type", type);
            params.put("uploadVideoOrder", "1");//上传视频排序
            params.put("appType", appType);
            page = videosService.findVideosList(params, currentPage, pageSize);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("page", new RdPage(page));
        result.put("data", page);
        return ResultMap.success(result);
    }

    @ApiOperation(value = "批量删除上传视频信息")
    @PostMapping("/batchDelUploadVideos")
    public ResultMap batchDelUploadVideos(@ApiParam("上传视频ID 逗号隔开") String uploadVideosIds) {
        String[] split = uploadVideosIds.split(",");
        int num = videosService.batchDelUploadVideos(split);
        if (num > 0) {
            return ResultMap.success();
        } else {
            return ResultMap.error();
        }
    }

    /**
     * 上传视频判断是否需要广告
     *
     * @param commomParams
     * param type
     * @return
     */
    @ApiOperation(value = "上传视频判断是否需要广告")
    @PostMapping("/isNeedAdv")
    public ResultMap isNeedAdv(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                               @ApiParam("上传视频类型：1自己用 2大家用") String type) {
        String appType = commomParams.getAppType();
        String userId = commomParams.getUserId();
        Map<String, Object> result = new HashMap<>();
        String url = YmlUtil.getCommonYml("aws.endPoint");
        String accessKey = YmlUtil.getCommonYml("aws.accessKey");
        String secretKey = YmlUtil.getCommonYml("aws.secretKey");
        String bucket = YmlUtil.getCommonYml("aws.bucketName");
        String appEnvironment = Global.getValue("app_environment", appType);
        String folder = "dev-videos-laidian";
        if ("prod".equals(appEnvironment)) {
            folder = "pro-videos-laidian";
        }
        String str = DateUtil.dateStr(new Date(), DateUtil.DATEFORMAT_STR_013);
        String key = folder + "/uploadVideos/" + str;
        String imgKey = folder + "/uploadImgs/" + str;

        result.put("url", url);
        result.put("accessKey", accessKey);
        result.put("secretKey", secretKey);
        result.put("bucket", bucket);
        result.put("key", key);
        result.put("imgKey", imgKey);
        if ("1".equals(type)) {
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("type", "1");
            params.put("appType", appType);
            Page<Video> page = videosService.findVideosList(params, 1, 10);
            List<Video> videosList = page.getResult();
            if (videosList != null && videosList.size() > 0) {
                result.put("advSign", "0");
            } else {
                result.put("advSign", "1");
            }
        } else {
            result.put("advSign", "0");
        }

        return ResultMap.success(result);
    }
}
