package com.miguan.recommend.controller;

import com.miguan.recommend.common.constants.RabbitMqConstants;
import com.miguan.recommend.common.constants.SymbolConstants;
import com.miguan.recommend.common.es.EsDao;
import com.miguan.recommend.service.BloomFilterService;
import com.miguan.recommend.service.ck.DwVideoActionService;
import com.miguan.recommend.service.recommend.EmbeddingService;
import com.miguan.recommend.task.CatHotspotTask;
import com.miguan.recommend.task.RedisDelTask;
import com.miguan.recommend.task.TopVideoTask;
import com.miguan.recommend.vo.RecVideosVo;
import com.miguan.recommend.vo.ResultMap;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Resource
    private DwVideoActionService dwVideoActionService;
    @Resource
    private CatHotspotTask catHotspotTask;
    @Resource
    private BloomFilterService bloomFilterService;
    @Resource
    private TopVideoTask topVideoTask;
    @Resource
    private RedisDelTask redisDelTask;

    @PostMapping("/delRedisKey")
    public ResultMap delRedisKey() throws InterruptedException {
        Thread thread = new Thread(() -> {
                try{
                    redisDelTask.delKeys();
                } catch (Exception e){

                }
        });
        thread.start();
        return ResultMap.success();
    }

    @PostMapping("/topVideo")
    public ResultMap initTopVideo(@RequestParam("date") Integer date){
        List<String> allCatTopVideo = topVideoTask.setTopVideoToRedis(date);
        Map<String, List<String>> everyCatTopVideo = topVideoTask.setTopVideoToRedis4EveryCat(date);
        everyCatTopVideo.put("all", allCatTopVideo);
        return ResultMap.success(everyCatTopVideo);
    }

    @GetMapping
    public ResultMap test() {
        return ResultMap.success();
    }

    @GetMapping("/similarCat")
    public ResultMap similarCat(@RequestParam("date") String date, @RequestParam("catid") String catid) {
        return ResultMap.error(dwVideoActionService.findSimilarCatid(date, catid));
    }

    @GetMapping("/similarCat/init")
    public ResultMap similarCatInit() {
        catHotspotTask.initCatHotspot();
        return ResultMap.success();
    }

    @PostMapping("/isBloom")
    public ResultMap isBloom(@RequestParam("uuid") String uuid, @RequestParam("videoIds") String videoIds) {
        List<String> videoList = Arrays.asList(videoIds.split(SymbolConstants.comma));
        Map<String, List<String>> result = bloomFilterService.containMuil(uuid, videoList);
        return ResultMap.success(result);
    }

    @Resource
    private EmbeddingService embeddingService;

    @PostMapping("/videoEmbeddingTest")
    public void videoEmbeddingTest() {
        String aa = embeddingService.findAppsList("02e6e14adf564e524d0c18d4dd4bbac5");
        RecVideosVo videoVo = new RecVideosVo();
        videoVo.setId(122L);
        videoVo.setVideoUrl("http://xiyou.sc.diyixin.com/pro-video-xiyou/20191023/15717561966123.mp4");
        videoVo.setCatId(1L);
        embeddingService.videoEmbedding(videoVo);
    }

    @Resource
    private EsDao esDao;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/esTest")
    public void esTest() throws Exception{
        rabbitTemplate.convertAndSend(RabbitMqConstants.VIDEO_REC_EXCHANGE, RabbitMqConstants.VIDEO_REC_KEY, "videoUpdate@4312");

//        Typ4 typ1 = new Typ4();
//        typ1.setId(5L);
//        typ1.setTitle("测试1255");
//        typ1.setCatId(345L);
//
//        EsEntity<Typ4> entity = new EsEntity<>(typ1.getId().toString(), typ1);
//        esDao.insertOrUpdateOne("typ4", entity);
////
//        SearchSourceBuilder builder = new SearchSourceBuilder();
//        builder.query(new TermQueryBuilder("user_id", "1253"));
//        System.out.println(builder.toString());
//        List<UserEmbeddingEs> list = esDao.search("user_embedding_test", builder, UserEmbeddingEs.class);
//        System.out.println(list);

//        String rs = HttpUtil.doPost("http://192.168.100.10:19200/test-index1/_search", query);
//        System.out.println(1);

//        VideoEmbeddingEs es = embeddingService.getVideoEmbeddingVector("6667", "www.baid.com");
//        System.out.println(es);

//        RecVideosVo videoVo = new RecVideosVo();
//        videoVo.setId(4318L);
//        videoVo.setVideoUrl("http://xiyou.sc.diyixin.com/dev-video-xiyou/20190814/15657783221446.mp4");
//        videoVo.setCatId(1002L);
//        embeddingService.videoEmbedding(videoVo);

//        SearchSourceBuilder builder1 = new SearchSourceBuilder();
//        List<TestIndex> list2 = esDao.search("test-index1", builder1, TestIndex.class);
//        System.out.println(1);
    }

    public static String query = "{\n" +
            "  \"from\" : 0, \n" +
            "  \"size\" : 800,\n" +
            "  \"query\": {\n" +
            "    \"script_score\": {\n" +
            "      \"query\": {\n" +
            "        \"match_all\": {}\n" +
            "      },\n" +
            "      \"script\": {\n" +
            "        \"source\": \"cosineSimilarity(params.queryVector, doc['my_vector'])+1.0\",\n" +
            "        \"params\": {\n" +
            "          \"queryVector\": [0.5, 0.0101, 0.6, 0.756]\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
}
