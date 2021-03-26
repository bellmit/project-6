package com.miguan.laidian.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miguan.laidian.common.constants.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 公用工具类
 * @Author zhangbinglin
 * @Date 2019/7/11 15:51
 **/
@Slf4j
public class LaidianUtils {

    /**
     * 根据手机号查询出手机归属地
     *
     * @param phoneNumber 手机号
     * @return
     */
    public static JSONObject queryPhoneAscription(String phoneNumber) {
        String result = null;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("tel", phoneNumber);
            result = HttpUtils.doGet("http://mobsec-dianhua.baidu.com/dianhua_api/open/location", null, params);
            JSONObject getJson = JSONObject.parseObject(result).getJSONObject("response").getJSONObject(phoneNumber).getJSONObject("detail");
            JSONObject resultJson = new JSONObject();
            resultJson.put("operator", getJson.getString("operator"));
            resultJson.put("province", getJson.getString("province"));
            resultJson.put("city", getJson.getJSONArray("area").getJSONObject(0).getString("city"));
            return resultJson;
        } catch (Exception e) {
            //当前接口已无用，屏蔽打印 add shixh0520
            //log.error("根据手机号查询出手机归属地异常,异常日志：" + result);
            return null;
        }
    }

    /**
     * 设置公共校验appType方法
     *
     * @param request
     * @return
     * @Author shixh
     * @Date 2019/9/28
     */
    public static String checkAppType(HttpServletRequest request) {
        String appType = request.getParameter("appType");
        return checkAppType(appType);
    }

    /**
     * 设置公共校验appType方法
     *
     * @param appType
     * @return
     * @Author shixh
     * @Date 2019/9/28
     */
    public static String checkAppType(String appType) {
        if (StringUtils.isNotBlank(appType)) appType = appType.trim();
        if (Constant.appWld.equals(appType)) return Constant.appWld;
        if (Constant.appXld.equals(appType)) return Constant.appXld;
        return Constant.appXld;
    }

    public static String doGet(String url) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String result = "";
        try {
            // 通过址默认配置创建一个httpClient实例
            httpClient = HttpClients.createDefault();
            // 创建httpGet远程连接实例
            HttpGet httpGet = new HttpGet(url);
            // 设置请求头信息，鉴权
            httpGet.setHeader("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
            // 设置配置请求参数
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
                    .setConnectionRequestTimeout(35000)// 请求超时时间
                    .setSocketTimeout(60000)// 数据读取超时时间
                    .build();
            // 为httpGet实例设置配置
            httpGet.setConfig(requestConfig);
            // 执行get请求得到返回对象
            response = httpClient.execute(httpGet);
            // 通过返回对象获取返回数据
            HttpEntity entity = response.getEntity();
            // 通过EntityUtils中的toString方法将结果转换为字符串
            result = EntityUtils.toString(entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String url = "http://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=18826402758";
        String re = doGet(url);
        int i = re.indexOf("{");
        String s = re.substring(i, re.length() - 1);
        HashMap hashMap = JSON.parseObject(s, HashMap.class);
        System.out.println(hashMap.toString());
    }

    /**
     * 根据时间类型选择策略
     * @param eventType
     * @return
     */
    public static Integer getStrategyByEvent(Integer eventType){
        Integer strategyType;
        switch (eventType){
            case 4001:
            case 4002:
            case 5001:
            case 5002:
            case 5003:
                strategyType = 2;
                break;
            case 6001:
            case 6002:
            case 6003:
            case 6004:
            case 8001:
                strategyType = 3;
                break;
            case 7001:
            case 7002:
                strategyType = 4;
                break;
            default:
                strategyType = 1;
        }
        return strategyType;
    }
}
