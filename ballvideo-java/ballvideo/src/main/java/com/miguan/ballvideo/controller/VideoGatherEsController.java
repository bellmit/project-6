package com.miguan.ballvideo.controller;

import cn.jiguang.common.utils.StringUtils;
import com.miguan.ballvideo.common.aop.AbTestAdvParams;
import com.miguan.ballvideo.common.aop.CommonParams;
import com.miguan.ballvideo.common.aop.RequestCache;
import com.miguan.ballvideo.common.enums.VideoESOptions;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.common.interceptor.argument.params.CommonParamsVo;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.dto.VideoGatherParamsDto;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.VideoGatherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(value="视频合集相关接口",tags={"视频合集相关接口"})
@RequestMapping("/api/es/videoGather")
@RestController
public class VideoGatherEsController {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private VideoGatherService videoGatherService;

    @RequestCache
    @ApiOperation(value = "视频合集页面查询（5分钟缓存）")
    @GetMapping("/getVideos")
    public ResultMap getVideos(@ApiParam("合集ID") Long gatherId,@ApiParam("公共参数")@CommonParams CommonParamsVo params){
        Object o = videoGatherService.getVideos(gatherId,params);
        return ResultMap.success(o);
    }

    //@UserOperate(name= OperateConstant.gatherSearch,recordParams = true)
    @ApiOperation(value = "首页视频合集查询")
    @GetMapping("/getVideosByHomePage")
    public ResultMap getVideosByHomePage(@ApiParam("设备ID") String deviceId,@ApiParam("合集ID") Long gatherId,@ApiParam("最后/最前一个视频ID的权重")  Long totalWeight,@ApiParam("最后/最前一个视频ID(V2.5新增)")  Long videoId,@ApiParam("往左left/往右right") String step){
        Object o = videoGatherService.getVideos(deviceId,gatherId,totalWeight,videoId,step);
        return ResultMap.success(o);
    }

    @RequestCache
    @ApiOperation(value = "搜索页面默认合集查询（5分钟缓存）")
    @GetMapping("/getDefaultVideos")
    public ResultMap getDefaultVideos(@ApiParam("关键字")String userId,
                                      @ApiParam("公共参数")VideoGatherParamsDto params,
                                      @AbTestAdvParams AbTestAdvParamsVo queueVo){
        Object o = videoGatherService.getDefaultVideos(queueVo,userId,params);
        return ResultMap.success(o);
    }

    @ApiOperation("合集批量绑定/解绑视频（PHP调用）")
    @PostMapping("/updateVideoByGatherId")
    public ResultMap updateVideoByGatherId(@ApiParam("视频ID，多个逗号隔开")String videoIds,@ApiParam("合集ID，传0表示解绑") Long gatherId) {
        if(StringUtils.isEmpty(videoIds) || gatherId==null){
            return ResultMap.error("参数异常");
        }
        String json = VideoESOptions.gatherAddOrDelete.name()+ RabbitMQConstant._MQ_+gatherId+RabbitMQConstant._MQ_+videoIds;
        rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEOS_ES_SEARCH_EXCHANGE,RabbitMQConstant.VIDEOS_ES_SEARCH_KEY,json);
        return ResultMap.success();
    }

    @ApiOperation("删除或者关闭视频合集（PHP调用）")
    @PostMapping("/deleteOrCloseGather")
    public ResultMap deleteOrCloseGather(@ApiParam("合集ID")Long gatherId,@ApiParam("0-关闭,1-删除") int state) {
        if(gatherId==null || gatherId<0){
            return ResultMap.error("参数异常");
        }
        String json = VideoESOptions.gatherDeleteOrClose.name()+RabbitMQConstant._MQ_+gatherId+RabbitMQConstant._MQ_+state;
        rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEOS_ES_SEARCH_EXCHANGE,RabbitMQConstant.VIDEOS_ES_SEARCH_KEY,json);
        return ResultMap.success();
    }

    @RequestCache
    @ApiOperation("查询当前合集下视屏数据（5分钟缓存）")
    @GetMapping("/getVideosByGatgherId")
    public ResultMap getVideosByGatgherId(@ApiParam("合集ID")Long gatherId) {
        if(gatherId==null || gatherId<0){
            return ResultMap.error("参数异常");
        }
        return ResultMap.success(videoGatherService.getVideos(gatherId));
    }

    @ApiOperation("实时更新集合下视屏数据")
    @GetMapping("/refreshVideosByGatherId")
    public ResultMap refreshVideosByGatherId(@ApiParam("合集ID")Long gatherId) {
        if(gatherId==null || gatherId<0){
            return ResultMap.error("参数异常");
        }
        return ResultMap.success(videoGatherService.refreshVideosByGatherId(gatherId));
    }

}
