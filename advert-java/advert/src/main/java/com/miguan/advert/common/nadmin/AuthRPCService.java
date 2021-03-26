package com.miguan.advert.common.nadmin;

import com.miguan.advert.common.util.ResultMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @program: dspPutIn-java
 * @description: 统一登录平台鉴权
 * @author: suhj
 * @create: 2020-09-22 18:07
 **/
@Slf4j
@Service
public class AuthRPCService {

    @Value("${send.ndmin-java.url}")
    private String ndminUrl;

    //调用接口
    private static final String API_AUTH_USER_URL = "/api/auth/user";

    @Resource
    private RestTemplate restTemplate;

    public ResultMap existAdmin(String authorization, String username, String platForm){
        try {
            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            header.add("Authorization", authorization);
            LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap(16);
            body.add("username", username);
            body.add("platForm", platForm);
            HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity(body, header);
            ResponseEntity<ResultMap> res = restTemplate.postForEntity(ndminUrl.concat(API_AUTH_USER_URL), httpEntity, ResultMap.class);
            if (res.getStatusCode().is2xxSuccessful()) {
                ResultMap resD = res.getBody();
                return resD;
            }
        } catch (HttpClientErrorException e) {
            log.error("DSP调用统一登录系统接口失败，客户端调用异常：{}", e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            log.error("DSP调用统一登录系统接口失败，服务端异常：{}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("DSP调用统一登录系统接口失败，未知异常：{}", e.getMessage(), e);
        }
        return ResultMap.error();
    }
}
