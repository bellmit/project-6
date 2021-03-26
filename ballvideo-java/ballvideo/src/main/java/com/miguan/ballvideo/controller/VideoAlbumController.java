package com.miguan.ballvideo.controller;

import com.github.pagehelper.Page;
import com.miguan.ballvideo.common.aop.AbTestAdvParams;
import com.miguan.ballvideo.common.aop.CommonParams;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.common.interceptor.argument.params.CommonParamsVo;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.common.util.StringUtil;
import com.miguan.ballvideo.dto.VideoGatherParamsDto;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.RedisService;
import com.miguan.ballvideo.service.VideoAlbumService;
import com.miguan.ballvideo.vo.FirstVideos;
import com.miguan.ballvideo.vo.VideoAlbumVo;
import com.miguan.ballvideo.vo.video.VideoAlbumListResultVo;
import com.miguan.ballvideo.vo.video.VideoAlbumResultVo;
import com.miguan.ballvideo.vo.video.VideoGatherVo;
import com.miguan.ballvideo.vo.video.Videos161Vo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Api(value="视频专辑相关接口",tags={"视频专辑相关接口"})
@RequestMapping("/api/videoAlbum")
@RestController
public class VideoAlbumController {

    @Resource
    private VideoAlbumService videoAlbumService;

    @Resource
    private RedisService redisService;

    @ApiOperation(value = "视频专辑列表接口")
    @GetMapping("/getVideoAlbumList")
    public ResultMap getVideoAlbumList(@ApiParam("公共参数") VideoGatherParamsDto params){
        List<VideoAlbumVo> videoAlbumList = videoAlbumService.getVideoAlbumList(params);
        return ResultMap.success(videoAlbumList);
    }

    @ApiOperation(value = "视频专辑列表接口")
    @GetMapping("/getVideoAlbumList/2.6")
    public ResultMap<VideoAlbumListResultVo> getVideoAlbumList26(@ApiParam("公共参数") VideoGatherParamsDto params,
                                                                 @AbTestAdvParams AbTestAdvParamsVo queueVo){
        VideoAlbumListResultVo videoAlbum = videoAlbumService.getVideoAlbumList26(queueVo,params);
        return ResultMap.success(videoAlbum);
    }

    @ApiOperation(value = "查询视频专辑詳情列表接口")
    @GetMapping("/getVideoAlbumDetailList")
    public ResultMap getVideoAlbumDetailList(@ApiParam("专辑ID") @RequestParam Long albumId,
                                             @ApiParam("公共参数") @CommonParams CommonParamsVo params){
        Page<Videos161Vo> videoAlbumDetailList = videoAlbumService.getVideoAlbumDetailList(albumId, params);
        return ResultMap.success(videoAlbumDetailList);
    }

    @ApiOperation(value = "查询视频专辑詳情列表接口")
    @GetMapping("/getVideoAlbumDetailList/2.6")
    public ResultMap<VideoAlbumResultVo> getVideoAlbumDetailList26(@ApiParam("专辑ID") @RequestParam Long albumId,
                                                                   @ApiParam("广告位置ID") @RequestParam String positionType,
                                                                   @ApiParam("安卓是够开启储存、IMEI权限") String permission,
                                                                   @ApiParam("公共参数") @CommonParams CommonParamsVo params,
                                                                   @AbTestAdvParams AbTestAdvParamsVo queueVo){
        VideoAlbumResultVo videoAlbumDetailList = videoAlbumService.getVideoAlbumDetailList(queueVo, albumId, params, positionType, permission);
        return ResultMap.success(videoAlbumDetailList);
    }

    @ApiOperation(value = "专辑视频详情接口")
    @PostMapping("/videosDetail")
    public ResultMap firstVideosDetail(@ApiParam("用户ID") @RequestParam(required = false) String userId,
                                       @ApiParam("视频ID") @RequestParam String id,
                                       @ApiParam("专辑ID") @RequestParam Long albumId,
                                       @ApiParam("手机类型：1-ios，2：安卓") @RequestParam String mobileType,
                                       @ApiParam("版本号") @RequestParam String appVersion) {
        FirstVideos firstVideos = videoAlbumService.firstVideosDetail(userId, id, albumId, mobileType, appVersion);
        return ResultMap.success(firstVideos);
    }

    @ApiOperation(value = "专辑修改时间戳（PHP调用）")
    @PostMapping("/setAlbumChangeTime")
    public ResultMap setAlbumChangeTime(@ApiParam("专辑修改时间戳") @RequestParam Long albumChangeTime) {
        redisService.set(RedisKeyConstant.ALBUM_CHANGE_TIME, albumChangeTime, -1);
        return ResultMap.success();
    }

    @ApiOperation(value = "专辑菜单栏tips提示（app端调用）")
    @PostMapping("/getAlbumChangeTime")
    public ResultMap getAlbumChangeTime() {
        long albumChangeTime;
        if (redisService.exits(RedisKeyConstant.ALBUM_CHANGE_TIME)){
            albumChangeTime = Long.valueOf(redisService.get(RedisKeyConstant.ALBUM_CHANGE_TIME));
        }else {
            albumChangeTime = System.currentTimeMillis() / 1000;
            redisService.set(RedisKeyConstant.ALBUM_CHANGE_TIME, albumChangeTime, -1);
        }
        return ResultMap.success(albumChangeTime);
    }

    @ApiOperation(value = "搜索页面默认专集查询")
    @GetMapping("/getDefaultVideos")
    public ResultMap getDefaultVideos(@ApiParam("关键字")String userId,
                                      @ApiParam("公共参数")VideoGatherParamsDto params,
                                      @AbTestAdvParams AbTestAdvParamsVo queueVo){
        Object o = videoAlbumService.getDefaultVideos(queueVo,userId,params);
        return ResultMap.success(o);
    }

    @ApiOperation(value = "详情页专辑列表查询")
    @GetMapping("/getVideosByHomePage")
    public ResultMap getVideosByHomePage(@ApiParam("设备ID") String deviceId,
                                         @ApiParam("专辑ID") Long albumId,
                                         @ApiParam("最后/最前一个视频ID的权重")  Long totalWeight,
                                         @ApiParam("最后/最前一个视频ID(V2.5新增)")  Long videoId,
                                         @ApiParam("往左left/往右right") String step,
                                         @ApiParam("马甲包") String appPackage){
        VideoGatherVo result = videoAlbumService.getVideoAlbums(albumId,videoId,step,appPackage);
        return ResultMap.success(result);
    }

    @ApiOperation(value = "刷新视频Id所属专辑缓存（PHP调用）")
    @PostMapping("/updateVideoIdAlbumChange")
    public ResultMap updateVideoIdAlbumChange(@ApiParam("所属专辑Id") @RequestParam String albumId,
                                              @ApiParam("批量视频Id,逗号隔开") @RequestParam String videoIds) {
        videoAlbumService.updateVideoIdAlbumChange(albumId, videoIds);
        return ResultMap.success();
    }

    @ApiOperation(value = "刷新全部视频Id所属专辑缓存（开发调用）")
    @GetMapping("/changeVideoIdAlbum")
    public ResultMap changeVideoIdAlbum() {
        videoAlbumService.changeVideoIdAlbum();
        return ResultMap.success();
    }

    @ApiOperation(value = "下一个专辑视频查询")
    @GetMapping("/getNextAlbumVideo")
    public ResultMap<List<Videos161Vo>> getNextAlbumVideo(@ApiParam("渠道ID") String channelId,
                                                          @ApiParam("版本") String appVersion,
                                                          @ApiParam("专辑ID") Long albumId,
                                                          @ApiParam("当前视频ID") @RequestParam Long videoId,
                                                          @RequestHeader(value = "Public-Info", required = false) String publicInfo,
                                                          @RequestHeader(value = "ab-exp", required = false) String abExp,
                                                          @RequestHeader(value = "ab-isol", required = false) String abIsol,
                                                          @ApiParam("马甲包") String appPackage,
                                                          HttpServletRequest request){
        List<Videos161Vo> result = videoAlbumService.getNextAlbumVideo(albumId,videoId,publicInfo,appPackage,channelId,appVersion,abExp,abIsol);
        if (CollectionUtils.isEmpty(result)) {
            StringUtil.printErrorLog(request,2);
            log.error("abExp:{},abIsol:{}",abExp,abIsol);
        }
        return ResultMap.success(result);
    }
}
