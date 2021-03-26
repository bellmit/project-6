package com.miguan.advert.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
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

    private static volatile RestTemplate restTemplate = new RestTemplate();

    static {
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> messageConverter : messageConverters) {
            if (messageConverter instanceof StringHttpMessageConverter) {
                messageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
            }
        }
        restTemplate.setMessageConverters(messageConverters);
    }

    private static Scanner scanner;


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

    public static ResultMap httpSend(String url, LinkedMultiValueMap<String, Object> body){
        try {
            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            if(body == null){
                body = new LinkedMultiValueMap(16);
            }
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
