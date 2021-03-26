package com.miguan.recommend.service.recommend.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cgcg.context.util.StringUtils;
import com.miguan.recommend.bo.PublicInfo;
import com.miguan.recommend.common.config.EsConfig;
import com.miguan.recommend.common.constants.MongoConstants;
import com.miguan.recommend.common.constants.RedisCountConstant;
import com.miguan.recommend.common.es.EsDao;
import com.miguan.recommend.common.util.TimeUtil;
import com.miguan.recommend.entity.es.UserEmbeddingEs;
import com.miguan.recommend.entity.es.VideoEmbeddingEs;
import com.miguan.recommend.entity.mongo.AppsInfo;
import com.miguan.recommend.entity.mongo.UserEmbeddingLog;
import com.miguan.recommend.entity.mongo.VideoEmbeddingLog;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.service.BloomFilterService;
import com.miguan.recommend.service.impl.RedisDB11ServiceImpl;
import com.miguan.recommend.service.mongo.UserRawTagsService;
import com.miguan.recommend.service.mongo.VideoHotspotService;
import com.miguan.recommend.service.recommend.EmbeddingService;
import com.miguan.recommend.vo.RecVideosVo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tool.util.DateUtil;
import tool.util.StringUtil;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmbeddingServiceImpl implements EmbeddingService {
    @Resource
    private EsConfig esConfig;
    @Resource(name = "featureMongoTemplate")
    private MongoTemplate featureMongoTempLate;
    @Resource(name = "driveMongoTemplate")
    private MongoTemplate driveMongoTemplate;
    @Value("${spring.embedding.video-embedding}")
    private String videoEmbeddingUrl;
    @Value("${spring.embedding.user-embedding}")
    private String userEmbeddingUrl;
    @Resource
    private RedisDB11ServiceImpl redisDB11Service;
    @Resource
    private UserRawTagsService userRawTagsService;
    @Resource
    private EsDao esDao;
    @Resource
    private VideoHotspotService videoHotspotService;
    @Resource
    private BloomFilterService bloomFilterService;
    @Resource
    private RestTemplate restTemplate;
    @Value("${spring.python-img.get-img-vector}")
    private String imgVectorUrl;

    /**
     * 在视频新增、发布、更新时生成视频向量
     *
     * @param videoVo
     */
    public void videoEmbedding(RecVideosVo videoVo) {
        int status = 0;
        try {
            String url = videoEmbeddingUrl;
            url = url + "?video_id=" + videoVo.getId() + "&video_url=" + videoVo.getVideoUrl() + "&video_type=" + videoVo.getCatId();
            String restJson = restTemplate.postForObject(url, null, String.class);
            ;  //调用接口
            JSONObject jsonResult = JSONObject.parseObject(restJson);
            status = jsonResult.getInteger("status");  //结果：1成功/0失败
        } catch (Exception e) {
            log.error("调用视频向量接口异常", e);
            log.error("调用视频向量接口参数为video_id：{}，video_url：{}，video_type：{}", videoVo.getId(), videoVo.getVideoUrl(), videoVo.getCatId());
        }
        //把调用视频向量的结果存入日志中
        this.savevideoEmbeddingLog(videoVo.getId().intValue(), videoVo.getVideoUrl(), videoVo.getCatId().intValue(), status);
    }

    /**
     * 删除es中的视频向量
     *
     * @param videoId
     */
    public void deleteVideoEmbedding(String videoId) {
        esDao.deleteByQuery(esConfig.getVideo_embedding(), new TermQueryBuilder("video_id", videoId));
    }

    /**
     * 把调用视频向量的结果存入日志中
     *
     * @param videoId   视频id
     * @param videoUrl  视频url
     * @param videoType 视频分类id
     * @param status    结果：1成功/0失败
     */
    private void savevideoEmbeddingLog(Integer videoId, String videoUrl, Integer videoType, Integer status) {
        String time = DateUtil.dateStr4(new Date());
        VideoEmbeddingLog log = new VideoEmbeddingLog(videoId, videoUrl, videoType, status, time);
        this.featureMongoTempLate.save(log, MongoConstants.video_embedding_log);
    }

    /**
     * 调用用户向量接口
     *
     * @param publicInfo
     */
    @Override
    public List<String> userEmbedding(PublicInfo publicInfo) {
        int status = 0;  //1执行成功，0执行失败
        String embedding = "";  //接口调用结果
        String appVector = "";
        List<String> resultList = new ArrayList<>();
        String uuid = publicInfo.getUuid();
        String distinctId = publicInfo.getDistinctId();
        String channel = publicInfo.getChannel();
        String city = publicInfo.getGpscity();  //用户城市
        String model = publicInfo.getModel();  //用户机型
        boolean isNewApp = publicInfo.isNewApp();  //是否新老用户
        Integer topType = 0;  //用户观看最多视频类别
        String appList = "[]";
        String tagList = "";
        try {
            String key = String.format(RedisCountConstant.user_embedding, uuid);  //根据uuid做redis缓存
            String value = redisDB11Service.get(key);
            if (StringUtils.isNotBlank(value)) {
                //如果有缓存，则从缓存获取
                return JSON.parseObject(value, List.class);
            }
            if (isNewApp) {
                //新用户
                topType = getUserRealTopType(uuid);
            } else {
                //老用户
                topType = getUserOffLineTopType(uuid);
            }
            appList = findAppsList(distinctId); //用户安装app列表
            tagList = findTagList(uuid);
            String restJson = postUserEmb(uuid, distinctId, channel, city, model, topType, appList, tagList);
            JSONObject jsonResult = JSONObject.parseObject(restJson);
            status = jsonResult.getInteger("status");  //结果：1成功/0失败
            embedding = jsonResult.getString("embedding");
            appVector = jsonResult.getString("app_vector");
            if (status == 1) {
                //接口调用成功，把结果缓存到redis
                resultList.add(embedding);
                resultList.add(appVector);
                redisDB11Service.set(key, JSON.toJSONString(resultList), 24 * 60 * 60);  //缓存半天
            }
        } catch (Exception e) {
            resultList.add("");
            resultList.add("");
            log.error("调用视频向量接口异常", e);
            log.error("调用视频向量接口参数为uuid:{},distinctId:{},channel:{},city:{},model:{},topType:{},appList:{},tagList:{}", uuid, distinctId, channel, city, model, topType, appList, tagList);
        }
//        if (status == 0) {
            //调用失败，保存调用失败的日志
//            savevideoEmbeddingLog(uuid, distinctId, channel, city, model, topType, appList, status, embedding);
//        }
        return resultList;
    }

    /**
     * 请求userEmbedding接口
     *
     * @param uuid
     * @param distinctId
     * @param channel
     * @param city
     * @param model
     * @param topType
     * @param appList
     * @param tagList
     * @return
     */
    private String postUserEmb(String uuid, String distinctId, String channel, String city, String model, Integer topType, String appList, String tagList) {
        String url = userEmbeddingUrl + "?uuid={0}&distinct_id={1}&channel={2}&city={3}&model={4}&top_type={5}&app_list={6}&tag_list={7}";
        url = MessageFormat.format(url, uuid, distinctId, channel, city, model, String.valueOf(topType), appList, tagList);
        return restTemplate.postForObject(url, null, String.class);
    }

    /**
     * 获取用户最感兴趣的3个视频标签
     *
     * @param uuid
     * @return
     */
    private String findTagList(String uuid) {
        String key = String.format(RedisCountConstant.user_like_tag_pool, com.miguan.recommend.common.util.DateUtil.yyyy_MM_dd(), uuid);
        Map<String, String> allMap = redisDB11Service.hgetAll(key);
        //按照兴趣度取top3
        List list = allMap.entrySet().stream().sorted(Map.Entry.<String, String>comparingByValue().reversed()).limit(3)
                .map(entry -> entry.getKey()).collect(Collectors.toList());
        return JSON.toJSONString(list);
    }

    /**
     * 户观看最多视频类别，取实时数据
     *
     * @param uuid
     * @return
     */
    private Integer getUserRealTopType(String uuid) {
        String key = String.format(RedisCountConstant.user_cat_score, TimeUtil.getDayOfMonth(), uuid);
        Map<String, String> map = redisDB11Service.hgetAll(key);
        if (map == null) {
            return 0;
        }

        //找出分支最高的分类
        String topType = "";
        Integer maxSorce = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String type = entry.getKey();
            Integer sorce = Integer.parseInt(entry.getValue());
            if (sorce > maxSorce) {
                topType = type;
                maxSorce = sorce;
            }
        }
        return StringUtils.isBlank(topType) ? 0 : Integer.parseInt(topType);
    }


    /**
     * 户观看最多视频类别,取离线数据
     *
     * @param uuid
     * @return
     */
    private Integer getUserOffLineTopType(String uuid) {
        String key = RedisCountConstant.user_top_type + "uuid";
        String value = this.redisDB11Service.get(key);
        if (StringUtil.isNotBlank(value)) {
            return Integer.parseInt(value);
        } else {
            int topType = userRawTagsService.findTopType(uuid);
            redisDB11Service.set(key, String.valueOf(topType), 3 * 60 * 60);
            return topType;
        }
    }

    /**
     * 把调用用户向量的结果存入日志中
     */
    private void savevideoEmbeddingLog(String uuid, String distinctId, String channel, String city, String model, Integer topType, String appList,
                                       Integer status, String embedding) {
        String time = DateUtil.dateStr4(new Date());
        UserEmbeddingLog log = new UserEmbeddingLog(uuid, distinctId, channel, city, model, topType, appList, status, embedding, time);
        this.featureMongoTempLate.save(log, MongoConstants.user_embedding_log);
    }

    //获取用户手机应用的安装列表
    public String findAppsList(String distinctId) {

        Query query = new Query();
        query.addCriteria(Criteria.where("distinctId").is(distinctId));
        AppsInfo appsInfo = driveMongoTemplate.findOne(query, AppsInfo.class, "apps_info_list");
        if (appsInfo == null || appsInfo.getAppsList() == null) {
            return "[]";
        }
        JSONArray apps = JSONArray.parseArray(appsInfo.getAppsList().toJSONString());
        JSONArray appNames = new JSONArray();
        for (int i = 0; i < apps.size(); i++) {
            appNames.add(apps.getJSONObject(i).getString("appName"));
        }
        return appNames.toJSONString();
    }

    /**
     * 从es中获取具体的用户向量数组值
     *
     * @param uuid
     * @return
     */
    public String getUserEmbeddingVector(String uuid) {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(new TermQueryBuilder("user_id", uuid));
        System.out.println(builder.toString());
        List<UserEmbeddingEs> list = esDao.search(esConfig.getUser_embedding(), builder, UserEmbeddingEs.class);
        if (list == null || list.size() == 0) {
            return "";
        } else {
            return list.get(0).getVector();
        }
    }

    /**
     * 从es中获取视频向量
     *
     * @param videoId
     * @param videoUrl
     * @return
     */
    @Override
    @Cacheable(cacheNames = "video_embedding", cacheManager = "getCacheManager")
    public VideoEmbeddingEs getVideoEmbeddingVector(String videoId, String videoUrl) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.termQuery("video_id", videoId));
        boolQueryBuilder.must(QueryBuilders.termQuery("video_url", videoUrl));

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(boolQueryBuilder);
        List<VideoEmbeddingEs> list = esDao.search(esConfig.getVideo_embedding(), builder, VideoEmbeddingEs.class);
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    /**
     * 根据用户向量数据，在es中用余弦相似度函数查询出相似度最高的近800个视频
     *
     * @param vector
     * @return
     */
    @Override
    public List<String> findVideoByVideoEmbeddingVector(String vector) {
        List<String> videos = new ArrayList<>();
        if (StringUtil.isBlank(vector) || "[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]".equals(vector)) {
            return videos;
        }
        try {
            // 本地缓存
            // es查询和用户向量相似度高的视频
            String VideosJson = esDao.findVideoByVideoEmbeddingVector(vector);
            if(StringUtils.isNotEmpty(VideosJson)){
                JSONObject jsonObject = JSONObject.parseObject(VideosJson);
                JSONArray jsonArray = jsonObject.getJSONObject("hits").getJSONArray("hits");
                for(int i=0;i<jsonArray.size();i++) {
                    Long videoId = jsonArray.getJSONObject(i).getJSONObject("_source").getLong("video_id");
                    videos.add(String.valueOf(videoId));
                }
            } else {
                log.error("视频向量>>{},相似查询结果为空", vector);
            }
            return videos;
        } catch (Exception e) {
            log.error("解析视频向量异常", e);
            return videos;
        }
    }

    /**
     * 双塔向量视频召回
     *
     * @param publicInfo
     * @param alredayVideos 热门召回的视频集合
     * @return 双塔向量视频召回集合
     */
    @Override
    public List<VideoHotspotVo> findFromEsVideoEmbedding(PublicInfo publicInfo, List<VideoHotspotVo> alredayVideos) {
        long startTime = System.currentTimeMillis();
        int needVideoNum = 300; //需要从双塔向量召回的视频数
        List<String> excludeVideos = alredayVideos.stream().map(VideoHotspotVo::getVideo_id).collect(Collectors.toList()); //热门召回的视频id（需要从向量召回的视频中排除）
        String userVector = this.userEmbedding(publicInfo).get(0);  //调用用户向量接口生成用户向量，并返回用户向量
        List<String> videoEsEmbedding = this.findVideoByVideoEmbeddingVector(userVector);  //根据用户向量数据，在es中用余弦相似度函数查询出相似度最高的近800个视频
        videoEsEmbedding.removeAll(excludeVideos);  //移除热门召回的视频
        if (videoEsEmbedding.isEmpty()) {
            return new ArrayList<>();
        }

        // 在布隆过滤器中过滤已曝光过的视频
        List<String> passVideoIds = new ArrayList<>();
        int queryListSize = videoEsEmbedding.size();
        int step = 100;
        int toIndex = Math.min(100, videoEsEmbedding.size());
        int start = 0;
        int initNeedCount = needVideoNum;
        do {
            toIndex = Math.min(start + toIndex, queryListSize);
            passVideoIds.addAll(bloomFilterService.containMuil(initNeedCount, publicInfo.getUuid(), videoEsEmbedding.subList(start, toIndex)));
            initNeedCount = needVideoNum - passVideoIds.size();
            start = start + step;
            step = Math.min(step * 2, queryListSize - start);
            toIndex = start + step;

        } while (initNeedCount > 0 && start < queryListSize);

        if (passVideoIds.isEmpty()) {
            return new ArrayList<>();
        }
        //根据视频id从mongodb中查询出对应的视频信息
        List<VideoHotspotVo> videoList = videoHotspotService.findFromMongoById(passVideoIds, null);
        long endTime = System.currentTimeMillis();
        log.info("双塔向量视频召回，方法消耗时间：{},es视频数：{}，召回视频数：{}", (endTime - startTime) / 1000, videoEsEmbedding.size(), videoList.size());
        return videoList;
    }

    /**
     * ES新增或更新图像特征向量
     * @param videoId
     * @param title
     * @param imgUrl 视频背景图片url
     */
    public void saveEsImgVector(Integer videoId, String title, String imgUrl) {
        try {
            log.info("ES新增或更新图像特征向量,videoId:{}", videoId);
            String imgVector = getImgVector(imgUrl);  //获取图像特征向量
            if(this.isCanSave(imgVector)) {
                esDao.saveEsImgVector(videoId, title, imgUrl, imgVector);
            }
        } catch (Exception e) {
            log.error("ES新增或更新图像特征向量异常,videoId:{}", videoId);
        }
    }

    private boolean isCanSave(String imgVector) {
        try {
            if(StringUtil.isBlank(imgVector)) {
                return false;
            }
            imgVector = imgVector.replace("[", "").replace("]", "");
            String[] array = imgVector.split(",");
            long total = 0;
            for(int i=0;i<array.length;i++) {
                total+=Long.parseLong(array[i]);
            }
            if(total == 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("判断是否插入es错误,imgVector:{}", imgVector);
            return false;
        }
    }

    /**
     * 调用生成图片向量接口
     *
     * @param imgUrl 图像的在线url
     * @return
     */
    private String getImgVector(String imgUrl) {
        try {
            String url = imgVectorUrl + "?imgUrl=" + imgUrl;
            return restTemplate.postForObject(url, null, String.class);
        } catch (Exception e) {
            log.error("调用生成图片向量接口异常", e);
            return "";
        }
    }
}
