package com.miguan.flow.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miguan.flow.dto.IncentiveParamDto;
import com.miguan.flow.dto.IncentiveVideoDto;
import com.miguan.flow.mapper.IncentiveVideoMapper;
import com.miguan.flow.service.IncentiveVideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 激励视频serviceImpl
 **/
@Slf4j
@Service
public class IncentiveVideoServiceImpl implements IncentiveVideoService {

    @Resource
    private IncentiveVideoMapper incentiveVideoMapper;
    @Resource
    private RestTemplate restTemplate;
    @Value("${new-rec.incentive-video}")
    private String necUrl;

    /**
     * 根据参数从推荐接口中获取激励视频id，并返回激励视频信息
     * @param paramDto
     * @return
     */
    public List<IncentiveVideoDto> findIncentiveVideoList(IncentiveParamDto paramDto) {
        String videoIds = getIncentiveVideoIdFromNec(paramDto);
        Map<String, Object> params = new HashMap<>();
        params.put("videoIds", videoIds);
        params.put("incentiveNum", paramDto.getIncentiveNum());
        return incentiveVideoMapper.findIncentiveVideoList(params);
    }

    /**
     * 调用推荐接口，获取激励视频id
     * @param paramDto
     * @return
     */
    private String getIncentiveVideoIdFromNec(IncentiveParamDto paramDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(JSON.toJSONString(paramDto), headers);
        String videos = null;
        try {
            String result = restTemplate.postForObject(necUrl, request, String.class);
            JSONObject restJson = JSONObject.parseObject(result);
            videos = restJson.getJSONObject("data").getString("incentiveVideo");
            videos = videos.replace("\"","").replace("[","").replace("]","");
            log.info("激励视频视频id：{}", videos);
        } catch (Exception e) {
            log.error("调用推荐接口，获取激励视频id", e.getMessage());
            log.error("激励视频接口异常参数:{}", JSON.toJSONString(paramDto));
        }
        return videos;
    }
}
