package com.miguan.xuanyuan.common.util;

import com.alibaba.fastjson.JSONObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created by lsk on 2015/12/16.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpUtil {



    public static final String RESPONSE_CODE = "code";

    public static final String RESPONSE_CODE_MSG = "message";

    public static final int CLIENT_EXCEPTION_CODE_VALUE = 998; // 连接异常（除请求超时）

    public static final int TIMEOUT_CODE_VALUE = 999; // 请求超时



    private static volatile RestTemplate restTemplate = new RestTemplate();

    static {
//        trustAllHttpsCertificates();
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> messageConverter : messageConverters) {
            if (messageConverter instanceof StringHttpMessageConverter) {
                messageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
            }
        }
        restTemplate.setMessageConverters(messageConverters);
    }
    /**
     * 默认的编码格式
     */
    private static final String CHARSET = "UTF-8";

//    public final static String APPLICATION_JSON = "application/json";
//
//    public final static String APPLICATION_FOEM_URLENCODED = "application/x-www-form-urlencoded";

    /**
     * 默认的超时时间 60 S
     */
    private static final int TIMEOUT = 60000;

    /**
     * 大数据默认的超时时间 10 S
     */
    private static final int VIDEOTIMEOUT = 10000;

    private static Scanner scanner;

//    public static String doGet(String url) {
//        return send(url, null, false, "utf8");
//    }

    public static String postVideoClient(String clientURL, Map<String, String> params) {
        String resp = postClient(clientURL, params, CHARSET, VIDEOTIMEOUT);
        return resp;
    }

    public static String postClient(String clientURL, Map<String, String> params) {
        String resp = postClient(clientURL, params, CHARSET, TIMEOUT);
        return resp;
    }

    /**
     * 根据请求参数生成List<BasicNameValuePair>
     *
     * @param params
     * @return
     */
    private static List<BasicNameValuePair> wrapParam(Map<String, String> params) {
        List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
        for (Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            data.add(new BasicNameValuePair(key, value));
        }
        return data;
    }

    /**
     * 发送http请求
     *
     * @param clientURL
     * @param params
     * @return
     */
    public static String postClient(String clientURL, Map<String, String> params, String charset, int timeout) {
        HttpPost post = new HttpPost(clientURL);
        CloseableHttpClient client = HttpClients.createDefault();
        String result = "";
        try {
            //参数封装
            List<BasicNameValuePair> paramsList = wrapParam(params);
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramsList, charset);

            //设置请求和传输时长
            Builder builder = RequestConfig.custom();

            builder.setSocketTimeout(timeout);
            builder.setConnectTimeout(timeout);

            RequestConfig config = builder.build();

            post.setEntity(entity);
            post.setConfig(config);
            //发起请求
            CloseableHttpResponse response = client.execute(post);
            HttpEntity httpEntity = response.getEntity();

            if (httpEntity != null) {
                result = EntityUtils.toString(httpEntity, charset);
            }
        } catch (SocketTimeoutException e) {
            result = initResult(TIMEOUT_CODE_VALUE, "请求超时");
            log.info("请求发送失败，SocketTimeoutException原因：", e);
        } catch (ClientProtocolException e) {
            result = initResult(CLIENT_EXCEPTION_CODE_VALUE, "请求异常，ClientProtocolException");
            log.info("请求发送失败，ClientProtocolException原因：", e);
        } catch (UnsupportedEncodingException e) {
            result = initResult(CLIENT_EXCEPTION_CODE_VALUE, "请求异常，UnsupportedEncodingException");
            log.info("请求发送失败，UnsupportedEncodingException原因：", e);
        } catch (IOException e) {
            result = initResult(CLIENT_EXCEPTION_CODE_VALUE, "请求异常，IOException");
            log.info("请求发送失败，IOException原因：", e);
        } finally {
            try {
                client.close();
                post.releaseConnection();
            } catch (IOException e) {
                result = initResult(CLIENT_EXCEPTION_CODE_VALUE, "请求异常");
                log.info(e.toString(), e);
            }
        }
        return result;
    }

    /**
     * 初始化返回信息，用于异常时
     */
    public static String initResult(int code, String msg) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(RESPONSE_CODE, code);
        data.put(RESPONSE_CODE_MSG, msg);
        return JSONObject.toJSONString(data);
    }


//    public static String doPost(String url, Map<String, String> params) {
//        return send(url, params, true, "UTF-8");
//    }

    public static String send(String url, Map<String, String> params, boolean post, String readEncode) {
        InputStream input = null;
        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            connection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
            connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36 SE 2.X MetaSr 1.0");
            /*connection.setRequestProperty("Cookie","JSESSIONID=32754410A9908881317F32FE3FA84CB3; j_username=; j_password=");
            connection.setRequestProperty("Cache-Control","max-age=0");*/

            if (post) {
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                ((HttpURLConnection) connection).setRequestMethod("POST");

                if (params != null && !params.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (Entry<String, String> entry : params.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        sb.append(key + "=" + URLEncoder.encode(value, "UTF-8") + "&");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    OutputStream out = connection.getOutputStream();
                    out.write(sb.toString().getBytes("UTF-8"));
                }
            }

            input = connection.getInputStream();
            scanner = new Scanner(input, readEncode);
            scanner.useDelimiter("$");
            return scanner.next();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public static String doPost(String uri, String param) throws IOException {
        return doPost(uri,param,1000);
    }

    public static String doPost(String uri, String param, int timeout) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost post = new HttpPost(uri);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(timeout).setConnectTimeout(timeout)
                .setSocketTimeout(timeout).build();
        post.setConfig(requestConfig);
        post.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(param, StandardCharsets.UTF_8);
        post.setEntity(entity);

        HttpResponse response = httpClient.execute(post);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new RuntimeException("http请求异常" + statusCode);
        }

        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }

    /**
     * resttemplate post方式请求
     * @auth zhicong.lin
     * @date 2019/3/29
     */
    public static <T> T restPost(String url, Map params, Class<T> clazz, HttpHeaders httpHeaders) {
        org.springframework.http.HttpEntity<Map> httpEntity = new org.springframework.http.HttpEntity<>(params, httpHeaders);
        return restTemplate.postForObject(url, httpEntity, clazz);
    }



    public static ResultMap httpSend(String url, LinkedMultiValueMap<String, Object> body){
        try {
            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            org.springframework.http.HttpEntity<MultiValueMap<String, Object>> httpEntity = new org.springframework.http.HttpEntity(body, header);
            ResponseEntity<ResultMap> res = restTemplate.postForEntity(url, httpEntity, ResultMap.class);
            if (res.getStatusCode().is2xxSuccessful()) {
                ResultMap resD = res.getBody();
                return resD;
            }
        } catch (HttpClientErrorException e) {
            log.error("调用接口失败，客户端调用异常：{}", e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            log.error("调用接口失败，服务端异常：{}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("调用接口失败，未知异常：{}", e.getMessage(), e);
        }
        return ResultMap.error();
    }

}
