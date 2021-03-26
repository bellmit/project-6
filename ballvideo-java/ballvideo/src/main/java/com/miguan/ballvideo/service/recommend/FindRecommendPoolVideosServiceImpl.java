package com.miguan.ballvideo.service.recommend;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.miguan.ballvideo.vo.mongodb.VideoHotspotVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**离线计算推荐池
 * 视频推荐
 */
@Service
@Slf4j
public class FindRecommendPoolVideosServiceImpl {

    @Resource(name = "logMongoTemplate")
    private MongoTemplate mongoTemplate;
    @Autowired
    private BloomFilterService bloomFilterService;
    @Resource(name = "recDB9Pool")
    private JedisPool recDB9Pool;
    private final String keyPrifixed = "uuidRecs:";
    private final String keyPrifixed1 = "kafkaRecs:";
    private final String keyPrix = "adcoll:uuid:";

    /**
     * 根据条件获取
     * @param uuid
     * @param count
     * @param excludeCatids
     * @return
     */
    public List<String> getVideoId(String uuid, int count, List<Integer> excludeCatids, boolean isABTest) {
        return findAndFilterVideo(uuid, count, excludeCatids, isABTest);
    }

    /**
     * 根据条件获取推荐视频信息
     * @param uuid
     * @param count
     * @param excludeCatids
     * @return
     */
    public List<VideoHotspotVo> getVideoInfo(String uuid, int count, List<Integer> excludeCatids, boolean isABTest) {
        return findAndFilterVideoInfo(uuid, count, excludeCatids, isABTest);
    }


    /**
     *
     * @param uuid 设备号
     * @param getSize  此次要获取的目标视频数量
     * @param excludeCatids
     * @return
     */
    private List<String> findAndFilterVideo(String uuid, int getSize, List<Integer> excludeCatids, boolean isABTest) {
        List<VideoHotspotVo> listVo = findAndFilterVideoInfo(uuid,getSize,excludeCatids,isABTest);
        List<String> vidList = new ArrayList<>();
        if(isEmpty(listVo)){
            return vidList;
        }
        for(VideoHotspotVo vo:listVo){
            vidList.add(vo.getVideo_id());
        }
        return vidList;
    }

    private List<VideoHotspotVo> findAndFilterVideoInfo(String uuid, int getSize, List<Integer> excludeCatids, boolean isABTest) {
        List<String> videoIds = null;
        try (Jedis con = recDB9Pool.getResource()) {
            String key = keyPrix.concat(uuid);

            //添加key转换获取
            //这个存储依赖于埋点上报收集应用里的缓存
            String uuidint = con.get(key);
            if (uuidint == null) {
                log.warn("{} 推荐0.2从redis未找到uuid的uuidint", uuid);
                return null;
            }

            key = keyPrifixed.concat(uuidint);
            if(isABTest){
                key = keyPrifixed1.concat(uuidint);
            }
            log.warn("{} 推荐0.2获取离线推荐视频>>{}", uuid, key);

            String videoIdsStr = con.get(key);
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.2获取离线推荐视频：key={} value={}", uuid, key, videoIdsStr);
            }
            if (StringUtils.isNotBlank(videoIdsStr)) {
                videoIds = Lists.newArrayList(videoIdsStr.split(","));
            }
        }
        if (isEmpty(videoIds)) {
            return null;
        }
        //为什么要用这个的操作，是为了筛选屏蔽分类标签等
        Query query = new Query(Criteria.where("video_id").in(videoIds)
                .and("collection_id").is(0).and("album_id").is(0).and("state").is(1));
        query.with(Sort.by(Sort.Order.desc("weights")));
        query.fields().include("video_id").include("catid").include("video_time").exclude("_id");
        //从mongo中获取视频
        List<VideoHotspotVo> list = mongoTemplate.find(query, VideoHotspotVo.class, "video_hotspot");
        if (log.isDebugEnabled()) {
            log.debug("{} 推荐0.2获取离线推荐视频mongodb筛选：{}", uuid, videoIds);
        }
        if (isEmpty(list)) {
            return null;
        }
        //通过布隆过滤器过滤，限制条数得到想要的条数结束遍历
        int index = 1;
        List<VideoHotspotVo> ouputList = Lists.newArrayList();
        List<VideoHotspotVo> filterTmplist = Lists.newArrayList();
        List<String> tmpVideoIdList = new ArrayList<>();
        for (int i = 0; i < list.size() ; i++) {
            VideoHotspotVo tmpVo = list.get(i);
            //过滤屏蔽的分类
            if (!isEmpty(excludeCatids)) {
                Integer catid = tmpVo.getCatid();
                if (excludeCatids.contains(catid)) {
                    continue;
                }
            }
            filterTmplist.add(tmpVo);
            tmpVideoIdList.add(tmpVo.getVideo_id());
        }

        // 曝光过滤
        long pt = System.currentTimeMillis();
        List<String> getTmp = bloomFilterService.containMuil(getSize, uuid, tmpVideoIdList);
        log.info("{}   bloom耗时长{}：{}", uuid, index, System.currentTimeMillis() - pt);

        if (!isEmpty(getTmp)) {
            for(VideoHotspotVo vo:filterTmplist){
                if(getTmp.contains(vo.getVideo_id())){
                    ouputList.add(vo);
                }
            }
        }
        filterTmplist.clear();

        if (log.isDebugEnabled()) {
            log.debug("{} 推荐0.2获取离线推荐视频过滤后数据：{}", uuid, JSON.toJSONString(ouputList));
        }
        list.clear();
        return ouputList;
    }
}