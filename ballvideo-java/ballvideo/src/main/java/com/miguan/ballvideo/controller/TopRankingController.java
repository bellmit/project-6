package com.miguan.ballvideo.controller;

import com.cgcg.base.core.exception.CommonException;
import com.miguan.ballvideo.common.aop.CommonParams;
import com.miguan.ballvideo.common.constants.VideoContant;
import com.miguan.ballvideo.common.interceptor.argument.params.CommonParamsVo;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.service.TopRankingService;
import com.miguan.ballvideo.vo.HotListConfVo;
import com.miguan.ballvideo.vo.video.HotListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 热门榜单Controller
 *
 * @Author xy.chen
 * @Date 2020/8/25
 **/
@Slf4j
@Api(value="热门榜单接口",tags={"热门榜单接口"})
@RestController
@RequestMapping("/api/top/ranking")
public class TopRankingController {

    @Resource
    private TopRankingService topRankingService;

    @ApiOperation("刷新热榜缓存接口（PHP调用）")
    @PostMapping("/setHotListToRedis")
    public ResultMap setHotListToRedis(@ApiParam("热榜类型 1实时榜 2日榜") Integer type){
        if(type == null || (!type.equals(VideoContant.HOUR_HOT_LIST_CODE) && !type.equals(VideoContant.DAY_HOT_LIST_CODE))){
            throw new CommonException("参数type异常");
        }
        try {
            topRankingService.setHotListToRedis(type);
            return ResultMap.success("热榜缓存刷新成功");
        }catch (Exception e){
            log.error(e.getMessage());
            return ResultMap.error("热榜缓存刷新失败");
        }
    }

    @ApiOperation("获取榜单banner配置")
    @PostMapping("/getHotListConf")
    public ResultMap<HotListConfVo> getHotListConf(){
        HotListConfVo hotListConfVo = topRankingService.getHotListConf();
        return ResultMap.success(hotListConfVo);
    }

    @ApiOperation("查询实时热榜前三条数据")
    @PostMapping("/queryTopThreeHotList")
    public ResultMap<List<HotListVo>> queryTopThreeHotList(@CommonParams CommonParamsVo params){
        try {
            List<HotListVo> HotListVo = topRankingService.queryTopThreeHotList(params);
            return ResultMap.success(HotListVo);
        }catch (Exception e){
            log.error(e.getMessage());
            return ResultMap.error();
        }
    }

    @ApiOperation("根据热榜类型查询对应热榜数据")
    @PostMapping("/queryHotListByType")
    public ResultMap<List<HotListVo>> queryHotListByType(@CommonParams CommonParamsVo params,
                                        @ApiParam("热榜类型 1 实时热播榜 2 24小时热播榜") Integer type){
        try {
            List<HotListVo> list = topRankingService.queryHotListByType(params,type);
            return ResultMap.success(list);
        }catch (Exception e){
            log.error(e.getMessage());
            return ResultMap.error();
        }
    }
}
