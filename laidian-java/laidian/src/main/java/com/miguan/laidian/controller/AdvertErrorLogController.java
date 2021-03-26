package com.miguan.laidian.controller;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.miguan.laidian.common.util.DateUtil;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.entity.AdvertErrorCountLog;
import com.miguan.laidian.entity.AdvertErrorLog;
import com.miguan.laidian.rabbitMQ.util.RabbitMQConstant;
import com.miguan.laidian.service.AdErrorService;
import com.miguan.laidian.vo.AdvertErrorCountLogVo;
import com.miguan.laidian.vo.AdvertErrorLogVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tool.util.StringUtil;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@Api(value = "广告错误日志", tags = {"广告错误日志"})
@RequestMapping("/api/adError/")
public class AdvertErrorLogController {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private AdErrorService adErrorService;

    @PostMapping("/save")
    @ApiOperation(value = "广告错误日志保存")
    public ResultMap saveAdvertisementError(AdvertErrorLogVo advertErrorLogVo) {
        if (advertErrorLogVo != null && StringUtils.isBlank(advertErrorLogVo.getAdError())) {
            return ResultMap.error("AdError为空");
        }
        AdvertErrorLog adError = new AdvertErrorLog();
        BeanUtils.copyProperties(advertErrorLogVo, adError);
        adError.setCreatTime(new Date());
        if(StringUtils.isNotEmpty(advertErrorLogVo.getAppTime()) && StringUtils.isNumeric(advertErrorLogVo.getAppTime())){
            adError.setAppTime(Long.parseLong(advertErrorLogVo.getAppTime()));
        }
        if(adError.getAppTime()==null){
            adError.setAppTime(adError.getCreatTime().getTime());
        }
        String dataStr = JSON.toJSONString(adError);
        if (StringUtil.isNotBlank(adError.getAdError())) {
            rabbitTemplate.convertAndSend(RabbitMQConstant.AD_ERROR_EXCHANGE, RabbitMQConstant.AD_ERROR_KEY, dataStr);
        }
        return ResultMap.success();
    }

    @PostMapping("/batchSave")
    @ApiOperation(value = "批量保存错误日志统计")
    public ResultMap batchSave(String jsonList) {
        if (StringUtils.isBlank(jsonList)) {
            return ResultMap.error("数据异常");
        }
        jsonList = "[" + jsonList + "]";
        List<AdvertErrorCountLogVo> datas = JSON.parseArray(jsonList, AdvertErrorCountLogVo.class);
        List<AdvertErrorCountLog> sendDatas = Lists.newArrayList();
        for (AdvertErrorCountLogVo vo : datas) {
            if (StringUtils.isBlank(vo.getAppPackage())
                    || StringUtils.isBlank(vo.getAppVersion())
                    || StringUtils.isBlank(vo.getAdId())
                    || StringUtils.isBlank(vo.getDeviceId())) {
                continue;
            }
            AdvertErrorCountLog advertErrorCountLog = new AdvertErrorCountLog();
            advertErrorCountLog.setCreatTime(DateUtil.format(new SimpleDateFormat("yyyy-MM-dd"), new Date()));
            BeanUtils.copyProperties(vo, advertErrorCountLog);
            if(advertErrorCountLog.getAppTime()==null){
                advertErrorCountLog.setAppTime(System.currentTimeMillis()/1000);
            }
            sendDatas.add(advertErrorCountLog);
        }
        String dataStr = JSON.toJSONString(sendDatas);
        rabbitTemplate.convertAndSend(RabbitMQConstant.AD_ERROR_COUNT_EXCHANGE, RabbitMQConstant.AD_ERROR_COUNT_KEY, dataStr);
        return ResultMap.success();
    }

    @PostMapping("/saveAdvErrorInfo")
    @ApiOperation(value = "手动保存广告错误日志(错误日志堆积时使用)")
    public ResultMap saveAdvErrorInfo(String dateStr) {
        if (StringUtils.isBlank(dateStr)) {
            return ResultMap.error("错误日志堆积日期为空");
        }
        adErrorService.advWrongDatas(dateStr);
        return ResultMap.success();
    }

}
