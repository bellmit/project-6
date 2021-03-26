package com.miguan.laidian.controller;


import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.service.PushSevice;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 消息推送接口
 *
 * @Author shixh
 * @Date 2019/9/10
 **/
@Slf4j
@Api(value = "推送controller", tags = {"消息推送接口"})
@RequestMapping("/api/uPush")
@RestController
public class UPushController {

    @Resource
    private PushSevice uPushSevice;

    @ApiOperation("立即推送（PHP调用）")
    @PostMapping("/realTimeSendInfo")
    public ResultMap realTimeSendInfo(Long id) {
        //return uPushSevice.realTimeSendInfo(id);
        return ResultMap.success();
    }

    /**
     * 1、能根据推送ID，推送厂商进行单独厂商推送测试；
     * 2、能根据推送token，推送厂商进行制定用户单独推送测试；
     *
     * @param id          推送ID
     * @param tokens      推送tokens(参数有token，根据token来发送；无，则取相对应表字段值) 多个用逗号隔开
     * @param pushChannel 推送厂商 YouMeng,HuaWei,VIVO,OPPO,XiaoMi;
     * @param pushType    推送类型 1：指定用户推送  2：广播推送
     * @return
     */
    @ApiOperation("立即推送测试接口")
    @PostMapping("/realTimePushTest")
    public ResultMap realTimePushTest(Long id, String tokens, String pushChannel, String pushType) {
        /*if (StringUtils.isEmpty(pushType)) {
            pushType = "1";
        }
        return uPushSevice.realTimePushTest(id, tokens, pushChannel, pushType);*/
        return ResultMap.success();
    }

}
