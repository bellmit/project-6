package com.miguan.report.service.third.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.miguan.report.service.third.GdtService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * 第三方广点通Service
 */
@Slf4j
@Service
public class GdtServiceImpl implements GdtService {

    @Resource
    private RestTemplate restTemplate;
    @Value("${guang-dian-tong.memberid}")
    private String memberid;  //账户id
    @Value("${guang-dian-tong.secret}")
    private String secret;   //sha1的私钥
    @Value("${guang-dian-tong.reportUrl}")
    private String reportUrl;  //查询报表接口url

    /**
     * 查询广点通报表数据(包含点击数，展现数，营收等数据)
     *
     * @param startDate 开始日期，yyyyMMdd，时间跨度不超过2天
     * @param endDate   截至日期，yyyyMMdd，时间跨度不超过2天
     * @return app_id对应的app如下（炫来电(Android):1109644571,炫来电(iOS):1109647473,西柚视频(Android):1109787675,果果视频(iOS):1109869225,果果视频(Android):1110118798,蜜桃视频(Android):1110143343,茜柚视频(iOS):1110490327）
     */
    @Override
    public JSONArray getReportDatas(String startDate, String endDate) {
        StringBuffer url = new StringBuffer(reportUrl);
        url.append("?member_id=").append(memberid).append("&start_date=").append(startDate).append("&end_date=").append(endDate);  //创建接口url

        HttpHeaders headers = new HttpHeaders();
        String time = getTimestamp(); //时间戳
        headers.add("token", createToken(time));  //请求头中添加token
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(url.toString(), HttpMethod.GET, entity, String.class);  //调用广点通接口

        return JSONObject.parseObject(result.getBody()).getJSONObject("data").getJSONArray("list");
    }

    /**
     * 创建token
     *
     * @param time 时间戳(精确到秒)
     * @return
     */
    private String createToken(String time) {
        String sign = createSign(time);  //获取签名
        String token = memberid + "," + time + "," + sign;
        log.info("广点通加密前token：{}", token);
        try {
            token = Base64.getEncoder().encodeToString(token.getBytes("utf-8"));  //对token进行base64加密
        } catch (UnsupportedEncodingException e) {
            log.error("创建广点通接口token异常", e);
        }
        log.info("广点通加密后token：{}", token);
        return token;
    }

    /**
     * 创建sha1加密后的签名
     *
     * @param time 时间戳(精确到秒)
     * @return
     */
    private String createSign(String time) {
        String sign = memberid + secret + time;
        log.info("广点通加密前sign：{}", sign);
        return DigestUtils.sha1Hex(sign);
    }

    /**
     * 获取时间戳(精确到秒)
     *
     * @return
     */
    private String getTimestamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }
}
