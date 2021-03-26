
package com.miguan.ballvideo.controller;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.util.DateUtil;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.common.util.VersionUtil;
import com.miguan.ballvideo.entity.AdvertErrorCountLog;
import com.miguan.ballvideo.entity.AdvertErrorLog;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.vo.AdvertErrorCountLogVo;
import com.miguan.ballvideo.vo.AdvertErrorLogVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tool.util.StringUtil;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@Api(value = "广告错误日志保存",tags={"广告错误日志保存"})
@RequestMapping("/api")
public class AdvertErrorLogController {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/saveAdvertisementError")
    @ApiOperation(value = "广告错误日志保存")
    public ResultMap saveAdvertisementError(AdvertErrorLogVo advertErrorLogVo){
        if(advertErrorLogVo!=null && StringUtils.isBlank(advertErrorLogVo.getAdError())){
            return ResultMap.error("AdError为空");
        }

        /* 旧的写法限制了 IOS 的 appPackage 名称，新增常量 List，判断包名称的时候，取 List 来判断 */
        //if(Constant.IOSPACKAGE.equals(advertErrorLogVo.getAppPackage()) && !VersionUtil.compareIsHigh(advertErrorLogVo.getAppVersion(),"2.5.9")){
        if (Constant.IOSPACKAGELIST.contains(advertErrorLogVo.getAppPackage()) && !VersionUtil.compareIsHigh(advertErrorLogVo.getAppVersion(),Constant.APPVERSION_259)){
            return ResultMap.error("IOS日志不做处理");
        }
        AdvertErrorLog adError = new AdvertErrorLog();
        BeanUtils.copyProperties(advertErrorLogVo,adError);
        //传入创建时间
        adError.setCreatTime(new Date());
        if(StringUtils.isNotEmpty(advertErrorLogVo.getAppTime()) && StringUtils.isNumeric(advertErrorLogVo.getAppTime())){
            adError.setAppTime(Long.parseLong(advertErrorLogVo.getAppTime()));
        }
        if(adError.getAppTime()==null){
            adError.setAppTime(adError.getCreatTime().getTime());
        }
        //转换成json
        String dataStr = JSON.toJSONString(adError);
        if(StringUtil.isNotBlank(adError.getAdError())){
            rabbitTemplate.convertAndSend(RabbitMQConstant.AD_ERROR_EXCHANGE, RabbitMQConstant.AD_ERROR_KEY, dataStr);
        }
        return ResultMap.success();
    }

    @PostMapping("/advError/batchSave")
    @ApiOperation(value = "批量保存错误日志统计")
    public ResultMap batchSave(@RequestBody String jsonList){
        if(StringUtils.isBlank(jsonList)){
            return ResultMap.error("数据异常");
        }
        List<AdvertErrorCountLogVo> datas = JSON.parseArray(jsonList,AdvertErrorCountLogVo.class);
        List<AdvertErrorCountLog> sendDatas = Lists.newArrayList();
        for(AdvertErrorCountLogVo vo:datas){
            if(StringUtils.isBlank(vo.getAppPackage())
                    ||StringUtils.isBlank(vo.getAppVersion())
                    ||StringUtils.isBlank(vo.getAdId())
                    ||StringUtils.isBlank(vo.getDeviceId())){
                continue;
            }
            AdvertErrorCountLog advertErrorCountLog = new AdvertErrorCountLog();
            advertErrorCountLog.setCreatTime(DateUtil.format(new SimpleDateFormat("yyyy-MM-dd"),new Date()));
            BeanUtils.copyProperties(vo,advertErrorCountLog);
            if(advertErrorCountLog.getAppTime()==null){
                advertErrorCountLog.setAppTime(System.currentTimeMillis()/1000);
            }
            sendDatas.add(advertErrorCountLog);
        }
        String dataStr = JSON.toJSONString(sendDatas);
        rabbitTemplate.convertAndSend(RabbitMQConstant.AD_ERROR_COUNT_EXCHANGE, RabbitMQConstant.AD_ERROR_COUNT_KEY, dataStr);
        return ResultMap.success();
    }
}
