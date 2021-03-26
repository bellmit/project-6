package com.xiyou.speedvideo.controller;

import com.alibaba.fastjson.JSON;
import com.xiyou.speedvideo.common.Result;
import com.xiyou.speedvideo.dto.BaiduLabelParams;
import com.xiyou.speedvideo.dto.VideoLabelDto;
import com.xiyou.speedvideo.entity.FirstVideos;
import com.xiyou.speedvideo.entity.FirstVideosMca;
import com.xiyou.speedvideo.entity.FirstVideosMcaResult;
import com.xiyou.speedvideo.service.FirstVideoService;
import com.xiyou.speedvideo.util.CommonUtil;
import com.xiyou.speedvideo.util.MCAUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * description:
 *
 * @author huangjx
 * @date 2020/10/14 3:27 下午
 */
@Api(value = "百度AI解析视频标签接口", tags = {"百度AI解析视频标签接口"})
@RestController
@Slf4j
public class VideoController {

    /**
     * 线程池
     */
    private static ExecutorService executor = new ThreadPoolExecutor(14, 20,
            60L, TimeUnit.SECONDS, new LinkedBlockingDeque<>());

    @Resource
    private FirstVideoService firstVideoService;

    @ApiOperation(value = "获取百度云AI标签解析结果,如果视频没解析则开启解析任务")
    @PostMapping("/api/getBaiduLabelResult")
    public List<VideoLabelDto> getBaiduLabelResult(@RequestBody List<BaiduLabelParams> blParams) {
        return firstVideoService.getBaiduLabelResult(blParams);
    }

    @ApiOperation(value = "获取算法AI标签解析结果")
    @PostMapping("/api/getAlgorithmLabelResult")
    public List<VideoLabelDto> getAlgorithmLabelResult(@RequestBody @ApiParam("视频Id数组") List<Integer> videoIds) {
        if(videoIds == null) {
            return null;
        }
        return firstVideoService.getAlgorithmLabelResult(videoIds);
    }

    @ApiOperation(value = "上报历史百度云解析的视频标签")
    @PostMapping("/api/upLoadHistoryVideoLabel")
    public void upLoadHistoryVideoLabel(Integer startRow) {
        log.info("上报历史百度云解析的视频标签(开始)");
        firstVideoService.upLoadHistoryVideoLabel(startRow);
        log.info("上报历史百度云解析的视频标签(结束)");
    }

    @ApiOperation(value = "上报历史算法解析的视频标签")
    @PostMapping("/api/uploadHistoryAlgorithmLabel")
    public void uploadHistoryAlgorithmLabel(Integer startRow) {
        log.info("上报历史算法解析的视频标签(开始)");
        firstVideoService.uploadHistoryAlgorithmLabel(startRow);
        log.info("上报历史算法解析的视频标签(结束)");
    }

    /**
     * 一条龙服务
     * @param path
     * @param videoIds
     * @param limit
     * @return
     */
    @PostMapping("/allInOne")
    @ResponseBody
    public String allInOne(@RequestParam(value = "path", defaultValue = "/mca/videos",required = false) String path,
                           @RequestParam(value = "videoIds",required = false) String videoIds,
                           @RequestParam(value = "limit",required = false) Integer limit,
                           @RequestParam(value = "watchCount",required = false) Integer watchCount,
                           @RequestParam(value = "minute",required = false) Integer minute,
                           @RequestParam(value = "secondStart",required = false) Integer secondStart,
                           @RequestParam(value = "secondEnd",required = false) Integer secondEnd){
        //使用百度解析视频标签
        String result = firstVideoService.allInOneMethod(path, videoIds, limit, watchCount, minute, secondStart, secondEnd);
        if(result.indexOf("error") == -1) {
            return result;
        } else {
            return result.replace("error", "");
        }
    }




    /**
     * 下载视频
     * @param path
     * @return
     */
    @PostMapping("/downloadVideo")
    @ResponseBody
    public List<FirstVideos> downloadVideo(@RequestParam(value = "path", defaultValue = "/mca/videos",required = false) String path,
                                @RequestParam(value = "videoIds",required = false) String videoIds,
                                @RequestParam(value = "limit",required = false) Integer limit,
                                @RequestParam(value = "watchCount",required = false) Integer watchCount,
                                @RequestParam(value = "minute",required = false) Integer minute) {
        // 获取下载列表
        Map paramMap = new HashedMap();
        paramMap.put("videoIds",CommonUtil.string2List(videoIds,","));
        paramMap.put("excludeList",firstVideoService.getMCAExistList());
        paramMap.put("limit",limit);
        paramMap.put("watchCount",watchCount);
        paramMap.put("minute",minute);
        List<FirstVideos> resultList = firstVideoService.getDownloadList(paramMap);
        // 判断路径没有则创建
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        //信息插入待处理表
        if(firstVideoService.insertDownloading(resultList)){
            // 下载并更新状态
            for (FirstVideos videos : resultList) {
                executor.execute(()->firstVideoService.downloadAndUpdate(videos,path));
            }
        }
        return resultList;
    }

    /**
     * 对视频执行倍速操作
     * @return
     */
    @PostMapping("/doVideoSpeed")
    @ResponseBody
    public void doVideoSpeed() {
        // 找出下载完成的数据
        List<FirstVideosMca> downloadCompleteList = firstVideoService.getDownloadCompleteList();
        for(FirstVideosMca mca : downloadCompleteList){
            List<String> cmdList = new ArrayList<>();
            String cmd = CommonUtil.getCmdByRule(mca);
            if(cmd!=null){
                cmdList.add(cmd);
            }
            mca.setState(FirstVideosMca.STATE_SPEED_COMPLETE);
            //执行ffmpeg
            executor.execute(()->firstVideoService.speedAndUpdate(cmdList,mca));
        }
    }


    /**
     * 对视频进行上传
     * @return
     */
    @PostMapping("/doVideoUpload")
    @ResponseBody
    public List<FirstVideosMcaResult> doVideoUpload() {
        // 查找出待上传数据
        List<FirstVideosMcaResult> uploadList = firstVideoService.getWaitUploadList();
        for(FirstVideosMcaResult result:uploadList){
            //更新result表
            executor.execute(()->firstVideoService.doUploadAndUpdateResult(result));
        }
        return uploadList;
    }

    /**
     * 发起MCA请求接口
     * @return
     */
    @PostMapping("/applyMCA")
    @ResponseBody
    public List<FirstVideosMcaResult> applyMCA(){
        // 查找出待解析数据
        List<FirstVideosMcaResult> uploadList = firstVideoService.getWaitApplyList();
        for(FirstVideosMcaResult result:uploadList){
            executor.execute(()->firstVideoService.doApplyAction(result));
        }
        return uploadList;
    }

    /**
     * MCA回调接口
     * @return
     */
    @RequestMapping("/mcaCallBack")
    @ResponseBody
    public void MCACallBack(@RequestBody String param){
        log.info("接收百度回调："+param);
        firstVideoService.doResultSave(param);
    }

    /**
     * 查看回调结果
     * @return
     */
    @RequestMapping("/mcaResult")
    @ResponseBody
    public List<FirstVideosMcaResult> mcaResult(@RequestParam(value = "videoIds",required = false) String videoIds,
                            @RequestParam(value = "speed",required = false) Integer speed,
                            @RequestParam(value = "catId",required = false) Integer catId){
        // 获取符合条件的数据
        Map paramMap = new HashedMap();
        paramMap.put("videoIds",CommonUtil.string2List(videoIds,","));
        paramMap.put("speed",speed);
        paramMap.put("catId",catId);
        List<FirstVideosMcaResult> results = firstVideoService.getMCAResult(paramMap);
        for(FirstVideosMcaResult result:results){
            //输出基本信息
            System.out.println("------------------【id:"+result.getVideoId()+"】【speed:"+result.getSpeed()+"】【bsyURL:"+result.getBsyUrl()+"】---------------------------------");
            MCAUtil.parsingResult(result.getResult());
        }
        return results;
    }

    @PostMapping("/test")
    @ResponseBody
    public Object test(){
        return Result.suc("22222");
    }

}
