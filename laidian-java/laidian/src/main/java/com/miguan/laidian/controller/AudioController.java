package com.miguan.laidian.controller;

import com.github.pagehelper.Page;
import com.miguan.laidian.common.annotation.CommonParams;
import com.miguan.laidian.common.aop.RequestCache;
import com.miguan.laidian.common.constants.AudioConstant;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.RdPage;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.entity.Video;
import com.miguan.laidian.entity.VideosCat;
import com.miguan.laidian.service.AudioService;
import com.miguan.laidian.vo.AudioCatVo;
import com.miguan.laidian.vo.AudioVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "音频controller", tags = {"音频接口"})
@RestController
@RequestMapping("/api/audio")
public class AudioController {

    @Resource
    private AudioService audioService;

//    @RequestCache
    @ApiOperation(value = "音频分类")
    @PostMapping("/catList")
    public ResultMap audiosCatList() {
        List<AudioCatVo> audioCatList = audioService.findAudioCatList();
        return ResultMap.success(audioCatList);
    }

    @ApiOperation(value = "音频列表")
    @PostMapping("/audiosList")
    public ResultMap audiosList(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                @ApiParam("音频分类ID,热门和最新不用传") String catId,
                                @ApiParam("类型：10--热门 20--最新 30--其他分类") String audioType) {
        Map<String, Object> params = new HashMap<>();
        if (AudioConstant.AUDIO_OTHER.equals(audioType) && StringUtils.isNotBlank(catId)) {
            params.put("catId", Integer.parseInt(catId));
        }
        params.put("audioType", audioType);//热门：按权重值降序排列,最新和其余标签：按后台采集创建时间降序排列
        Page<AudioVo> audioList = audioService.findAudioList(params, commomParams.getCurrentPage(), commomParams.getPageSize());
        Map<String, Object> result = new HashMap<>();
        result.put("page", new RdPage(audioList));
        result.put("data", audioList);
        return ResultMap.success(result);
    }

    @ApiOperation(value = "歌手名或者铃声名模糊查询列表")
    @PostMapping("/likeList")
    public ResultMap likeList(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                              @ApiParam("歌手名或者铃声名模糊查询参数,默认不传") String likeParam) {
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(likeParam)){
            params.put("likeParam", likeParam);
        }
        Page<AudioVo> audioList = audioService.findAudioList(params, commomParams.getCurrentPage(), commomParams.getPageSize());
        Map<String, Object> result = new HashMap<>();
        result.put("page", new RdPage(audioList));
        result.put("data", audioList);
        return ResultMap.success(result);
    }

    @ApiOperation(value = "更新用户收藏数、分享数、下载数、试听数")
    @PostMapping("/updateCount")
    public ResultMap updateCount(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                 @ApiParam("音频ID") @RequestParam("id") String id,
                                 @ApiParam("操作类型：10--收藏 20--分享 30--取消收藏 40--试听数 50--下载数") @RequestParam(value = "opType") String opType) {
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(id)) {
            params.put("audioId", Integer.parseInt(id));
        }
        params.put("deviceId", commomParams.getDeviceId());
        params.put("opType", opType);
        //发送消息队列
        audioService.updateCountSendMQ(params);
        return ResultMap.success();
    }

    @ApiOperation(value = "音频详情（H5铃声分享调用）")
    @PostMapping("/audiosDetail")
    public ResultMap audiosList(@ApiParam("音频ID") Long audioId) {
        AudioVo audioDetail = audioService.findAudioDetail(audioId);
        return ResultMap.success(audioDetail);
    }
}
