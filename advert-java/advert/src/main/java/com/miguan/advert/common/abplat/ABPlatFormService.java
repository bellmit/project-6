package com.miguan.advert.common.abplat;

import cn.jiguang.common.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.cgcg.base.format.Result;
import com.google.common.collect.Maps;
import com.miguan.advert.common.constants.FlowGroupConstant;
import com.miguan.advert.common.util.AbResultMap;
import com.miguan.advert.common.util.ResultMap;
import com.miguan.advert.domain.vo.interactive.AbExperiment;
import com.miguan.advert.domain.vo.interactive.AbFlowDistribution;
import com.miguan.advert.domain.vo.interactive.AbLayer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @program: advert-java
 * @description: AB实验平台平台鉴权
 * @author: suhj
 * @create: 2020-09-22 18:07
 **/
@Slf4j
@Service
public class ABPlatFormService {

    @Value("${send.abtest.url}")
    private String url;

    @Value("${send.abtest-php.url}")
    private String php_url;
    @Value("${send.abtest-php.userName}")
    private String userName;
    @Value("${send.abtest-php.password}")
    private String password;

    //调用接口
    private static final String API_ROUTE_URL = "/v1/api/getExpById";

    //调用接口：登录
    private static final String API_SSO_LOGIN = "/api/sso/login";

    //调用接口：获取标签列表
    private static final String API_TAG_LIST = "/api/tag/list";

    //调用接口：获取层级信息
    private static final String API_LAYER_INFO = "/api/layer/info";

    //调用接口：获取app列表
    private static final String API_APP_LIST = "/api/app/list";

    //调用接口：获取参数条件
    private static final String API_EXPERIMENT_GETCONDITION = "/api/experiment/getCondition";

    //调用接口：获取实验组信息
    private static final String API_EXPERIMENT_GETGROUPS = "/api/experiment/getGroups";

    //调用接口：创建分层
    private static final String API_LAYER_CREATE = "/api/layer/create";

    //调用接口：创建实验详情
    private static final String API_EXPERIMENT_CREATE = "/api/experiment/create";

    //调用接口：修改实验详情
    private static final String API_EXPERIMENT_UPDATE = "/api/experiment/update";

    //调用接口：查询实验详情
    private static final String API_EXPERIMENT_INFO = "/api/experiment/info";

    //调用接口：创建流量分配
    private static final String API_EXPERIMENT_TRAFFICEDIT = "/api/experiment/trafficEdit";

    //调用接口：修改运行状态
    private static final String API_EXPERIMENT_UPDATESTATE = "/api/experiment/updateState";

    //流量分组默认前缀
    private static final String FLOW_PREFIX = "ad_exp";

    @Resource
    private RestTemplate restTemplate;

    public ResultMap getExpById(String expId){
        String errStr = "";
        try {
            ResponseEntity<ResultMap> res = restTemplate.getForEntity(url.concat(API_ROUTE_URL)
                    .concat("?exp_id=").concat(expId), ResultMap.class);
            if (res.getStatusCode().is2xxSuccessful()) {
                ResultMap resD = res.getBody();
                return resD;
            }
        } catch (HttpClientErrorException e) {
            errStr = "DSP调用AB实验平台接口失败，客户端调用异常";
            log.error(errStr + "：{}", e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            errStr = "DSP调用AB实验平台接口失败，服务端异常";
            log.error(errStr + "：{}", e.getMessage(), e);

        } catch (Exception e) {
            errStr = "DSP调用AB实验平台接口失败，服务端异常";
            log.error(errStr + "：{}", e.getMessage(), e);
        }
        return ResultMap.error(errStr);
    }

    public AbResultMap login(){
        String errStr = "";
        try {
            ResponseEntity<AbResultMap> res = restTemplate.getForEntity(php_url.concat(API_SSO_LOGIN)
                    .concat("?user_name=").concat(userName).concat("&password=").concat(password), AbResultMap.class);
            if (res.getStatusCode().is2xxSuccessful()) {
                AbResultMap resD = res.getBody();
                return resD;
            }
        } catch (HttpClientErrorException e) {
            errStr = "DSP调用AB实验平台接口失败，客户端调用异常";
            log.error(errStr + "：{}", e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            errStr = "DSP调用AB实验平台接口失败，服务端异常";
            log.error(errStr + "：{}", e.getMessage(), e);

        } catch (Exception e) {
            errStr = "DSP调用AB实验平台接口失败，服务端异常";
            log.error(errStr + "：{}", e.getMessage(), e);
        }
        return AbResultMap.error(errStr);
    }


    private HttpEntity createHeader(Object o, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        return new HttpEntity<>(o, headers);
    }

    public AbResultMap getTags(Integer appId, String token){
        Map<String,String> params = Maps.newHashMap();
        params.put("app_id",String.valueOf(appId));
        return getUrl(params, token,API_TAG_LIST);
    }

    public AbResultMap appList(String token) {
        Map<String,String> params = Maps.newHashMap();
        return getUrl(params, token,API_APP_LIST);
    }

    public AbResultMap getCondition(Integer appId, Integer expId, String token) {
        Map<String,String> params = Maps.newHashMap();
        params.put("app_id",String.valueOf(appId));
        params.put("type",2+"");
        params.put("experiment_id",String.valueOf(expId));
        return getUrl(params, token,API_EXPERIMENT_GETCONDITION);
    }


    public AbResultMap getLayerTrafficById(Integer olayerId, String token) {
        Map<String,String> params = Maps.newHashMap();
        params.put("layer_id",String.valueOf(olayerId));
        params.put("type",2+"");
        return getUrl(params, token,API_LAYER_INFO);
    }


    /**
     * 保存层级
     * @param abLayer
     * @param token
     * @return
     */
    public AbResultMap saveLayer(AbLayer abLayer, String token){
        return postUrl(abLayer, token,API_LAYER_CREATE);
    }

    /**
     * 创建实验
     * @param abExperiment
     * @param token
     * @return
     */
    public AbResultMap saveAbExperiment(AbExperiment abExperiment, String token) {
        if(abExperiment.getId() == null){
            abExperiment.setState(0);//将实验状态置为预运行
            return postUrl(abExperiment, token,API_EXPERIMENT_CREATE);
        } else {
            return postUrl(abExperiment, token,API_EXPERIMENT_UPDATE);
        }
    }

    /**
     * 获得实验信息
     * @param exp_id
     * @param appId
     * @param token
     * @return
     */
    public AbResultMap getExperimentInfo(String exp_id, Integer appId, String token) {
        Map<String,String> params = Maps.newHashMap();
        params.put("app_id",String.valueOf(appId));
        params.put("experiment_id",exp_id);
        return getUrl(params, token,API_EXPERIMENT_INFO);
    }

    private AbResultMap getUrl(Map<String, String> params, String token, String uri) {
        String errStr = "";
        try {
            String url = php_url.concat(uri);
            Set<String> keys = params.keySet();
            int flag = 0;
            for (String key:keys) {
                if(flag == 0){
                    flag ++ ;
                    url = url.concat("?"+key+"=").concat(params.get(key));
                } else {
                    url = url.concat("&"+key+"=").concat(params.get(key));
                }
            }
            HttpEntity httpEntity = createHeader(null,token);
            ResponseEntity<AbResultMap> res = restTemplate.exchange(url, HttpMethod.GET,httpEntity, AbResultMap.class);
            if (res.getStatusCode().is2xxSuccessful()) {
                AbResultMap resD = res.getBody();
                return resD;
            }
        } catch (HttpClientErrorException e) {
            errStr = "DSP调用AB实验平台接口失败，客户端调用异常";
            log.error(errStr + "：{}", e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            errStr = "DSP调用AB实验平台接口失败，服务端异常";
            log.error(errStr + "：{}", e.getMessage(), e);

        } catch (Exception e) {
            errStr = "DSP调用AB实验平台接口失败，服务端异常";
            log.error(errStr + "：{}", e.getMessage(), e);
        }
        return AbResultMap.error(errStr);
    }

    /**
     * 流量分配
     * @param abFlowDistribution
     * @param token
     * @return
     */
    public AbResultMap saveAbFlowDistribution(AbFlowDistribution abFlowDistribution, String token) {
        return postUrl(abFlowDistribution, token,API_EXPERIMENT_TRAFFICEDIT);
    }


    public AbResultMap searchGroupByExpId(Integer exp_id, String token) {
        String errStr = "";
        try {
            HttpEntity httpEntity = createHeader(null,token);
            ResponseEntity<AbResultMap> res = restTemplate.exchange(php_url.concat(API_EXPERIMENT_GETGROUPS).concat("?experiment_id=")
                    .concat(String.valueOf(exp_id)), HttpMethod.GET,httpEntity, AbResultMap.class);
            if (res.getStatusCode().is2xxSuccessful()) {
                AbResultMap resD = res.getBody();
                return resD;
            }
        } catch (HttpClientErrorException e) {
            errStr = "DSP调用AB实验平台接口失败，客户端调用异常";
            log.error(errStr + "：{}", e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            errStr = "DSP调用AB实验平台接口失败，服务端异常";
            log.error(errStr + "：{}", e.getMessage(), e);

        } catch (Exception e) {
            errStr = "DSP调用AB实验平台接口失败，服务端异常";
            log.error(errStr + "：{}", e.getMessage(), e);
        }
        return AbResultMap.error(errStr);
    }


    public AbResultMap sendEditState(Map<String, Object> param, String token) {
        return postUrl(param, token,API_EXPERIMENT_UPDATESTATE);
    }

    private AbResultMap postUrl(Object param, String token,String url) {
        String errStr = "";
        try {
            HttpEntity httpEntity = createHeader(param,token);
            ResponseEntity<AbResultMap> res = restTemplate.postForEntity(php_url.concat(url),httpEntity,AbResultMap.class);
            if (res.getStatusCode().is2xxSuccessful()) {
                AbResultMap resD = res.getBody();
                return resD;
            }
        } catch (HttpClientErrorException e) {
            errStr = "广告配置平台调用AB实验平台接口失败，客户端调用异常";
            log.error(errStr + "：{}", e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            errStr = "广告配置平台调用AB实验平台接口失败，服务端异常";
            log.error(errStr + "：{}", e.getMessage(), e);
        } catch (Exception e) {
            errStr = "广告配置平台调用AB实验平台接口失败，服务端异常";
            log.error(errStr + "：{}", e.getMessage(), e);
        }
        return AbResultMap.error(errStr);
    }


    /**
     * 调用AB实验平台 流量分组接口
     * @param abFlowId 实验分组ID
     * @return
     */
    public ResultMap<Map<String,Object>> getAbFlowMapByInt(String abFlowId) {

        Map<String,Object> retMap = Maps.newHashMap();

        ResultMap resultMap = getExpById(abFlowId);
        if (resultMap.getCode() != 200) {
            if (resultMap.getCode() == 500) {
                return ResultMap.error("AB实验平台的实验ID不存在");
            }
            return ResultMap.error("AB实验平台返回参数异常");
        }
        Map<String,Object> acceptorResponse = (Map<String,Object>)resultMap.getData();

        Map<String,String> expInfo = (Map<String,String>) acceptorResponse.get("exp_info");
        if(MapUtils.isNotEmpty(expInfo)){
            if (!expInfo.get("code").startsWith(FLOW_PREFIX)) {
                return ResultMap.error("AB实验平台的实验CODE配置有误,code:" + expInfo.get("code"));
            }
            retMap.put("exp_info",expInfo);
        }

        String filterParam = (String) acceptorResponse.get("filter_param");
        if(StringUtils.isNotEmpty(filterParam)){
            List<String> filterParamLst = Arrays.asList(filterParam.split("&&"));
            for (int i = 0; i < filterParamLst.size(); i++) {
                String filterObj = filterParamLst.get(i);
                JSONObject jsonObject = null;
                try {
                    jsonObject = JSONObject.parseObject(filterObj);
                } catch (Exception e) {
                    return ResultMap.error("AB实验实验平台条件解析失败，实验ID：" + abFlowId + "，报文：" + filterParam);
                }
                String key = (String) jsonObject.get("key");
                String operation = (String) jsonObject.get("operation");
                String value = (String) jsonObject.get("value");
                if("app_version".equals(key)){
                    if(operation.equals(">=")){
                        retMap.put("version1",value);
                        retMap.put("versionFlag","大于等于");
                    }
                    if(operation.equals(">")){
                        retMap.put("version1",value);
                        retMap.put("versionFlag","大于");
                    }
                    if(operation.equals("<=")){
                        retMap.put("version2",value);
                        retMap.put("versionFlag","小于等于");
                    }
                    if(operation.equals("<")){
                        retMap.put("version2",value);
                        retMap.put("versionFlag","小于");
                    }
                    if(operation.equals("=")){
                        retMap.put("version3",value);
                        retMap.put("versionFlag","等于");
                    }

                    if(operation.equals("in")){
                        retMap.put("version3",value);
                        retMap.put("versionFlag","仅限版本:");
                    }
                    if(operation.equals("notin")){
                        retMap.put("version4",value);
                        retMap.put("versionFlag","排除版本:");
                    }
                }
                if("channel".equals(key)){
                    if(operation.equals("in")){
                        retMap.put("channel",value);
                    }
                    if(operation.equals("notin")){
                        retMap.put("channel2",value);
                    }
                }
                if("father_channel".equals(key)){
                    if(operation.equals("in")){
                        retMap.put("channel",value);
                    }
                    if(operation.equals("notin")){
                        retMap.put("channel2",value);
                    }
                }
                if("is_new".equals(key)){
                    if(operation.equals("in")){
                        retMap.put("is_new",value);
                    }
                }
            }
        }
        List<Map<String,String>> expGroup = (List<Map<String,String>>)acceptorResponse.get("exp_group");
        if(CollectionUtils.isNotEmpty(expGroup)){
            retMap.put("testArr",expGroup);
        }

        return ResultMap.success(retMap);
    }

    public static void main(String[] args) {
        String filterParam = "{\"key\":\"app_version\",\"operation\":\">=\",\"value\":\"1.0.0\"}&&{\"key\":\"app_version\",\"operation\":\"<=\",\"value\":\"4.0.0\"}&&{\"key\":\"channel\",\"operation\":\"in\",\"value\":\"123,456\"}";
        List<String> filterParamLst = Arrays.asList(filterParam.split("&&"));
        Map<String,String> retMap = Maps.newHashMap();
        for (int i = 0; i < filterParamLst.size(); i++) {
            String filterObj = filterParamLst.get(i);
            JSONObject jsonObject =JSONObject.parseObject(filterObj);
            String key = (String) jsonObject.get("key");
            String operation = (String) jsonObject.get("operation");
            String value = (String) jsonObject.get("value");
            if("app_version".equals(key)){
                if(operation.equals(">=")){
                    retMap.put("version1",value);
                }
                if(operation.equals("<=")){
                    retMap.put("version2",value);
                }
            }
            if("channel".equals(key)){
                if(operation.equals("in")){
                    retMap.put("channel",value);
                }
            }
        }
    }

}
