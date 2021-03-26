package com.miguan.laidian.common.util;

import com.alibaba.fastjson.JSONObject;
import com.miguan.laidian.common.constants.Constant;
import com.mysql.jdbc.ExportControlled;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created by lsk on 2015/12/16.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpUtil {

    private static volatile RestTemplate restTemplate = new RestTemplate();

    static {
        trustAllHttpsCertificates();
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

    public final static String APPLICATION_JSON = "application/json";

    public final static String APPLICATION_FOEM_URLENCODED = "application/x-www-form-urlencoded";

    /**
     * 默认的超时时间 60 S
     */
    private static final int TIMEOUT = 60000;

    private static Scanner scanner;

    public static String doGet(String url) {
        return send(url, null, false, "utf8");
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
            result = initResult(Constant.TIMEOUT_CODE_VALUE, "请求超时");
            log.info("请求发送失败，SocketTimeoutException原因：", e);
        } catch (ClientProtocolException e) {
            result = initResult(Constant.CLIENT_EXCEPTION_CODE_VALUE, "请求异常，ClientProtocolException");
            log.info("请求发送失败，ClientProtocolException原因：", e);
        } catch (UnsupportedEncodingException e) {
            result = initResult(Constant.CLIENT_EXCEPTION_CODE_VALUE, "请求异常，UnsupportedEncodingException");
            log.info("请求发送失败，UnsupportedEncodingException原因：", e);
        } catch (IOException e) {
            result = initResult(Constant.CLIENT_EXCEPTION_CODE_VALUE, "请求异常，IOException");
            log.info("请求发送失败，IOException原因：", e);
        } finally {
            try {
                client.close();
                post.releaseConnection();
            } catch (IOException e) {
                result = initResult(Constant.CLIENT_EXCEPTION_CODE_VALUE, "请求异常");
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
        data.put(Constant.RESPONSE_CODE, code);
        data.put(Constant.RESPONSE_CODE_MSG, msg);
        return JSONObject.toJSONString(data);
    }


    public static String doPost(String url, Map<String, String> params) {
        return send(url, params, true, "UTF-8");
    }

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


    /**
     * 发起http请求，获取响应结果
     *
     * @return
     */
    public static String getHttpResponse(String targetURL) {
        String result = "";
        BufferedReader in = null;
        InputStreamReader isr = null;
        InputStream is = null;
        HttpURLConnection huc = null;
        try {
            URL url = new URL(targetURL);
            huc = (HttpURLConnection) url.openConnection();
            is = huc.getInputStream();
            isr = new InputStreamReader(is);
            in = new BufferedReader(isr);
            String line = null;
            while (((line = in.readLine()) != null)) {
                if (line.length() == 0)
                    continue;
                result += line;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (is != null)
                    is.close();
                if (isr != null)
                    isr.close();
                if (in != null)
                    in.close();
                if (huc != null)
                    huc.disconnect();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return result;
    }

    /**
     * 发送post请求
     *
     * @return
     */
    public static JSONObject postClient(String urlString, String jsonObject) {
        long currentTimeMillis = System.currentTimeMillis();
        JSONObject returnJson = null;
        try {
            // 创建连接
            URL url = new URL(urlString);

            if ("https".equalsIgnoreCase(url.getProtocol())) {
                try {
                    SslUtils.ignoreSsl();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");//**注意点1**，需要此格式，后边这个字符集可以不设置
            connection.connect();
            DataOutputStream out = new DataOutputStream(
                    connection.getOutputStream());
            out.write(jsonObject.toString().getBytes("UTF-8"));//**注意点2**，需要此格式
            out.flush();
            out.close();
            // 读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "utf-8"));//**注意点3**，需要此格式
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                sb.append(lines);
            }
            returnJson = JSONObject.parseObject(sb.toString());
            reader.close();
            // 断开连接
            connection.disconnect();
        } catch (MalformedURLException e) {
            log.error(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        System.out.println("returnJson = " + (System.currentTimeMillis() - currentTimeMillis));
        return returnJson;
    }


    /**
     * dsData接口需要token
     *
     * @param urlString
     * @param jsonObject
     * @param token
     * @return
     */
    public static JSONObject postClient(String urlString, String jsonObject, String token) {
        JSONObject returnJson = null;
        try {
            // 创建连接
            URL url = new URL(urlString);
            if ("https".equalsIgnoreCase(url.getProtocol())) {
                try {
                    SslUtils.ignoreSsl();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");//**注意点1**，需要此格式，后边这个字符集可以不设置
            connection.setRequestProperty("auth_token", token);

            connection.setConnectTimeout(60000);  //设置连接主机超时（单位：毫秒） 
            connection.setReadTimeout(60000); //设置从主机读取数据超时（单位：毫秒） 

            connection.connect();
            DataOutputStream out = new DataOutputStream(
                    connection.getOutputStream());
            out.write(jsonObject.toString().getBytes("UTF-8"));//**注意点2**，需要此格式
            out.flush();
            out.close();
            // 读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "utf-8"));//**注意点3**，需要此格式
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                sb.append(lines);
            }
            returnJson = JSONObject.parseObject(sb.toString());
            reader.close();
            // 断开连接
            connection.disconnect();
        } catch (MalformedURLException e) {
            log.error(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return returnJson;
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

    /**
     * resttemplate post方式请求
     * @auth zhicong.lin
     * @date 2019/3/29
     */
    public static <T> T getPost(String url, Map params, Class<T> clazz, HttpHeaders httpHeaders) {
        org.springframework.http.HttpEntity<Map> httpEntity = new org.springframework.http.HttpEntity<>(params, httpHeaders);
        return restTemplate.getForObject(url, clazz, httpEntity);
    }

    /**
     * resttemplate post方式请求
     * @auth zhicong.lin
     * @date 2019/3/29
     */
    public static <T> T restPost(String url, Map params, Class<T> clazz) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return restPost(url, params, clazz, httpHeaders);
    }

    /**
     * 去除https证书验证 .
     * @Return: void
     * @Author: ZhiCong Lin
     * @Date: 2018/12/3 13:43
     */
    private static void trustAllHttpsCertificates() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[1];
            TrustManager tm = new ExportControlled.X509TrustManagerWrapper();
            trustAllCerts[0] = tm;
            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, trustAllCerts, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((urlHostName, session) -> {
                log.warn("URL Host: {} vs. {}", urlHostName, session.getPeerHost());
                return true;
            });
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static String post(String urlString, String context) {
        return post(urlString, context, CHARSET, null);
    }

    public static String post(String urlString, String context, String charset, String contentType) {
        log.info("\n请求地址>>{}\n请求报文>>{}", urlString, context);
        HttpURLConnection httpURLConnection = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            if (APPLICATION_JSON.equals(contentType)) {
                httpURLConnection.setRequestProperty("Content-Type", APPLICATION_JSON);
                httpURLConnection.setRequestProperty("Accept-Charset", charset);
            } else if (APPLICATION_FOEM_URLENCODED.equals(contentType)) {
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            } else {
                httpURLConnection.setRequestProperty("contentType", charset);
            }
            out = new PrintWriter(httpURLConnection.getOutputStream());
            out.write(context);
            out.flush();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuffer content = new StringBuffer();
                String tempStr = null;
                in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), charset));
                while ((tempStr = in.readLine()) != null) {
                    content.append(tempStr);
                }

                log.info("\n响应报文>>{}", content.toString());
                return content.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Http Post Exception:", e);
            return null;
        } finally {
            if (out != null) {
                out.close();
            }
            httpURLConnection.disconnect();
        }
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
