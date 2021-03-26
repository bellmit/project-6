package com.miguan.ballvideo.controller;

import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.service.PushSevice;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("/api/uPush")
@RestController
public class UPushController {

    @Resource
    private PushSevice pushSevice;

    @ApiOperation("立即推送")
    @PostMapping("/realTimeSendInfo")
    public ResultMap realTimeSendInfo(Long id) {
        //return pushSevice.realTimeSendInfo(id,null);
        return ResultMap.success();
    }

    /**
     * 1、能根据推送ID，推送厂商进行单独厂商推送测试；
     * 2、能根据推送token，推送厂商进行制定用户单独推送测试；
     *
     * @param id          推送ID
     * @param tokens      推送tokens(参数有token，根据token来发送；无，则取相对应表字段值) 多个用逗号隔开
     * @param pushChannel 推送厂商 YouMeng,HuaWei,VIVO,OPPO,XiaoMi;
     * @return
     */
    @ApiOperation("立即推送测试接口")
    @PostMapping("/realTimePushTest")
    public ResultMap realTimePushTest(Long id, String tokens, String pushChannel) {
        //return pushSevice.realTimePushTest(id, tokens, pushChannel,null);
        return ResultMap.success();
    }
}
