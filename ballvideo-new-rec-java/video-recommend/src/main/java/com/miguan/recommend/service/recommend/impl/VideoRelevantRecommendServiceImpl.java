package com.miguan.recommend.service.recommend.impl;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.bo.BaseDto;
import com.miguan.recommend.bo.VideoRelavantRecommendDto;
import com.miguan.recommend.common.config.EsConfig;
import com.miguan.recommend.common.constants.ExistConstants;
import com.miguan.recommend.common.constants.MongoConstants;
import com.miguan.recommend.common.constants.RedisRecommendConstants;
import com.miguan.recommend.common.constants.SymbolConstants;
import com.miguan.recommend.common.es.EsDao;
import com.miguan.recommend.entity.es.HotspotVideos;
import com.miguan.recommend.entity.es.VideoEmbeddingEs;
import com.miguan.recommend.entity.mongo.FullLable;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.service.BloomFilterService;
import com.miguan.recommend.service.RedisService;
import com.miguan.recommend.service.es.EsSearchService;
import com.miguan.recommend.service.recommend.EmbeddingService;
import com.miguan.recommend.service.recommend.VideoRecommendService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service("videoRelevantRecommendService")
public class VideoRelevantRecommendServiceImpl implements VideoRecommendService<VideoRelavantRecommendDto> {

    @Resource(name = "logMongoTemplate")
    private MongoTemplate logMongoTemplate;
    @Resource(name = "redisDB0Service")
    private RedisService redisDB0Service;
    @Resource
    private EsConfig esConfig;
    @Resource
    private EsDao esDao;
    @Resource
    private EmbeddingService embeddingService;
    @Resource
    private EsSearchService esSearchService;
    @Resource
    private BloomFilterService bloomFilterService;

    @Override
    public void recommend(BaseDto baseDto, VideoRelavantRecommendDto recommendDto) {
        List<String> relevantVideos = null;
        long t1 = System.currentTimeMillis();
        switch (baseDto.getRelevantGroup()) {
            case "2":
                // 视频向量相关推荐
                relevantVideos = this.recommendByVideoEmbedding(baseDto, recommendDto);
                log.info("{} 即时相关推荐 向量相关视频ID：{}", baseDto.getUuid(), JSONObject.toJSONString(relevantVideos));
                break;
            case "3":
                // 百度飞桨标签相关推荐
                relevantVideos = this.recommendByPaddleTag(baseDto, recommendDto);
                log.info("{} 即时相关推荐 标签相关视频ID：{}", baseDto.getUuid(), JSONObject.toJSONString(relevantVideos));
                break;
            default:
                // 视频标题相关推荐
                relevantVideos = this.recommendByVideoTitle(baseDto, recommendDto);
                log.info("{} 即时相关推荐 标题相关视频ID：{}", baseDto.getUuid(), JSONObject.toJSONString(relevantVideos));
        }
        log.info("{} 即时相关推荐 总耗时：{}", baseDto.getUuid(), System.currentTimeMillis() - t1);
        recommendDto.setRecommendVideo(relevantVideos);
        bloomFilterService.putAll(baseDto.getUuid(), relevantVideos);
    }

    private List<String> recommendByVideoTitle(BaseDto baseDto, VideoRelavantRecommendDto recommendDto) {

        List<String> relevantVideos = null;
        String videoTitle = this.getVideoTitle(recommendDto.getVideoId());
        if (StringUtils.isEmpty(videoTitle)) {
            log.info("{} 即时相关推荐 视频：{}，未获取到标题", baseDto.getUuid(), recommendDto.getVideoId());
        } else {
            relevantVideos = esSearchService.relevantVideoOfTitle(videoTitle);
            if (isEmpty(relevantVideos)) {
                log.info("{} 即时相关推荐 视频：{}，获取标题相关视频0个", baseDto.getUuid(), recommendDto.getVideoId());
            } else {
                relevantVideos.remove(recommendDto.getVideoId());
                log.info("{} 即时相关推荐 视频：{}，获取标题相关视频{}个", baseDto.getUuid(), recommendDto.getVideoId(), relevantVideos.size());
                relevantVideos = bloomFilterService.containMuilSplit(recommendDto.getNum(), baseDto.getUuid(), relevantVideos);
            }
        }
        return relevantVideos;
    }

    private String getVideoTitle(String videoId){
        String key = RedisRecommendConstants.video_title + videoId;
        String title = redisDB0Service.get(key);
        if(StringUtils.isEmpty(title)){
            SearchSourceBuilder idBuilder = new SearchSourceBuilder();
            idBuilder.query(new TermQueryBuilder("id", videoId));
            List<HotspotVideos> videosList = esDao.search(esConfig.getVideo_title(), idBuilder, HotspotVideos.class);
            if (!isEmpty(videosList)) {
                title = videosList.get(0).getTitle();
                redisDB0Service.set(key, title, ExistConstants.one_hour_seconds);
            }
        }
        return title;
    }

    /**
     * 根据视频向量推荐
     *
     * @param baseDto
     * @param recommendDto
     * @return
     */
    private List<String> recommendByVideoEmbedding(BaseDto baseDto, VideoRelavantRecommendDto recommendDto) {
        String vector = this.getVideoEmbeddingVector(baseDto.getUuid(), recommendDto.getVideoId());
        log.info("{} 即时相关推荐 视频：{}，向量：{}", baseDto.getUuid(), recommendDto.getVideoId(), vector);
        // 通过向量获取相关视频集合
        List<String> relevantVideos = embeddingService.findVideoByVideoEmbeddingVector(vector);
        if(isEmpty(relevantVideos)){
            log.info("{} 即时相关推荐 视频：{}，获取向量相关0个视频", baseDto.getUuid(), recommendDto.getVideoId());
            return null;
        }
        relevantVideos.remove(recommendDto.getVideoId());
        log.info("{} 即时相关推荐 视频：{}，获取向量相关{}个视频", baseDto.getUuid(), recommendDto.getVideoId(), relevantVideos.size());
        if(isEmpty(relevantVideos)){
            return null;
        }
        return bloomFilterService.containMuilSplit(recommendDto.getNum(), baseDto.getUuid(), relevantVideos);
    }

    /**
     * 获取视频向量
     *
     * @param videoId 视频ID
     * @return
     */
    public String getVideoEmbeddingVector(String uuid, String videoId) {
        String key = RedisRecommendConstants.video_embedding_vector + videoId;
        String value = redisDB0Service.get(key);
        if (StringUtils.isEmpty(value)) {
            Query query = new Query();
            query.addCriteria(Criteria.where("video_id").is(videoId));
            List<VideoHotspotVo> videoVo = logMongoTemplate.find(query, VideoHotspotVo.class, MongoConstants.video_hotspot);
            if(CollectionUtils.isEmpty(videoVo)){
                return null;
            }
            String videoUrl = videoVo.get(0).getVideo_url();
            if(StringUtils.isEmpty(videoUrl)){
                log.error("{} 即时相关推荐 视频：{}，地址为空", uuid, videoId);
                return null;
            }
            // 获取视频向量
            VideoEmbeddingEs embeddingEs = embeddingService.getVideoEmbeddingVector(videoId, videoUrl);
            if (embeddingEs == null || StringUtils.isEmpty(embeddingEs.getVector())) {
                return null;
            }
            value = embeddingEs.getVector();
            redisDB0Service.set(key, value, ExistConstants.five_minutes_seconds);
        }
        return value;
    }

    /**
     * 根据百度飞桨标签推荐
     *
     * @param baseDto
     * @param recommendDto
     * @return
     */
    private List<String> recommendByPaddleTag(BaseDto baseDto, VideoRelavantRecommendDto recommendDto) {
        // 获取视频置信度前3的标签ID
        List<Integer> top3Ids = this.findVideoTop3Ids(recommendDto.getVideoId());
        if(isEmpty(top3Ids)){
            log.info("{} 即时相关推荐 视频：{}, 未获取到标签，直接返回空", baseDto.getUuid(), recommendDto.getVideoId());
            return null;
        }
        log.info("{} 即时相关推荐 视频：{}, 标签ID：{}", baseDto.getUuid(), recommendDto.getVideoId(), JSONObject.toJSONString(top3Ids));
        List<String> relevantVideos = this.findRelevantVideoInTop5Ids(top3Ids, recommendDto.getSensitiveState());
        if (isEmpty(relevantVideos)) {
            log.info("{} 即时相关推荐 视频：{}, 获取标签相关视频0个", baseDto.getUuid(), recommendDto.getVideoId());
            return null;
        }
        relevantVideos.remove(recommendDto.getVideoId());
        log.info("{} 即时相关推荐 视频：{}, 获取标签相关视频{}个", baseDto.getUuid(), recommendDto.getVideoId(), relevantVideos.size());
        if(isEmpty(relevantVideos)){
            return null;
        }
        return bloomFilterService.containMuilSplit(recommendDto.getNum(), baseDto.getUuid(), relevantVideos);
    }

    /**
     * 标签置信度求和
     *
     * @param videoHotspotVo 视频实体对象
     * @param tagIds         指定求和的标签ID
     * @return
     */
    private Double sumTagProbability(VideoHotspotVo videoHotspotVo, List<Integer> tagIds) {
        double sumProbability = 0.0D;
        List<FullLable> videoFullTag = videoHotspotVo.getTop5_ids();
        Map<Integer, Double> tagProbabilityMap = videoFullTag.stream().collect(Collectors.toMap(FullLable::getClass_id, FullLable::getProbability));
        for (Integer tagId : tagIds) {
            double tagProbability = tagProbabilityMap.get(tagId);
            sumProbability += tagProbability;
        }
        return sumProbability;
    }

    /**
     * 查询置信度前5的标签,包含指定标签ID的视频
     *
     * @param top5Ids   置信度前5的标签
     * @param sensitive 0 无限制，1 非敏感视频
     * @return
     */
    private List<String> findRelevantVideoInTop5Ids(List<Integer> top5Ids, Integer sensitive) {

        List<String> relevantVideos = null;

        String key = RedisRecommendConstants.relevant_video_of_tag3_ids + StringUtils.collectionToDelimitedString(top5Ids, SymbolConstants.comma);
        String value = redisDB0Service.get(key);
        if (StringUtils.isEmpty(value)) {
            // 查询出置信度前5的标签，包含目标视频前3标签的视频
            Query relevantQuery = new Query();
            relevantQuery.addCriteria(Criteria.where("state").is(1));
            relevantQuery.addCriteria(Criteria.where("top5_ids.'class_id").all(top5Ids));
            if (sensitive != null && sensitive == 1) {
                relevantQuery.addCriteria(Criteria.where("sensitive").is(-2));

            }
            log.debug("相关推荐[标签]，相关视频查询语句：{}", relevantQuery.toString());
            List<VideoHotspotVo> relevantList = logMongoTemplate.find(relevantQuery, VideoHotspotVo.class, MongoConstants.video_hotspot);
            if (isEmpty(relevantList)) {
                log.debug("相关推荐[标签]，未查询标签{}的相关视频，直接返回空", JSONObject.toJSONString(top5Ids));
                return null;
            }

            // 根据标签的置信度和，进行倒序排序
            relevantVideos = relevantList.stream().sorted((v1, v2) -> {
                return this.sumTagProbability(v2, top5Ids).compareTo(this.sumTagProbability(v1, top5Ids));
            }).map(VideoHotspotVo::getVideo_id).collect(Collectors.toList());

            value = StringUtils.collectionToDelimitedString(relevantVideos, SymbolConstants.comma);
            redisDB0Service.set(key, value, ExistConstants.one_hour_seconds);

        } else {
            relevantVideos = Stream.of(value.split(SymbolConstants.comma)).collect(Collectors.toList());
        }
        log.debug("相关推荐[标签]，查询到的相关视频：{}", JSONObject.toJSONString(relevantVideos));
        return relevantVideos;
    }

    /**
     * 查询视频置信度前3的标签ID
     *
     * @param videoId 视频ID
     * @return
     */
    private List<Integer> findVideoTop3Ids(String videoId) {
        String key = RedisRecommendConstants.video_paddle_tag3_ids + videoId;
        String value = redisDB0Service.get(key);
        List<Integer> top3Ids = null;
        if (StringUtils.isEmpty(value)) {
            // 根据目标视频ID，查询视频置信度前3的标签
            Query query = new Query();
            query.addCriteria(Criteria.where("video_id").is(videoId));
            VideoHotspotVo videoHotspotVo = logMongoTemplate.findOne(query, VideoHotspotVo.class, MongoConstants.video_hotspot);
            if (videoHotspotVo == null || isEmpty(videoHotspotVo.getTop5_ids())) {
                log.debug("相关推荐[标签]，未查询到视频[{}]的标签，直接返回空", videoId);
                return null;
            }
            top3Ids = videoHotspotVo.getTop5_ids().stream().limit(3L).map(FullLable::getClass_id).sorted().collect(Collectors.toList());
            value = StringUtils.collectionToDelimitedString(top3Ids, SymbolConstants.comma);
            redisDB0Service.set(key, value, ExistConstants.one_hour_seconds);
        } else {
            top3Ids = Stream.of(value.split(SymbolConstants.comma)).map(Integer::new).collect(Collectors.toList());
        }
        log.debug("{相关推荐[标签]，查询到视频[{}]的标签：{}", videoId, JSONObject.toJSONString(top3Ids));
        return top3Ids;
    }

}
