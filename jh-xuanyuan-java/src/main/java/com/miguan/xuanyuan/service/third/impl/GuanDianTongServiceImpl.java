package com.miguan.xuanyuan.service.third.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.miguan.xuanyuan.common.enums.ThirdPlatEnum;
import com.miguan.xuanyuan.service.third.GuanDianTongService;
import com.miguan.xuanyuan.service.third.ThirdPlatApiService;
import com.miguan.xuanyuan.vo.ThirdPlatDataVo;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * 第三方广点通Service
 */
@Slf4j
@Service
public class GuanDianTongServiceImpl implements GuanDianTongService {

    @Resource
    private RestTemplate restTemplate;
//    @Value("${guang-dian-tong.memberid}")
//    private String memberid;  //账户id
//    @Value("${guang-dian-tong.secret}")
//    private String secret;   //sha1的私钥
//    @Value("${guang-dian-tong.reportUrl}")
    private String reportUrl = "https://api.adnet.qq.com/open/v1.1/report/get";  //查询报表接口url
    @Resource
    private ThirdPlatApiService thirdPlatApiService;

    /**
     * 查询广点通报表数据(包含点击数，展现数，营收等数据)
     *
     * @param startDate 开始日期，yyyyMMdd，时间跨度不超过2天
     * @param endDate   截至日期，yyyyMMdd，时间跨度不超过2天
     * @return app_id对应的app如下（炫来电(Android):1109644571,炫来电(iOS):1109647473,西柚视频(Android):1109787675,果果视频(iOS):1109869225,果果视频(Android):1110118798,蜜桃视频(Android):1110143343,茜柚视频(iOS):1110490327）
     */
    @Override
    public JSONArray getReportDatas(String startDate, String endDate, String memberid, String secret) {
        StringBuffer url = new StringBuffer(reportUrl);
        url.append("?member_id=").append(memberid).append("&start_date=").append(startDate).append("&end_date=").append(endDate);  //创建接口url

        HttpHeaders headers = new HttpHeaders();
        String time = getTimestamp(); //时间戳
        headers.add("token", createToken(time, memberid, secret));  //请求头中添加token
        JSONObject params = new JSONObject();
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(url.toString(), HttpMethod.GET, entity, String.class);  //调用广点通接口

        return JSONObject.parseObject(result.getBody()).getJSONObject("data").getJSONArray("list");
    }

    /**
     * 获取快手广告接口统计数据
     * @param date 格式yyy-MM-dd
     * @param memberid
     * @param secret
     * @return
     */
    public List<ThirdPlatDataVo> getDataList(String date, String memberid, String secret) {
        List<ThirdPlatDataVo> list = new ArrayList<>();
        try {
            date = date.replace("-", "");
            JSONArray array = this.getReportDatas(date, date, memberid, secret);
            for(int i=0;i<array.size();i++) {
                list.add(this.formatData(array.getJSONObject(i)));
            }
            if(!list.isEmpty()) {
                //第三方平台的广告数据导入成功
                this.thirdPlatApiService.setSuccessTag(date, memberid, secret);
            }
            return list;
        } catch (Exception e) {
            log.error("广点通获取数据失败, memberid:{}", memberid);
            log.error("广点通第三方数据失败", e);
            return list;
        }
    }

    private ThirdPlatDataVo formatData(JSONObject json) {
        ThirdPlatDataVo dataVo = new ThirdPlatDataVo();
        dataVo.setDate(json.getString("date"));
        dataVo.setName(json.getString("member_id"));  //开发者账号，
        dataVo.setAdSource(ThirdPlatEnum.GUANG_DIAN_TONG.getCode());  //平台key
        dataVo.setAppId(json.getString("app_id"));  //应用id
        dataVo.setAppName(json.getString("member_name"));  //应用名称
        dataVo.setAdId(json.getString("placement_id"));  //代码位ID
        dataVo.setAdName(json.getString("placement_name"));  //代码位名称
        dataVo.setAdType(json.getString("placement_type"));  //代码位类型
        dataVo.setShow(json.getInteger("pv"));  //展示量
        dataVo.setClick(json.getInteger("click"));  //点击量
        dataVo.setRevenue(json.getDouble("revenue"));  //收益
        dataVo.setEcpm(json.getDouble("ecpm"));  //ecpm
        dataVo.setRequestCount(json.getInteger("request_count"));  //广告位请求量
        dataVo.setReturnCount(json.getInteger("return_count"));  //广告位返回量
        dataVo.setAdRequestCount(json.getInteger("ad_request_count"));  //广告请求量
        dataVo.setAdReturnCount(json.getInteger("ad_return_count"));  //广告返回量
        //点击率
        String clickRate = (json.get("click_rate") == null ? "0" : json.getString("click_rate"));
        clickRate = clickRate.replace("%", "");
        dataVo.setClickRate(Double.parseDouble(clickRate));

        //广告位填充率
        String fillRate = (json.get("fill_rate") == null ? "0" : json.getString("fill_rate"));
        fillRate = fillRate.replace("%", "");
        dataVo.setFillRate(Double.parseDouble(fillRate));
        //广告位曝光率
        String exposureRate = (json.get("exposure_rate") == null ? "0" : json.getString("exposure_rate"));
        exposureRate = exposureRate.replace("%", "");
        dataVo.setExposureRate(Double.parseDouble(exposureRate));
        //广告填充率
        String adFillRate = (json.get("ad_fill_rate") == null ? "0" : json.getString("ad_fill_rate"));
        adFillRate = adFillRate.replace("%", "");
        dataVo.setAdFillRate(Double.parseDouble(adFillRate));
        //广告曝光率
        String adExposureRate = (json.get("ad_exposure_rate") == null ? "0" : json.getString("ad_exposure_rate"));
        adExposureRate = adExposureRate.replace("%", "");
        dataVo.setAdExposureRate(Double.parseDouble(adExposureRate));
        dataVo.setCpc(json.getDouble("cpc"));
        return dataVo;
    }


    /**
     * 创建token
     *
     * @param time 时间戳(精确到秒)
     * @return
     */
    private String createToken(String time, String memberid, String secret) {
        String sign = createSign(time, memberid, secret);  //获取签名
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
    private String createSign(String time, String memberid, String secret) {
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
