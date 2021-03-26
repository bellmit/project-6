package com.miguan.laidian.controller;

import com.github.pagehelper.Page;
import com.miguan.laidian.common.annotation.CommonParams;
import com.miguan.laidian.common.aop.RequestCache;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.Global;
import com.miguan.laidian.common.util.RdPage;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.entity.Video;
import com.miguan.laidian.entity.VideosCat;
import com.miguan.laidian.service.SmallVideoService;
import com.miguan.laidian.service.VideoService;
import com.miguan.laidian.service.VideoSettingService;
import com.miguan.laidian.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频Controller
 *
 * @Author xy.chen
 * @Date 2019/7/8
 **/
@Api(value = "视频controller", tags = {"视频接口"})
@RestController
@RequestMapping("/api/video")
public class VideoController {

    @Resource
    private VideoService videosService;

    @Autowired
    SmallVideoService iOSVideosService;

    @Resource
    private VideoSettingService videoSettingService;

    private static final String BULK_DELETE_FAVORITE = "1";

    /**
     * 视频分类
     *
     * @return
     */
    @RequestCache
    @ApiOperation(value = "视频分类")
    @PostMapping("/videosCatList")
    public ResultMap videosCatList() {
        List<VideosCat> videosCatList = videosService.findAll();
        return ResultMap.success(videosCatList);
    }

    /**
     * 视频源列表
     *
     * @param catId
     * @param videoType
     * @return
     */
    //@RequestCache
    @ApiOperation(value = "视频源列表")
    @PostMapping("/videosList")
    public ResultMap videosList(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                @ApiParam("视频分类ID") String catId,
                                @ApiParam("从锁屏页面进入时候，当前接口会传入视频ID") String videoId,
                                @ApiParam("类型：10--热门 20--最新 30--其他分类") String videoType,
                                @ApiParam("AB实验标识：1-A实验（旧逻辑）,2-B实验（新逻辑）") String ABTestFlag) {
        Map<String, Object> params = new HashMap<>();
        if ("30".equals(videoType) && StringUtils.isNotBlank(catId)) {
            params.put("catId", Integer.parseInt(catId));
        }
        if (StringUtils.isNotBlank(videoId)) {
            params.put("videoId", videoId);//视频id
        }
        params.put("state", Constant.open);//状态 1开启 2关闭
        params.put("videoType", videoType);
        params.put("approvalState", "2");//默认审批通过状态
        params.put("recommend", Video.RECOMMEND);//只查看推荐的视频 add shixh 20190911
        params.put("appType", commomParams.getAppType());
        params.put("channelId", commomParams.getChannelId());//渠道
        params.put("tempUserId", commomParams.getUserId());//用户ID
        params.put("deviceId", commomParams.getDeviceId());
        params.put("appVersion", commomParams.getAppVersion());
        params.put("ABTestFlag", ABTestFlag);
        Page<Video> page = videosService.findVideosList(params, commomParams.getCurrentPage(), commomParams.getPageSize());
        Map<String, Object> result = new HashMap<>();
        result.put("page", new RdPage(page));
        result.put("data", page);
        return ResultMap.success(result);
    }

    @ApiOperation(value = "获取随机视频列表")
    @PostMapping("/randomVideosList")
    public ResultMap randomVideosList(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams) {
        Map<String, Object> params = new HashMap<>();
        params.put("state", Constant.open);//状态 1开启 2关闭
        params.put("approvalState", "2");//默认审批通过状态
        params.put("recommend", Video.RECOMMEND);//只查看推荐的视频 add shixh 20190911
        params.put("appType", commomParams.getAppType());
        params.put("randomTag", Constant.open);  //获取随机视频标识，randomTag不为空则获取随机视频。
        params.put("tempUserId", commomParams.getUserId());//用户ID
        Page<Video> page = videosService.findVideosList(params, commomParams.getCurrentPage(), commomParams.getPageSize());
        Map<String, Object> result = new HashMap<>();
        result.put("page", new RdPage(page));
        result.put("data", page);
        return ResultMap.success(result);
    }

    /**
     * 视频播放详情
     *
     * @param videoId
     * @return
     */
    @RequestCache
    @ApiOperation(value = "视频播放详情")
    @PostMapping("/videosDetail")
    public ResultMap videosDetail(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                  @ApiParam("视频ID") @RequestParam String videoId) {
        Map<String, Object> params = new HashMap<>();
        params.put("state", Constant.open);//状态 1开启 2关闭
        params.put("id", Integer.parseInt(videoId));
        params.put("appType", commomParams.getAppType());
        params.put("tempUserId", commomParams.getUserId());//用户ID
        Video videosVo = videosService.findOne(params);
        if (videosVo != null) return ResultMap.success(videosVo);
        return ResultMap.error("查无视频数据");
    }

    /**
     * 更新用户收藏数量、分享数量、点击数
     *
     * @param id
     * @param opType
     * @return
     */
    @ApiOperation(value = "更新用户收藏数量、分享数量、点击数")
    @PostMapping("/updateCount")
    public ResultMap updateCount(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                 @ApiParam("视频ID") @RequestParam("id") String id,
                                 @ApiParam("操作类型：10--收藏 20--分享 30--取消收藏 40--点击 50--来电设置成功数") @RequestParam(value = "opType") String opType) {
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(id)) {
            params.put("id", Integer.parseInt(id));
            params.put("videoId", Integer.parseInt(id));
        }
        params.put("opType", opType);
        params.put("appType", commomParams.getAppType());
        boolean success = videosService.updateCountSendMQ(params);
        if (success) return ResultMap.success();
        return ResultMap.error();
    }

    /**
     * 查询收藏信息列表
     *
     * @param commomParams
     * @return
     */
    @ApiOperation(value = "收藏列表（这个接口是不是没有用了）")
    @PostMapping("/collectionList")
    public ResultMap collectionList(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams) {
        Map<String, Object> params = new HashMap<>();
        params.put("deviceId", commomParams.getDeviceId());
        params.put("appType", commomParams.getAppType());
        int currentPage = commomParams.getCurrentPage();
        int pageSize = commomParams.getPageSize();
        Page<Map<String, Object>> page = videosService.findCollectionList(params, currentPage, pageSize);
        Map<String, Object> result = new HashMap<>();
        result.put("page", new RdPage(page));
        result.put("data", page);
        return ResultMap.success(result);
    }

    /**
     * 更新用户收藏数、点赞数、观看数、是否感兴趣
     *
     * @param
     * @return
     */
    @ApiOperation(value = "查看我的收藏")
    @PostMapping("/findMyCollection")
    public ResultMap findMyCollection(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams) {
        Page<SmallVideoVo> page = iOSVideosService.findMyCollection(commomParams.getUserId(), commomParams.getCurrentPage(), commomParams.getPageSize(), commomParams.getAppType());
        Map<String, Object> result = new HashMap<>();
        result.put("page", new RdPage(page));
        //TODO 安卓已打包市场 临时处理
        int pages = page.getPages();
        if (commomParams.getCurrentPage() > pages) {
            page = new Page<>();
        }
        result.put("data", page);
        return ResultMap.success(result);
    }

    /**
     * 设置收藏类型
     *
     * @param id
     * @param opType
     * @return
     */
    @ApiOperation(value = "设置收藏类型(这个接口是不是没有用了)")
    @PostMapping("/updateType")
    public ResultMap updateType(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                @ApiParam("收藏ID") @RequestParam("id") String id,
                                @ApiParam("操作类型：收藏类型 10 专属  20默认") String opType) {
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(id)) {
            params.put("id", Integer.parseInt(id));
        }
        params.put("opType", opType);
        params.put("appType", commomParams.getAppType());
        int num = videosService.updateType(params);
        if (num == 1) {
            return ResultMap.success();
        }
        return ResultMap.error();
    }

    @ApiOperation(value = "分享接口")
    @PostMapping("/shareInfo")
    public ResultMap<ShareVo> shareInfo(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                        @ApiParam("视频id（不传的话表示是ios的分享）") String videoId) {
        String shareTitle; //分享标题
        String shareContent; //分享内容
        String shareUrl;  //分享链接地址
        String logoUrl;//logo地址
        Video videosVo = null;
        if (StringUtils.isNotBlank(videoId)) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", Integer.parseInt(videoId));
            videosVo = videosService.findVideo(params);
            //Android的分享
            if (videosVo != null){
                shareTitle = videosVo.getTitle();
            }else {
                shareTitle = Global.getValue("android_share_title", commomParams.getAppType());

            }
            shareUrl = Global.getValue("android_share_url", commomParams.getAppType());
            shareContent = Global.getValue("android_share_content", commomParams.getAppType());

            StringBuilder stringBuilder = new StringBuilder(shareUrl);
            stringBuilder.append("?videoId=");
            stringBuilder.append(videoId);
            shareUrl = stringBuilder.toString();
        } else {
            //ios的分享
            shareUrl = Global.getValue("ios_share_url", commomParams.getAppType());
            shareTitle = Global.getValue("ios_share_title", commomParams.getAppType());
            shareContent = Global.getValue("ios_share_content", commomParams.getAppType());
        }
        if (videosVo != null){
            logoUrl = videosVo.getBsyImgUrl();
        }else {
            logoUrl = Global.getValue("logo_url", commomParams.getAppType());

        }
        ShareVo shareVo = new ShareVo();
        shareVo.setShareTitle(shareTitle);
        shareVo.setShareContent(shareContent);
        shareVo.setShareUrl(shareUrl);
        shareVo.setLogoUrl(logoUrl);
        return ResultMap.success(shareVo);
    }

    @ApiOperation(value = "获得分享视频列表(分享视频，以及热门前8的视频,公用H5调用)")
    @PostMapping("/gainShareVideos")
    public ResultMap gainShareVideos(@ApiParam("视频id") String videoId) {
        List<Map<String, Object>> list = this.videosService.gainShareVideos(videoId);
        return ResultMap.success(list);
    }

    @ApiOperation(value = "批量删除用户收藏")
    @PostMapping("/batchDelCollections")
    public ResultMap batchDelCollections(@ApiParam("收藏ID 逗号隔开") @RequestParam String collectionIds,
                                         @ApiParam("2.6.0来电我的收藏清空操作 type=1时，批量删除已收藏数据，type=2时清空所有") String type,
                                         @ApiParam("根据用户id来清空操作") String userId) {
        int num = 0;
        //2.6.0以前版本，批量删除操作
        if(StringUtils.isBlank(type) || BULK_DELETE_FAVORITE.equals(type)){
            String[] split = collectionIds.split(",");
             num = videosService.batchDelCollections(split);
        }else {
            //2.6.0以后版本清空操作
             num = videosService.emptyMyCollection(userId);
        }
        if (num > 0) {
            return ResultMap.success();
        }
        return ResultMap.error();
    }

    /**
     * 快捷设置来电秀素材
     *
     * @param commomParams
     * @return
     */
    @ApiOperation(value = "快捷设置来电秀素材（6个）")
    @PostMapping("/quickSetupCallShowVideos")
    public ResultMap quickSetupCallShowVideos(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams) {
        Map<String, Object> params = new HashMap<>();
        params.put("state", Constant.open);//状态 1开启 2关闭
        params.put("approvalState", "2");//默认审批通过状态
        params.put("recommend", Video.RECOMMEND);//只查看推荐的视频 add shixh 20190911
        params.put("quickSetting", "1");//快捷设置：1是 0否
        params.put("videoType", "10");//按权重倒序
        params.put("appType", commomParams.getAppType());
        params.put("tempUserId", commomParams.getUserId());//用户ID
        return ResultMap.success(videosService.quickSetupCallShowVideos(params, 6));
    }

    @ApiOperation(value = "保存视频设置记录信息")
    @PostMapping("/saveVideoSettingInfo")
    public ResultMap saveVideoSettingInfo(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                          @ApiParam("视频id") String videoId,
                                          @ApiParam("设置类型：1来电秀，2锁屏，3壁纸，4微信/QQ皮肤") String setType) {
        if (StringUtils.isBlank(videoId) || StringUtils.isBlank(setType)) {
            return ResultMap.error(400, "保存视频设置记录失败：视频Id或者设置类型为空！");
        }
        if (StringUtils.isBlank(commomParams.getUserId()) || "0".equals(commomParams.getUserId())) {
            return ResultMap.error(400, "保存视频设置记录失败：用户Id不能为空！");
        }
        videoSettingService.saveVideoSettingInfo(commomParams, videoId, setType);
        return ResultMap.success();
    }

    @ApiOperation(value = "查询视频设置记录信息")
    @GetMapping("/findVideoSettingInfo")
    public ResultMap findVideoSettingInfo(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                          @ApiParam("设置类型：1来电秀，2锁屏，3壁纸，4微信/QQ皮肤") String setType) {
        if (StringUtils.isBlank(setType)) {
            return ResultMap.error(400, "查询视频设置记录失败：设置类型不能为空！");
        }
        if (StringUtils.isBlank(commomParams.getUserId()) || "0".equals(commomParams.getUserId())) {
            return ResultMap.error(400, "查询视频设置记录失败：用户Id不能为空！");
        }
        List<Video> list = videoSettingService.findVideoSettingInfo(commomParams, setType);
        Map<String, Object> result = new HashMap<>();
        result.put("data", list);
        return ResultMap.success(result);
    }

    @ApiOperation(value = "联系人保存视频设置记录信息")
    @PostMapping("/saveVideoSettingPhone")
    public ResultMap saveVideoSettingPhone(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                           @ApiParam("视频id") String videoId,
                                           @ApiParam("手机号") String phone) {
        if (StringUtils.isBlank(videoId) || StringUtils.isBlank(phone)) {
            return ResultMap.error("保存视频设置记录失败：视频Id或者设置类型为空！");
        }
        if (StringUtils.isBlank(commomParams.getUserId()) || "0".equals(commomParams.getUserId())) {
            return ResultMap.error("保存视频设置记录失败：用户Id不能为空！");
        }
        return videoSettingService.saveVideoSettingPhone(commomParams, videoId, phone);
    }

    @ApiOperation(value = "查询视频设置联系人记录信息")
    @GetMapping("/findVideoSettingPhone")
    public ResultMap findVideoSettingPhone(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                           @ApiParam("手机号") String phone) {
        if (StringUtils.isBlank(phone)) {
            return ResultMap.error("查询视频设置记录信息失败：手机号不能为空！");
        }
        if (StringUtils.isBlank(commomParams.getUserId()) || "0".equals(commomParams.getUserId())) {
            return ResultMap.error("查询视频设置记录信息失败：用户Id不能为空！");
        }
        List<Video> list = videoSettingService.findVideoSettingPhone(commomParams, phone);
        Map<String, Object> result = new HashMap<>();
        result.put("data", list);
        return ResultMap.success(result);
    }

    @ApiOperation(value = "视频设置记录信息删除")
    @PostMapping("/delVideoSettinginfo")
    public ResultMap delVideoSettinginfo(@ApiParam("用户Id") String userId,
                                         @ApiParam("视频Id") String videoIds,
                                         @ApiParam("手机号") String phone,
                                         @ApiParam("设置类型：1来电秀，2锁屏，3壁纸，4微信/QQ皮肤, 5专属来电秀") String setType) {
        if (StringUtils.isBlank(videoIds) || StringUtils.isBlank(setType)) {
            return ResultMap.error("删除视频设置记录失败：视频Id或者设置类型为空！");
        }
        if (StringUtils.isBlank(userId) || "0".equals(userId)) {
            return ResultMap.error("删除视频设置记录失败：用户Id不能为空！");
        }
        if ("5".equals(setType) && StringUtils.isBlank(phone)) {
            return ResultMap.error("删除视频设置记录失败：手机号不能为空！");
        }
        int result = videoSettingService.delVideoSettinginfo(userId, videoIds, phone, setType);
        return ResultMap.success(result);
    }

    @ApiOperation(value = "首页信息接口")
    @PostMapping("/firstPageParamInfo")
    public ResultMap firstPageParamInfo() {
        FirstPageParamVo firstPageParamVo = new FirstPageParamVo();
        firstPageParamVo.setNewVideosTime(videosService.queryNewVideosTime());
        return ResultMap.success(firstPageParamVo);
    }

    @ApiOperation(value = "获取视频曝光数（PHP调用）")
    @GetMapping("/getVideoExposureCountInfo")
    public Map<String, List<VideoExposureVo>> getVideoExposureCountInfo(@ApiParam("曝光日期(yyyyMMdd)，以逗号隔开，空则查询全部") String dates) {
        return videosService.getVideoExposureCountInfo(dates);
    }

    @ApiOperation(value = "热门搜索标签")
    @PostMapping("/topSearchLabel")
    public ResultMap<List<VideoLabelVo>> topSearchLabel(){
        List<VideoLabelVo> list = videosService.topSearchLabel();
        return ResultMap.success(list);
    }

    @RequestCache
    @ApiOperation(value = "根据视频标签查询视频列表")
    @PostMapping("/labelVideosList")
    public ResultMap labelVideosList(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                @ApiParam("标签名称(支持模糊查询)") @RequestParam String likeLabelName) {
        Map<String, Object> result = new HashMap<>();
        Page<Video> page = videosService.findLabelVideosList(likeLabelName, commomParams);
        result.put("page", new RdPage(page));
        result.put("data", page);
        return ResultMap.success(result);
    }
}
