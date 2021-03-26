package com.miguan.flow.task;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.miguan.flow.common.constant.RedisConstant;
import com.miguan.flow.common.util.ResultMap;
import com.miguan.flow.mapper.FirstVideosMapper;
import com.miguan.flow.service.common.RedisAdvertService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 激励视频出入库任务
 */
@Slf4j
@Component
public class InceVideoStorageTask {

    @Resource
    private FirstVideosMapper firstVideosMapper;

    @Resource
    private RedisAdvertService redisAdvertService;
    @Resource
    private RestTemplate restTemplate;

    @Value("${bigdata-server.strategy.inIncentiveVideo}")
    private String inIncentiveVideo;

    @Value("${bigdata-server.strategy.outIncentiveVideo}")
    private String outIncentiveVideo;

    @Value("${xy-server.esFirstVideo.addOrDelete}")
    private String addOrDeleteUrl;

    /**
     * 激励视频入库
     */
    @Scheduled(cron = "${task.scheduled.cron.inceVideo.storage}")
    public  void storageTask(){
        storage();//入库
        delivery();//出库
    }

    /**
     * 激励视频入库
     */
    public void storage() {
        log.info("激励视频开始入库（start）");
        int incentiveVideoDayIncr =  redisAdvertService.getInt(RedisConstant.INCENTIVE_VIDEO_DAY_INCR); //激励视频入库视频数量
        log.info("激励视频每日入库数量配置:{}",incentiveVideoDayIncr);
        //请求大数据,获取满足条件的激励视频
        String incentiveVideoStr =  dbInVideo(incentiveVideoDayIncr);
        log.info("大数据获取的入库视频信息:{}",incentiveVideoStr);
        //将视频改为激励视频
        updateVideoState(incentiveVideoStr,1);
        log.info("激励视频结束入库（end）");
    }

    /**
     * 激励视频出库
     */
    public void delivery() {
        log.info("激励视频开始出库（start）");
        int inNum =  redisAdvertService.getInt(RedisConstant.INCENTIVE_VIDEO_DAY_INCR); //激励视频入库视频数量
        int retainNum =  redisAdvertService.getInt(RedisConstant.INCENTIVE_VIDEO_DELIVERY_NUM); //激励视频最多能存储的数量
        log.info("激励视频出库数量配置:{}",retainNum);
        //激励视频数量
        int incentivceCode = firstVideosMapper.countIncentiveVideo();
        log.info("已有的激励视频数量:{}",incentivceCode);
        if(incentivceCode > retainNum){
            //已存在的数量超过了阈值,则出库
            String incentiveVideoStr =  dbOutVideo(inNum,retainNum);
            log.info("大数据获取的出库视频信息:{}",incentiveVideoStr);
            //将激励视频出库
            updateVideoState(incentiveVideoStr,0);
        }
        log.info("激励视频结束出库（end）");
    }

    private void updateVideoState(String incentiveVideoStr, int isIncentive) {
        if(StringUtils.isEmpty(incentiveVideoStr)){
            return ;
        }
        Map<String,Object> param = Maps.newHashMap();
        param.put("isIncentive",isIncentive);
        param.put("idList",Lists.newArrayList(incentiveVideoStr.split(",")));
        firstVideosMapper.updateBatchIncentive(param);
        //更新ES
        changeToEs(incentiveVideoStr);
    }

    private String dbInVideo(int configValue) {
        try {
            String url = inIncentiveVideo + "?configValue=" + configValue;
            String result = restTemplate.getForObject(url, String.class);//入库
            ResultMap resultMap = JSONObject.parseObject(result, ResultMap.class);
            log.info("入库:大数据端获得的结果集[{}]",result);
            if(resultMap == null || resultMap.getCode() != 200 || resultMap.getData() == null){
                return null;
            } else {
                return resultMap.getData().toString();
            }

        } catch (Exception e){
            log.info("激励视频标准出入库,大数据端获取视频失败。失败原因{}",e.getMessage());
            return null;
        }
    }

    private String dbOutVideo(int inNum, int retainNum) {
        try {
            String url = outIncentiveVideo + "?inNum=" + inNum + "&retainNum=" + retainNum;
            String result = restTemplate.getForObject(url, String.class);
            ResultMap resultMap = JSONObject.parseObject(result, ResultMap.class);
            log.info("出库:大数据端获得的结果集[{}]",result);
            if(resultMap == null || resultMap.getCode() != 200 || resultMap.getData() == null){
                return null;
            } else {
                return resultMap.getData().toString();
            }
        } catch (Exception e){
            log.info("激励视频标准出库,大数据端获取视频失败。失败原因{}",e.getMessage());
            return null;
        }
    }

    private void changeToEs(String incentiveVideoStr) {
        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap(16);
        body.add("options","videoAdd");
        body.add("videoIds",incentiveVideoStr);
        try {
            restTemplate.postForObject(addOrDeleteUrl, body, String.class);
        } catch (Exception e){
            log.info("调用西柚更新激励视频失败。失败原因{}",e.getMessage());
        }
    }
}
