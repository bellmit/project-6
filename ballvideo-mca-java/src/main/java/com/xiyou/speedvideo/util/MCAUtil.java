package com.xiyou.speedvideo.util;

import com.alibaba.fastjson.JSON;
import com.baidubce.BceClientConfiguration;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.vca.VcaClient;
import com.baidubce.services.vca.model.*;
import com.xiyou.speedvideo.entity.FirstVideosMcaResult;
import com.xiyou.speedvideo.entity.mongo.VideoMcaResult;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * description:百度MCA工具类
 *
 * @author huangjx
 * @date 2020/10/15 9:02 下午
 */
@Component
public class MCAUtil {

    /**
     * 百度AI控制台accessKey
     */
    private static String accessKey;
    /**
     * 百度AI控制台accessKey
     */
    private static String secretKey;

    /**
     * 解析模板
     */
    private static String preset;
    /**
     * 通知名称
     */
    private static String notification;

    private static volatile VcaClient vcaClient = null;

    @Value("${baidu.accessKey}")
    public void setAccessKey(String accessKey) {
        MCAUtil.accessKey= accessKey;
    }

    @Value("${baidu.secretKey}")
    public void setSecretKey(String secretKey) {
        MCAUtil.secretKey= secretKey;
    }

    @Value("${baidu.preset}")
    public void setPreset(String preset) {
        MCAUtil.preset= preset;
    }

    @Value("${baidu.notification}")
    public void setNotification(String notification) {
        MCAUtil.notification= notification;
    }

    private static VcaClient getVcaClient(){
        if(vcaClient == null){
            synchronized(MCAUtil.class){
                if(vcaClient == null){
                    try{
                        //读取配置
                        Properties properties = new Properties();
                        // 使用ClassLoader加载properties配置文件生成对应的输入流
                        InputStream in = MCAUtil.class.getClassLoader().getResourceAsStream("mca.properties");
                        // 使用properties对象加载输入流
                        properties.load(in);
                        //获取key对应的value值
                        accessKey = properties.getProperty("accessKey");
                        secretKey = properties.getProperty("secretKey");
                        preset = properties.getProperty("preset");
                        notification = properties.getProperty("notification");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    // 初始化一个VcaClient
                    BceClientConfiguration config = new BceClientConfiguration();
                    config.setCredentials(new DefaultBceCredentials(accessKey, secretKey));
                    // 如果有需要，可以用https协议
                    config.setEndpoint("http://vca.bj.baidubce.com");
                    vcaClient = new VcaClient(config);
                }
            }
        }
        return vcaClient;
    }

    /**
     * 提交视频解析
     * @param source 视频url
     */
    public static AnalyzeResponse analyzeMedia(String source) {
        AnalyzeRequest request = new AnalyzeRequest();
        request.setSource(source);
        request.setPreset(preset);
        //通知名称
        request.setNotification(notification);
        return getVcaClient().analyze(request);
    }

    /**
     * 获取视频解析结果
     * @param source
     */
    public static void queryResult(String source) {
        QueryResultResponse response = getVcaClient().queryResult(source);
        String status = response.getStatus();
        if ("FINISHED".equals(status)) {
            for (TagsResult tagsResult: response.getResults()) {
                String type = tagsResult.getType();
                System.out.println(type);
                for (ResultItem item: tagsResult.getResult()) {
                    String attribute = item.getAttribute();
                    Double confidence = item.getConfidence();
                    String source1 = item.getSource();
                    List<ResultItem.TimeInSeconds> time = item.getTime();
                }
            }
        }
    }

    /**
     * 解析单条
     * @param result
     */
    public static void parsingResult(String result) {
        QueryResultResponse response = JSON.parseObject(result,QueryResultResponse.class);
        String status = response.getStatus();
        if ("FINISHED".equals(status)) {
            for (TagsResult tagsResult: response.getResults()) {
                String type = tagsResult.getType();
                System.out.println(type);
                for (ResultItem item: tagsResult.getResult()) {
                    String attribute = item.getAttribute();
                    System.out.println(attribute);
                }
            }
        }
    }

    /**
     * 解析出存在mongodb的result
     * @param result
     * @return
     */
    public static VideoMcaResult parsingMongoResult(String result) {
        VideoMcaResult videoMcaResult = new VideoMcaResult();
        QueryResultResponse response = JSON.parseObject(result,QueryResultResponse.class);
        String status = response.getStatus();
        if ("FINISHED".equals(status)) {
            videoMcaResult.setResults(response.getResults());
            List<String> tagList = new ArrayList<>();
            for (TagsResult tagsResult: response.getResults()) {
                String type = tagsResult.getType();
//                System.out.println(type);
                if(!"keyword".equals(type)){
                    if("scenario".equals(type)){
                        videoMcaResult.setPriorityLabel(tagsResult.getResult().get(0).getAttribute());
                    }
                    for (ResultItem item: tagsResult.getResult()) {
                        String attribute = item.getAttribute();
//                        System.out.println(attribute);
                        tagList.add(attribute);
                    }
                }
            }
            videoMcaResult.setLabelList(tagList);
        }
        return videoMcaResult;
    }

    public static void main(String[] args){
        String param = "{\"taskId\":\"kjvw83w9asvhfhhfnt8\",\"source\":\"https://ss.bscstorage.com/xiyou-huangjunxian/speed-video/9263-15668698874753-4x.mp4\",\"description\":\"\",\"preset\":\"temp1\",\"notification\":\"xlearn\",\"status\":\"FINISHED\",\"percent\":100,\"results\":[{\"type\":\"entity\",\"result\":[{\"attribute\":\"秒速五厘米\",\"confidence\":89.3,\"source\":\"object_detect\",\"time\":[{\"start\":34,\"end\":34}]},{\"attribute\":\"人物-新闻主播\",\"confidence\":86.59,\"source\":\"image_classify\",\"time\":[{\"start\":2,\"end\":5}]},{\"attribute\":\"人物特写\",\"confidence\":85.7,\"source\":\"object_detect\",\"time\":[{\"start\":1,\"end\":3},{\"start\":5,\"end\":5}]}]},{\"type\":\"keyword\",\"result\":[{\"attribute\":\"你怎么了\",\"confidence\":100.0,\"source\":\"speech\",\"time\":[{\"start\":55,\"end\":60}]},{\"attribute\":\"兴国县\",\"confidence\":100.0,\"source\":\"character\",\"time\":[{\"start\":3,\"end\":11},{\"start\":19,\"end\":22}]},{\"attribute\":\"操你妈\",\"confidence\":72.87,\"source\":\"speech\",\"time\":[{\"start\":38,\"end\":46}]},{\"attribute\":\"八点半\",\"confidence\":11.75,\"source\":\"character\",\"time\":[{\"start\":55,\"end\":55}]}]},{\"type\":\"scenario\",\"result\":[{\"attribute\":\"灾难事故\",\"confidence\":46.3,\"source\":\"scenario_classify\",\"time\":[{\"start\":0,\"end\":61}]}]}],\"createTime\":\"2020-10-20T12:15:59Z\",\"startTime\":\"2020-10-20T12:16:11Z\",\"durationInSecond\":61,\"publishTime\":\"2020-10-20T12:17:16Z\"}";
        VideoMcaResult videoMcaResult =  MCAUtil.parsingMongoResult(param);
        System.out.println(JSON.toJSONString(videoMcaResult));
    }

}
