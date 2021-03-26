package com.miguan.laidian.controller;

import com.amazonaws.util.Base64;
import com.miguan.laidian.common.annotation.CommonParams;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.rabbitMQ.util.RabbitMQConstant;
import com.miguan.laidian.service.UserContactService;
import com.miguan.laidian.vo.UserContactVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户通讯录
 */
@Slf4j
@Api(value = "用户通讯录controller", tags = {"用户通讯录接口"})
@RestController
@RequestMapping("/api/UserContact")
public class UserContactController {

    @Autowired
    UserContactService userContactService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @ApiOperation("把通讯录信息放到消息队列中")
    @PostMapping("/sendToMQ/TOPIC_userContact_MQ")
    public ResultMap sendToMQ(String jsonMsg){
        //把加密数据解密后放到消息队列中
        rabbitTemplate.convertAndSend(RabbitMQConstant.USERCONTACT_EXCHANGE, RabbitMQConstant.USERCONTACT_RUTE_KEY , new String(Base64.decode(jsonMsg)));
        return ResultMap.success();
    }

    @ApiOperation("通过设备ID判断该设备是否已经保存过通讯记录")
    @PostMapping("/isAuthorized")
    public ResultMap isAuthorized(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams){
        Map<String, Object> params = new HashMap<>();
        params.put("deviceId", commomParams.getDeviceId());
        List<UserContactVo> userContactList = userContactService.findUserContactList(params);
        String isAuthorized = "0";
        if (userContactList!=null&&userContactList.size()>0){
            isAuthorized = "1";
        }
        return ResultMap.success(isAuthorized);
    }

}
