package com.miguan.xuanyuan.service.third.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.miguan.xuanyuan.common.enums.ThirdPlatEnum;
import com.miguan.xuanyuan.service.third.KuaiShouService;
import com.miguan.xuanyuan.service.third.ThirdPlatApiService;
import com.miguan.xuanyuan.vo.ThirdPlatDataVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 第三方快手Service
 */
@Slf4j
@Service
public class KuaiShouServiceImpl implements KuaiShouService {

    @Resource
    private RestTemplate restTemplate;
    private String url = "https://ssp.e.kuaishou.com";
    private String dailyShareUrl = "/api/report/dailyShare";
    @Resource
    private ThirdPlatApiService thirdPlatApiService;
//    @Value("${kuai-shou.ak}")
//    private String ak;
//    @Value("${kuai-shou.sk}")
//    private String sk;

    /**
     * 获取分成数据（日级别）
     *
     * @param date 格式yyy-MM-dd
     * @return impression--展现量，click--点击量，share--营收，ecpm--千次展现收益，ctr--点击量，position_id--代码位。
     * app_id对应的app如下（炫来电-Android:1109644571,炫来电-iOS:1109647473,西柚视频-Android:1109787675,果果视频-iOS:1109869225,果果视频-Android:1110118798,蜜桃视频-Android:1110143343,茜柚视频-iOS:1110490327）
     */
    @Override
    public JSONArray getDailyShare(String date, String ak, String sk) {
        Map<String, Object> params = getParam(date, ak, sk);
        String sign = getSign(params);  //生成签名
        params.put("sign", sign);
        String url = createUrl(params);
        String jsonStr = restTemplate.getForObject(url, String.class);
        JSONArray array = JSONObject.parseObject(jsonStr).getJSONArray("data");
        return array;
    }

    /**
     * 获取快手广告接口统计数据
     * @param date 格式yyy-MM-dd
     * @param ak
     * @param sk
     * @return
     */
    public List<ThirdPlatDataVo> getDataList(String date, String ak, String sk) {
        List<ThirdPlatDataVo> list = new ArrayList<>();
        try {
            JSONArray array = this.getDailyShare(date, ak, sk);
            for(int i=0;i<array.size();i++) {
                list.add(this.formatData(array.getJSONObject(i)));
            }
            if(!list.isEmpty()) {
                //第三方平台的广告数据导入成功
                this.thirdPlatApiService.setSuccessTag(date, ak, sk);
            }
            return list;
        } catch (Exception e) {
            log.error("快手获获取数据失败, ak:{}, sk:{}", ak, sk);
            log.error("快手第三方数据失败", e);
            return list;
        }
    }

    private ThirdPlatDataVo formatData(JSONObject json) {
        ThirdPlatDataVo dataVo = new ThirdPlatDataVo();
        dataVo.setDate(json.getString("date"));
        dataVo.setName(json.getString("name"));
        dataVo.setAdSource(ThirdPlatEnum.KUAI_SHOU.getCode());
        dataVo.setAppId(json.getString("app_id"));  //应用id
        dataVo.setAdId(json.getString("position_id"));  //代码位ID
        dataVo.setShow(json.getInteger("impression"));  //展示量
        dataVo.setClick(json.getInteger("click"));  //点击量
        double ctr = (json.getDouble("ctr") == null ? 0 : json.getDouble("ctr") * 100);
        dataVo.setClickRate(ctr);  //点击率
        dataVo.setRevenue(json.getDouble("share"));  //收益
        dataVo.setEcpm(json.getDouble("ecpm"));  //ecpm
        return dataVo;
    }

    private TreeMap<String, Object> getParam(String date, String ak, String sk) {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("date", date);
        params.put("ak", ak);
        params.put("sk", sk);
        params.put("timestamp", getTimestamp());
        return params;
    }

    /**
     * 生成sign
     *
     * @return
     */
    private String getSign(Map<String, Object> params) {
        StringBuffer signb = new StringBuffer(dailyShareUrl + "?");
        params.forEach((key, value) -> {
            signb.append(key).append("=").append(value).append("&");
        });
        String sign = signb.toString();
        sign = sign.substring(0, sign.length() - 1);
        log.info("快手分成数据（日级别）接口加密前，签名：{}", sign);
        return DigestUtils.md5Hex(sign);   //md5加密
    }

    private String createUrl(Map<String, Object> params) {
        StringBuffer urlb = new StringBuffer(url + dailyShareUrl + "?");
        params.forEach((key, value) -> {
            urlb.append(key).append("=").append(value).append("&");
        });
        String url = urlb.toString();
        url = url.substring(0, url.length() - 1);
        return url;
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
