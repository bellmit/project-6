package com.miguan.laidian.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.poi.util.IOUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * HTTP客户端帮助类
 * Created by 98du on 2020/8/14.
 */
public class HttpClientUtil {
	
	private static final String CHAR_SET = "UTF-8";
	
	private static CloseableHttpClient httpClient;
	private static int socketTimeout =  10000;
	private static int connectTimeout = 3000;
	private static int connectionRequestTimeout = 3000;
	private static int retryMaxTimes = 0;
	static {
		// 加入3次重试机制，解决因服务器压力大，请求失败问题
		HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
			@Override
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				if (executionCount >= retryMaxTimes) {
					// Do not retry if over max retry count
					return false;
				}
				if (exception instanceof NoHttpResponseException) {
					return true;
				}
				return false;
			}

		};
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(800);
		cm.setDefaultMaxPerRoute(400);
		RequestConfig config = RequestConfig.custom()
				.setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout)
				.setConnectionRequestTimeout(connectionRequestTimeout).build();
		httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(config).setRetryHandler(myRetryHandler).build();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				cm.closeExpiredConnections();
				cm.closeIdleConnections(5, TimeUnit.SECONDS);
			}
		}, 0, 5 * 1000);
	}
	
	public static CloseableHttpClient getClient() {
		return httpClient;
	}
	
	public static String get(String url) throws IOException {
		return get(url, null, null);
	}

	public static String get(String url, int connTimeout, int soTimeOut) throws IOException {
		return get(url, null, null, connTimeout, soTimeOut);
	}

	public static String get(String url, Map<String, String> map) throws IOException {
		return get(url, map, null);
	}
	
	public static String get(String url, String charset) throws IOException {
		return get(url, null, charset);
	}
	
	public static String get(String url, Map<String, String> paramsMap, String charset) throws IOException {
		CloseableHttpResponse response = null;
		try{
			if (url == null || url.isEmpty()) {
				return null;
			}
			charset = (charset == null ? CHAR_SET : charset);
			if(null != paramsMap && !paramsMap.isEmpty()) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> map : paramsMap.entrySet()) {
					params.add(new BasicNameValuePair(map.getKey(), map.getValue()));
				}
				String querystring = URLEncodedUtils.format(params, charset);
				if(url.contains("?")){
					url += "&" + querystring;
				}else{
					url += "?" + querystring;
				}
			}
			HttpGet httpGet = new HttpGet(url);
			httpGet.addHeader("Accept-Encoding", "*");
			response = getClient().execute(httpGet);
			//状态不为200的异常处理。
			int httpCode = response.getStatusLine().getStatusCode();
			if (httpCode != 200) {
				httpGet.abort();
				throw new RuntimeException("HttpClient,error status code :" + httpCode);
			}
			return EntityUtils.toString(response.getEntity(), charset);
		}catch (Exception e){
			throw e;
		}finally {
			if (response != null) {
				response.close();
			}
		}
	}


	public static String get(String url, Map<String, String> paramsMap, String charset, int connTimeout, int soTimeOut) throws IOException {
		CloseableHttpResponse response = null;
		try{
			if (url == null || url.isEmpty()) {
				return null;
			}
			charset = (charset == null ? CHAR_SET : charset);
			if(null != paramsMap && !paramsMap.isEmpty()) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> map : paramsMap.entrySet()) {
					params.add(new BasicNameValuePair(map.getKey(), map.getValue()));
				}
				String querystring = URLEncodedUtils.format(params, charset);
				if(url.contains("?")){
					url += "&" + querystring;
				}else{
					url += "?" + querystring;
				}
			}
			HttpGet httpGet = new HttpGet(url);
			// 设置请求和传输超时时间
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(soTimeOut).setConnectTimeout(connTimeout).build();
			httpGet.setConfig(requestConfig);
			httpGet.addHeader("Accept-Encoding", "*");
			response = getClient().execute(httpGet);
			//状态不为200的异常处理。
			int httpCode = response.getStatusLine().getStatusCode();
			if (httpCode != 200) {
				httpGet.abort();
				throw new RuntimeException("HttpClient,error status code :" + httpCode);
			}
			return EntityUtils.toString(response.getEntity(), charset);
		}catch (Exception e){
			throw e;
		}finally {
			if (response != null) {
				response.close();
			}
		}
	}

	/**
	 * HTTP Get 获取内容
	 *
	 * @param url
	 *            请求的url地址 ?之前的地址
	 * @param params
	 *            请求的参数
	 * @param headers
	 *            header头信息
	 * @return 页面内容
	 */
	public static String doGet(String url, Map<String, String> params, Map<String, String> headers) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		HttpGet httpGet = null;
		try {
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
				for (Map.Entry<String, String> entry : params.entrySet()) {
					String value = entry.getValue();
					if (value != null) {
						pairs.add(new BasicNameValuePair(entry.getKey(), value));
					}
				}
				url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, CHAR_SET));
			}
			httpGet = new HttpGet(url);

			// 设置header
			if (headers != null && !headers.isEmpty()) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					String value = entry.getValue();
					if (value != null) {
						httpGet.addHeader(entry.getKey(), value);
					}
				}
			}

			CloseableHttpResponse response = getClient().execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpGet.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, CHAR_SET);
			}
			EntityUtils.consume(entity);
			response.close();
			return result;
		} catch (Exception e) {
			if (httpGet != null) {
				httpGet.abort();
			}
			throw new RuntimeException(e);
		} finally {
			if (httpGet != null) {
				httpGet.releaseConnection();
			}
		}
	}

	public static String getQueryUrl(String url,Map<String, String> paramsMap, String charset) {
		if (url == null || url.isEmpty()) {
			return null;
		}
		String querystring = getQueryString(paramsMap, charset);
		if(url.contains("?")){
			url += "&" + querystring;
		}else{
			url += "?" + querystring;
		}
		return url;
	}
	public static String getQueryString(Map<String, String> paramsMap, String charset) {
		charset = (charset == null ? CHAR_SET : charset);
		if(null != paramsMap && !paramsMap.isEmpty()) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> map : paramsMap.entrySet()) {
				params.add(new BasicNameValuePair(map.getKey(), map.getValue()));
			}
			return URLEncodedUtils.format(params, charset);
		}
		return "";
	}

	public static HttpEntity getEntity(String url, Map<String, String> paramsMap, String charset) throws IOException{
		CloseableHttpResponse response = null;
		try{
			if (url == null || url.isEmpty()) {
				return null;
			}
			charset = (charset == null ? CHAR_SET : charset);
			if(null != paramsMap && !paramsMap.isEmpty()) {
				List<NameValuePair> params = new ArrayList<>();
				for (Map.Entry<String, String> map : paramsMap.entrySet()) {
					params.add(new BasicNameValuePair(map.getKey(), map.getValue()));
				}
				String querystring = URLEncodedUtils.format(params, charset);
				url += "?" + querystring;
			}
			HttpGet httpGet = new HttpGet(url);
			httpGet.addHeader("Accept-Encoding", "none");
			response = getClient().execute(httpGet);
			//状态不为200的异常处理。
			int httpCode = response.getStatusLine().getStatusCode();
			if (httpCode != 200) {
				httpGet.abort();
				throw new RuntimeException("HttpClient,error status code :" + httpCode);
			}
			return response.getEntity();
		}catch (Exception e){
			throw e;
		}/*finally {
			if (response != null) {
				response.close();
			}
		}*/
	}
	
	public static String post(String url, String request) throws IOException {
		return post(url, request, null);
	}
	
	public static String post(String url, String request, String charset) throws IOException {
		if (url == null || url.isEmpty()) {
			return null;
		}
		charset = (charset == null ? CHAR_SET : charset);
		CloseableHttpResponse response = null;
		String res = null;
		try {
			StringEntity entity = new StringEntity(request, charset);
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("Content-Type", "application/json;charset=utf-8");
			httpPost.addHeader("Accept-Encoding", "*");
			httpPost.setEntity(entity);
			response = getClient().execute(httpPost);
			//状态不为200的异常处理。
			int httpCode = response.getStatusLine().getStatusCode();
			if (httpCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + httpCode);
			}
			res = EntityUtils.toString(response.getEntity());
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return res;
	}
	
	public static String post(String url, Map<String, String> map) throws IOException {
		return post(url, map, null);
	}
	
	public static String post(String url, Map<String, String> paramsMap, String charset)
			throws IOException {
		if (url == null || url.isEmpty()) {
			return null;
		}
		charset = (charset == null ? CHAR_SET : charset);
		CloseableHttpResponse response = null;
		String res = null;
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> map : paramsMap.entrySet()) {
				params.add(new BasicNameValuePair(map.getKey(), map.getValue()));
			}
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, charset);
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("Accept-Encoding", "*");
			httpPost.setEntity(formEntity);
			response = getClient().execute(httpPost);
			res = EntityUtils.toString(response.getEntity());
			//状态不为200的异常处理。
			int httpCode = response.getStatusLine().getStatusCode();
			if (httpCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + httpCode);
			}
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return res;
	}

	public static String put(String url, Map<String, Object> paramsMap, String charset)
			throws IOException {
		if (url == null || url.isEmpty()) {
			return null;
		}
		charset = (charset == null ? CHAR_SET : charset);
		CloseableHttpResponse response = null;
		String res = null;
		try {
			List<NameValuePair> params = new ArrayList<>();
			for (Map.Entry<String, Object> map : paramsMap.entrySet()) {
				params.add(new BasicNameValuePair(map.getKey(), String.valueOf(map.getValue())));
			}
			String querystring = URLEncodedUtils.format(params, charset);
			url += "?" + querystring;
			HttpPut httpPut = new HttpPut(url);
			response = getClient().execute(httpPut);
			res = EntityUtils.toString(response.getEntity());
			//状态不为200的异常处理。
			int httpCode = response.getStatusLine().getStatusCode();
			if (httpCode != 200) {
				httpPut.abort();
				throw new RuntimeException("HttpClient,error status code :" + httpCode);
			}
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return res;
	}

	/**
	 * HTTP Post 获取内容
	 *
	 * @param url
	 *            请求的url地址
	 * @param jsonData
	 *            请求的json数据字符串
	 * @param headers
	 *            请求头
	 * @return 返回内容
	 */
	public static String postJson(String url, String jsonData, Map<String, String> headers) throws IOException {
		httpClient = getClient();
		CloseableHttpResponse response = null;
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost(url);

			// 设置header
			if (headers != null && !headers.isEmpty()) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					String value = entry.getValue();
					if (value != null) {
						httpPost.addHeader(entry.getKey(), value);
					}
				}
			}

			StringEntity s = new StringEntity(jsonData, "UTF-8");
			s.setContentEncoding("UTF-8");
			s.setContentType("application/json");
			httpPost.setEntity(s);
			response = httpClient.execute(httpPost);
			int httpCode = response.getStatusLine().getStatusCode();
			if (httpCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + httpCode);
			}
			String result = null;
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, CHAR_SET);
			}
			EntityUtils.consume(entity);
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally {
			if (response != null) {
				response.close();
			}
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}
	}

	/**
	 * HTTP Post 获取内容
	 *
	 * @param url
	 *            请求的url地址
	 * @param body
	 *            请求的json数据字符串
	 * @param headers
	 *            请求头
	 * @return 返回内容
	 */
	public static String postString(String url, String body, Map<String, String> headers) throws ClientProtocolException, IOException {
		httpClient = getClient();
		CloseableHttpResponse response = null;
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost(url);

			// 设置header
			if (headers != null && !headers.isEmpty()) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					String value = entry.getValue();
					if (value != null) {
						httpPost.addHeader(entry.getKey(), value);
					}
				}
			}

			StringEntity s = new StringEntity(body, "UTF-8");
			s.setContentEncoding("UTF-8");
			httpPost.setEntity(s);
			response = httpClient.execute(httpPost);
			int httpCode = response.getStatusLine().getStatusCode();
			if (httpCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + httpCode);
			}
			String result = null;
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, CHAR_SET);
			}
			EntityUtils.consume(entity);
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally {
			if (response != null) {
				response.close();
			}
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}
	}

	/**
	 * HTTP Post 字节流
	 *
	 * @param url
	 *            请求的url地址
	 * @param bytes
	 *            请求的字节流
	 * @param headers
	 *            请求头
	 * @return 返回内容
	 */
	public static String postByte(String url, byte[] bytes, Map<String, String> headers) throws ClientProtocolException, IOException {
		httpClient = getClient();
		CloseableHttpResponse response = null;
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost(url);

			// 设置header
			if (headers != null && !headers.isEmpty()) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					String value = entry.getValue();
					if (value != null) {
						httpPost.addHeader(entry.getKey(), value);
					}
				}
			}

			httpPost.setEntity(new ByteArrayEntity(bytes));
			response = httpClient.execute(httpPost);
			int httpCode = response.getStatusLine().getStatusCode();
			if (httpCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + httpCode);
			}
			String result = null;
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, CHAR_SET);
			}
			EntityUtils.consume(entity);
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally {
			if (response != null) {
				response.close();
			}
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}
	}

	public static byte[] postByte4Byte(String url, byte[] bytes, Map<String, String> headers) throws IOException {
		httpClient = getClient();
		CloseableHttpResponse response = null;
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost(url);
			// 设置header
			if (headers != null && !headers.isEmpty()) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					String value = entry.getValue();
					if (value != null) {
						httpPost.addHeader(entry.getKey(), value);
					}
				}
			}
			httpPost.setEntity(new ByteArrayEntity(bytes));
			response = httpClient.execute(httpPost);
			int httpCode = response.getStatusLine().getStatusCode();
			if (httpCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + httpCode);
			}
			byte[] bytesResponse = null;
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				bytesResponse = IOUtils.toByteArray(entity.getContent());
			}
			EntityUtils.consume(entity);
			return bytesResponse;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally {
			if (response != null) {
				response.close();
			}
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}
	}

	public static int getSocketTimeout() {
		return socketTimeout;
	}

	public static void setSocketTimeout(int socketTimeout) {
		HttpClientUtil.socketTimeout = socketTimeout;
	}

	public static int getConnectTimeout() {
		return connectTimeout;
	}

	public static void setConnectTimeout(int connectTimeout) {
		HttpClientUtil.connectTimeout = connectTimeout;
	}

	public static int getConnectionRequestTimeout() {
		return connectionRequestTimeout;
	}

	public static void setConnectionRequestTimeout(int connectionRequestTimeout) {
		HttpClientUtil.connectionRequestTimeout = connectionRequestTimeout;
	}

	public static byte[] getByte(String url, Map<String, String> paramsMap, String charset) throws IOException{
		CloseableHttpResponse response = null;
		HttpGet httpGet = null;
		ByteArrayOutputStream swapStream = null;
		try{
			if (url == null || url.isEmpty()) {
				return null;
			}
			charset = (charset == null ? CHAR_SET : charset);
			if(null != paramsMap && !paramsMap.isEmpty()) {
				List<NameValuePair> params = new ArrayList<>();
				for (Map.Entry<String, String> map : paramsMap.entrySet()) {
					params.add(new BasicNameValuePair(map.getKey(), map.getValue()));
				}
				String querystring = URLEncodedUtils.format(params, charset);
				url += "?" + querystring;
			}
			httpGet = new HttpGet(url);
			httpGet.addHeader("Accept-Encoding", "none");

			httpGet.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.79 Safari/537.1");
			httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			httpGet.setHeader("Content-Type", "text/html;charset=UTF-8");
//			httpGet.setHeader("version","HTTP/1.1");

			response = getClient().execute(httpGet);
			int httpCode = response.getStatusLine().getStatusCode();
			if (httpCode != 200) {
				httpGet.abort();
				throw new RuntimeException("HttpClient,error status code :" + httpCode);
			}
			swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[512];
			int rc = 0;
			while ((rc = response.getEntity().getContent().read(buff, 0, 512)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			byte[] in2b = swapStream.toByteArray();
			return in2b;
		}catch (Exception e){
			throw e;
		}finally {
			if (swapStream != null) {
				swapStream.close();
			}
			if (response != null) {
				response.close();
			}
			if (httpGet != null) {
				httpGet.releaseConnection();
			}
		}
	}

	/**
	 * 下载文件流
	 *
	 * @param url
	 * @throws IOException
	 */
	public static byte[] getFileBytes(String url) throws IOException {
		// 生成一个httpclient对象
		CloseableHttpClient httpclient = getClient();
		HttpGet httpget = null;
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		InputStream in = null;
		ByteArrayOutputStream baos = null;
		try {

			httpget = new HttpGet(url);
			response = httpclient.execute(httpget);
			//状态不为200的异常处理。
			int httpCode = response.getStatusLine().getStatusCode();
			if (httpCode != 200) {
				httpget.abort();
				throw new RuntimeException("HttpClient,error status code :" + httpCode);
			}
			entity = response.getEntity();
			in = entity.getContent();

			baos = new ByteArrayOutputStream();
			int l = -1;
			byte[] tmp = new byte[1024];
			while ((l = in.read(tmp)) != -1) {
				baos.write(tmp, 0, l);
			}
			return baos.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally {
			// 关闭低层流。
			if (in != null) {
				in.close();
			}
			if (in != null) {
				in.close();
			}

			if (response != null) {
				response.close();
			}
			if (httpget != null) {
				httpget.releaseConnection();
			}
		}
	}

	public static void main(String[] args){

/*		while (true){
			try {
				get("https://www.baidu.com");
				JSONObject.parseObject(get("https://api.kuyinyun.com/p/search_vr?a=d0b23f78c27e9078&w=%E5%B0%91%E5%B9%B4&ps=20&px=0&uid=testUid&tc=testTc"));
			}catch (Exception e){
				e.printStackTrace();
			}
		}*/
	}

}
