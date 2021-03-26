package com.miguan.laidian.controller;

import com.github.pagehelper.Page;
import com.miguan.laidian.common.annotation.CommonParams;
import com.miguan.laidian.common.aop.RequestCache;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.RdPage;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.service.SmallVideoService;
import com.miguan.laidian.vo.SmallVideoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * iOS视频Controller
 *
 * @Author xy.chen
 * @Date 2019/7/8
 **/
@Slf4j
@Api(value = "小视频controller", tags = {"小视频接口"})
@RestController
@RequestMapping("/api/iOSVideo")
public class SmallVideoController {

    @Resource
    private SmallVideoService smallVideosService;

    /**
     * iOS视频源列表
     *
     * @return
     */
    @RequestCache
    @ApiOperation(value = "iOS视频源列表(5分钟缓存)")
    @PostMapping("/iOSVideosList")
    public ResultMap videosList(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("state", Constant.open);
        params.put("appType", commomParams.getAppType());
        Page<SmallVideoVo> page  = smallVideosService.findIOSVideosList(params, commomParams.getCurrentPage(), commomParams.getPageSize());
        List<SmallVideoVo> list = page.getResult();
        Map<String, Object> result = new HashMap<>();
        result.put("page", new RdPage(page));
        result.put("data", list);
        return ResultMap.success(result);
    }

    /**
     * 小视频列表
     *
     * @return
     */
    @RequestCache
    @ApiOperation(value = "小视频列表(5分钟缓存)")
    @PostMapping("/smallVideosList")
    public ResultMap smallVideosList(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("state", Constant.open);
        if (StringUtils.isNotBlank(commomParams.getUserId())) {
            params.put("userId", Integer.parseInt(commomParams.getUserId()));
        }
        params.put("appType",commomParams.getAppType());
        Page<SmallVideoVo> page  = smallVideosService.findIOSVideosList(params, commomParams.getCurrentPage(), commomParams.getPageSize());
        List<SmallVideoVo> list = page.getResult();
        Map<String, Object> result = new HashMap<>();
        result.put("page", new RdPage(page));
        result.put("data", list);
        return ResultMap.success(result);
    }

    /**
     * 更新举报次数
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "更新举报次数")
    @PostMapping("/updateReportCount")
    public ResultMap updateReportCount(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                       @ApiParam("iOS视频ID") @RequestParam("id") String id) {
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(id)) {
            params.put("id", Integer.parseInt(id));
        }
        params.put("appType", commomParams.getAppType());
        int num = smallVideosService.updateReportCount(params);
        if (num>0) {
            return ResultMap.success();
        } else {
            return ResultMap.error();
        }
    }

    /**
     * 更新用户收藏数、点赞数、观看数、是否感兴趣
     *
     * @param id
     * @param opType
     * @return
     */
    @ApiOperation(value = "更新用户收藏数、点赞数、观看数、是否感兴趣")
    @PostMapping("/updateSmallVideosCount")
    public ResultMap updateCount(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                 @ApiParam("视频ID") @RequestParam("id") String id,
                                 @ApiParam("操作类型：10--收藏 20--点赞 30--观看 40--取消收藏 50--取消点赞 60--不感兴趣") String opType) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(id)) {
            params.put("id", Integer.parseInt(id));
        }
        params.put("opType", opType);
        params.put("userId", commomParams.getUserId());
        params.put("appType", commomParams.getAppType());
        int num = smallVideosService.updateVideosCount(params);
        if(num>0)return ResultMap.success("更新成功");
        return ResultMap.error("更新失败");
    }

    /**
     * 小视频播放详情
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "小视频播放详情")
    @PostMapping("/smallVideosDetail")
    public ResultMap smallVideosDetail(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                       @ApiParam("视频ID") String id) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(id)) {
            params.put("id", Integer.parseInt(id));
        }
        params.put("state", Constant.open);
        if (StringUtils.isNotBlank(commomParams.getUserId())) {
            params.put("userId", Integer.parseInt(commomParams.getUserId()));
        }
        params.put("appType", commomParams.getAppType());
        SmallVideoVo smallVideoVo = smallVideosService.findVideosDetailByOne(params);
        if(smallVideoVo!=null) {
            return ResultMap.success(smallVideoVo);
        } else {
            return ResultMap.error("查询失败：参数错误或者无此视频");
        }
    }
}
