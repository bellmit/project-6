package com.miguan.ballvideo.controller;

import com.miguan.ballvideo.common.task.AutoPushTask;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.service.AutoPushSynService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 友盟消息推送联调接口
 * <p>
 * 友盟消息推送接口修改（单个推送，固定PHP传入 推送id  进行推送）   2019年9月25日15:46:14     HYL
 *
 * @Author shixh
 * @Date 2019/9/10
 **/
@Slf4j
@Api(value="消息推送接口",tags={"消息推送接口"})
@RequestMapping("/api/autoPush")
@RestController
public class AutoPushController {

    @Resource
    private AutoPushSynService autoPushSynService;
    @Resource
    private AutoPushTask autoPushTask;

    @ApiOperation("重置定时器")
    @GetMapping("/restTask")
    public ResultMap restTask(Long autoPushId) {
        /*try{
            if(autoPushId == null){
               return ResultMap.error("请输入重置的id");
            }
            autoPushTask.resetTaskToMQ(autoPushId);
            return ResultMap.success();
        } catch (Exception e){
            return ResultMap.error(e.getMessage());
        }*/
        return ResultMap.success();
    }


    @ApiOperation("同步distinct_id的数据")
    @GetMapping("/stopTask")
    public ResultMap stopTask(Long autoPushId) {
        /*try{
            if(autoPushId == null){
                return ResultMap.error("传入的id不能为空");
            }
            autoPushTask.stopTaskToMQ(autoPushId);
            return ResultMap.success();
        } catch (Exception e){
            return ResultMap.error(e.getMessage());
        }*/
        return ResultMap.success();
    }

    @ApiOperation("同步distinct_id的数据")
    @GetMapping("/startSyn")
    public ResultMap startSyn() {
        /*String s = autoPushSynService.startSyn();
        return ResultMap.success(s);*/
        return ResultMap.success();
    }


    @ApiOperation("停止同步")
    @GetMapping("/stopSyn")
    public ResultMap stopSyn() {
        /*String s = autoPushSynService.stopSyn();
        return ResultMap.success(s);*/
        return ResultMap.success();
    }



}
