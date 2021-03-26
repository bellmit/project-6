package com.xiyou.speedvideo;

import com.alibaba.fastjson.JSON;
import com.xiyou.speedvideo.dao.VideoMcaResultDao;
import com.xiyou.speedvideo.entity.mongo.VideoMcaResult;
import com.xiyou.speedvideo.util.MCAUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class SpeedVideoApplicationTests {

    @Autowired
    private VideoMcaResultDao videoMcaResultDao;

    @Test
    void contextLoads() {
    }

    @Test
    void saveTest() {
//		VideoMcaResult result = new VideoMcaResult();
//		result.setVideoId(1234567L);
//		result.setPriorityLabel("TestPriorityLabel");
//		List<String> labelList = new ArrayList();
//		labelList.add("testLabel");
//		labelList.add("testLabel1");
//		labelList.add("testLabel2");
//		result.setLabelList(labelList);
//		videoMcaResultDao.saveVideoMcaResult(result);
        String param = "{\"taskId\":\"kjvw83w9asvhfhhfnt8\",\"source\":\"https://ss.bscstorage.com/xiyou-huangjunxian/speed-video/9263-15668698874753-4x.mp4\",\"description\":\"\",\"preset\":\"temp1\",\"notification\":\"xlearn\",\"status\":\"FINISHED\",\"percent\":100,\"results\":[{\"type\":\"entity\",\"result\":[{\"attribute\":\"秒速五厘米\",\"confidence\":89.3,\"source\":\"object_detect\",\"time\":[{\"start\":34,\"end\":34}]},{\"attribute\":\"人物-新闻主播\",\"confidence\":86.59,\"source\":\"image_classify\",\"time\":[{\"start\":2,\"end\":5}]},{\"attribute\":\"人物特写\",\"confidence\":85.7,\"source\":\"object_detect\",\"time\":[{\"start\":1,\"end\":3},{\"start\":5,\"end\":5}]}]},{\"type\":\"keyword\",\"result\":[{\"attribute\":\"你怎么了\",\"confidence\":100.0,\"source\":\"speech\",\"time\":[{\"start\":55,\"end\":60}]},{\"attribute\":\"兴国县\",\"confidence\":100.0,\"source\":\"character\",\"time\":[{\"start\":3,\"end\":11},{\"start\":19,\"end\":22}]},{\"attribute\":\"操你妈\",\"confidence\":72.87,\"source\":\"speech\",\"time\":[{\"start\":38,\"end\":46}]},{\"attribute\":\"八点半\",\"confidence\":11.75,\"source\":\"character\",\"time\":[{\"start\":55,\"end\":55}]}]},{\"type\":\"scenario\",\"result\":[{\"attribute\":\"灾难事故\",\"confidence\":46.3,\"source\":\"scenario_classify\",\"time\":[{\"start\":0,\"end\":61}]}]}],\"createTime\":\"2020-10-20T12:15:59Z\",\"startTime\":\"2020-10-20T12:16:11Z\",\"durationInSecond\":61,\"publishTime\":\"2020-10-20T12:17:16Z\"}";
        VideoMcaResult videoMcaResult = MCAUtil.parsingMongoResult(param);
        videoMcaResult.setVideoId(1234567);
        videoMcaResultDao.saveVideoMcaResult(videoMcaResult);

    }

    @Test
    void removeTest() {
        videoMcaResultDao.removeByVideoId(1234567);
    }

    @Test
    void searchTest() {
        VideoMcaResult result = videoMcaResultDao.findByVideoId(4318);
        System.out.println(JSON.toJSON(result));
    }

    @Test
    void updateTest() {
        VideoMcaResult result = videoMcaResultDao.findByVideoId(1234567);
        List<String> resultList = new ArrayList();
        resultList.add("asdfgzxcv");
        resultList.add("zxcvadvzx");
        result.setResults(resultList);
        System.out.println(JSON.toJSON(result));
        videoMcaResultDao.updateByVideoId(result);
    }

}
