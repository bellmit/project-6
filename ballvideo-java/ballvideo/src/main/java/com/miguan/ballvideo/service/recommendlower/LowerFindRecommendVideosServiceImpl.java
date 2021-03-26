package com.miguan.ballvideo.service.recommendlower;

import com.google.common.collect.Lists;
import com.miguan.ballvideo.vo.mongodb.VideoHotspotVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * 视频推荐
 */
@Service
@Slf4j
public class LowerFindRecommendVideosServiceImpl {

    @Resource(name = "logMongoTemplate")
    private MongoTemplate mongoTemplate;
    @Autowired
    private LowerBloomFilterService lowerBloomFilterService;

    public List<String> getVideoId(String uuid, Map<Integer, Long> catidDivide, List<Integer> excludeCollectids) {
        List<String> videos = Lists.newArrayList();
        catidDivide.entrySet().forEach(e -> {
            int catid = e.getKey().intValue();
            List<String> get = getVideoId(uuid, catid, e.getValue().intValue(), excludeCollectids);
            if (!isEmpty(get)) {
                videos.addAll(get);
            }
        });
        return videos;
    }

    /**
     * 加单个分类获取
     * @param uuid
     * @param catid
     * @param count
     * @param excludeCollectids
     * @return
     */
    public List<String> getVideoId(String uuid, Integer catid, int count, List<Integer> excludeCollectids) {
        return getVideoId(uuid, catid, count, null, excludeCollectids, 0);
    }

    /**
     * 指定分类集合获取
     * @param uuid
     * @param count
     * @param includeCatids
     * @param excludeCollectids
     * @return
     */
    public List<String> getVideoId(String uuid, int count, List<Integer> includeCatids, List<Integer> excludeCollectids, long timeout) {
        return getVideoId(uuid, null, count, includeCatids, excludeCollectids, timeout);
    }

    /**
     * 根据条件获取
     * @param uuid
     * @param catid
     * @param count
     * @param includeCatids
     * @param excludeCollectids
     * @return
     */
    private List<String> getVideoId(String uuid, Integer catid, int count, List<Integer> includeCatids, List<Integer> excludeCollectids, long timeout) {
        int size = 1000;
        int currentSize = 0;
        return findAndFilterVideo(null, uuid, count, catid, size, currentSize, includeCatids, excludeCollectids, timeout);
    }

    /**
     *
     * @param hasQuery 过滤已经获取到的视频
     * @param uuid 设备号
     * @param getSize  此次要获取的目标视频数量
     * @param catid  此次查询的目标分类
     * @param size   分页限查询数量
     * @param currentSize 分页跳过数量
     * @param includeCatids
     * @param excludeCollectids
     * @return
     */
    private List<String> findAndFilterVideo(List<String> hasQuery, String uuid, int getSize, Integer catid, int size, int currentSize, List<Integer> includeCatids, List<Integer> excludeCollectids, long timeout) {
        Query query = new Query();
        if (catid != null) {
            query.addCriteria(Criteria.where("catid").is(catid.intValue()));
        }
        if (catid == null && !isEmpty(includeCatids)) {
            query.addCriteria(Criteria.where("catid").in(includeCatids));
        }
//        if (!isEmpty(excludeCollectids)) {
//            query.addCriteria(Criteria.where("collection_id").nin(excludeCollectids));
//        }
        //按运营要求，推荐里不出现合集视频
        query.addCriteria(Criteria.where("collection_id").is(0));
        query.addCriteria(Criteria.where("album_id").is(0));
        if (isEmpty(hasQuery)) {
            hasQuery = null;
        }
        query.addCriteria(Criteria.where("state").is(1));
        query.with(Sort.by(Sort.Order.desc("weights")));
        query.limit(size);
        query.skip(currentSize);
        query.fields().include("video_id").exclude("_id");
        //从mongo中获取视频
        List<VideoHotspotVo> list = mongoTemplate.find(query, VideoHotspotVo.class, "video_hotspot");
        if (isEmpty(list)) {
            return null;
        }
        int total = list.size();
        //通过布隆过滤器过滤，限制条数得到想要的条数结束遍历
        int index = 1;
        List<String> get = Lists.newArrayList();
        List<String> vlist = Lists.newArrayList();
        int h = list.size() - 1;
        for (int i = 0; i <= h; i++) {
            String vid = list.get(i).getVideo_id();
            if (hasQuery != null && hasQuery.contains(vid)) {
                continue;
            }
            vlist.add(vid);
            if ((index % 200 == 0) || i == h) {
                long pt = System.currentTimeMillis();
                List<String> getTmp = lowerBloomFilterService.containMuil(getSize - get.size(), uuid, vlist);
                //log.info("推荐0.0bloom耗时长{}：{}", index, System.currentTimeMillis() - pt);
                vlist.clear();
                if (!isEmpty(getTmp)) {
                    get.addAll(getTmp);
                    if (get.size() >= getSize) {
                        break;
                    }
                }
            }
            index++;
        }
        list.clear();
        //过滤后获取到的视频数
        int currentGetNum = 0;
        if (!isEmpty(get)) {
            currentGetNum = get.size();
        }
        boolean isContinu = currentGetNum < getSize;
        List<String> tmp = null;
        //如果没有找到或找到数量不足，则循环查找热度库，且判断热度库是不是还有数据可查(是不是最后一页)
        if (isContinu && total == size) {
            if (currentGetNum != 0) {
                hasQuery = hasQuery == null ? Lists.newArrayList() : hasQuery;
                hasQuery.addAll(get);
            }
            if (timeout != 0 && System.currentTimeMillis() > timeout) {
                log.warn("推荐0.0此次获取推荐超时，不再循环获取");
            } else {
                tmp = findAndFilterVideo(hasQuery, uuid, getSize - currentGetNum, catid, size, currentSize + size, includeCatids, excludeCollectids, timeout);
            }
        }
        if (tmp != null) {
            get = get == null ? Lists.newArrayList() : get;
            get.addAll(tmp);
        }
        return get;
    }


    public void recordHistory(String uuid, List<String> searchVideo) {
        lowerBloomFilterService.putAll(uuid, searchVideo);
    }
}