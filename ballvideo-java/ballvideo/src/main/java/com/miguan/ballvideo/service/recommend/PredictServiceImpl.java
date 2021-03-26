package com.miguan.ballvideo.service.recommend;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miguan.ballvideo.common.config.service.PredictConfig;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.util.HttpUtil;
import com.miguan.ballvideo.service.PredictService;
import com.miguan.ballvideo.vo.video.FirstVideoParamsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PredictServiceImpl implements PredictService {
    private final static String ApiPrefix = "/api/predict";
    private final static String predictApi = "/playrate2";
    private final static String predictApi3 = "/playrate3";
    private final static String immerse = "/api/immerse/video";
    private final static String code = "code";
    private final static int success = 200;
    private final static String recommend = "/api/video/recommend";
    private final static String recommendOther = "/api/cat/recommend";
    private final static String recommendDetail = "/api/video/detail/recommend";
    private final static String recommendNext = "/api/video/relevant/recommend";
    private final static String activeDate = "/api/distinct/activeDate";

    @Value("${spring.service.recommend.host}")
    private String recommendUrl;

    @Value("${spring.service.xyuid.host}")
    private String xyuidUrl;

    @Resource
    private PredictConfig predictConfig;

    @Override
    public Map<String, BigDecimal> predictPlayRate(List<Map<String, Object>> listFeature, boolean isABTest) {
        String api = predictApi;
        if(isABTest){
            api = predictApi3;
        }
        String predictUrl = getPredictApi(api);
        String jsonData = JSON.toJSONString(listFeature);
        try {
            String rs =HttpUtil.doPost(predictUrl,jsonData);
            return (Map<String, BigDecimal>)JSON.parse(rs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPredictApi(String ApiName){
        return "http://"+predictConfig.getHost()+ApiPrefix+ApiName;
    }

    public String getImmerseApi(){
        return "http://" + predictConfig.getHost() + immerse;
    }

    @Override
    public List<String> getImmerseVideoIds(String catIds) {
        Map<String, String> params = new HashMap<>();
        params.put("catids", catIds);
        String predictUrl = getImmerseApi();
        try {
            String result = HttpUtil.postClient(predictUrl, params);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if(jsonObject.getInteger(code) != null && success == jsonObject.getInteger(code)){
                String data = jsonObject.getString("data");
                if (StringUtils.isNotEmpty(data)) {
                    return (List<String>) JSON.parse(data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getRecommendVideoApi(){
        return "http://" + recommendUrl + recommend;
    }

    private String getRecommendOtherVideoApi(){
        return "http://" + recommendUrl + recommendOther;
    }

    private String getRecommendDetailVideoApi(){
        return "http://" + recommendUrl + recommendDetail;
    }

    private String getRecommendNextVideoApi(){
        return "http://" + recommendUrl + recommendNext;
    }

    @Override
    public String getRecommendVideoIds(FirstVideoParamsVo paramsVo, int type) {
        Map<String, String> params = new HashMap<>();
        String predictUrl = "";
        params.put("publicInfo", paramsVo.getPublicInfo());
        params.put("sensitiveState", paramsVo.getSensitiveState());
        if (type == Constant.recommend1) {
            params.put("defaultCat", paramsVo.getDefaultCat());
            params.put("excludeCat", paramsVo.getExcludeCat());
            params.put("incentiveVideoNum", paramsVo.getIncentiveVideoNum());
            params.put("ip", paramsVo.getIp());
            params.put("deviceId", paramsVo.getDeviceId());
            params.put("abExp", paramsVo.getAbExp());
            params.put("abIsol", paramsVo.getAbIsol());
            predictUrl = getRecommendVideoApi();
        } else if (type == Constant.recommend2) {
            params.put("ip", paramsVo.getIp());
            params.put("catid", paramsVo.getCatid());
            params.put("num", paramsVo.getNum());
            params.put("abExp", paramsVo.getAbExp());
            params.put("abIsol", paramsVo.getAbIsol());
            predictUrl = getRecommendOtherVideoApi();
        } else if (type == Constant.recommend3) {
            params.put("videoId", paramsVo.getVideoId());
            params.put("catid", paramsVo.getCatid());
            params.put("num", paramsVo.getNum());
            predictUrl = getRecommendDetailVideoApi();
        } else if (type == Constant.recommend4) {
            params.put("videoId", paramsVo.getVideoId());
            params.put("num", "1");
            params.put("abExp", paramsVo.getAbExp());
            params.put("abIsol", paramsVo.getAbIsol());
            predictUrl = getRecommendNextVideoApi();
        }
        return HttpUtil.postVideoClient(predictUrl, params);
    }

    @Override
    public String getActiveDate(String packageName, String distinctId) {
        Map<String, String> params = new HashMap<>();
        String url = "http://" + xyuidUrl + activeDate;
        params.put("packageName", packageName);
        params.put("distinctId", distinctId);
        return HttpUtil.postVideoClient(url, params);
    }
}
