package com.miguan.ballvideo.service.recommend;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * 视频推荐
 */
@Service
@Slf4j
public class FindRecommendVideosServiceImpl {

    @Resource(name = "logMongoTemplate")
    private MongoTemplate mongoTemplate;
    @Autowired
    private BloomFilterService bloomFilterService;

    public List<String> getVideoId(String uuid, Map<Integer, Long> catidDivide, List<Integer> excludeCollectids, boolean isABTest) {
        List<String> videos = Lists.newArrayList();
        catidDivide.entrySet().forEach(e -> {
            int catid = e.getKey().intValue();
            List<String> get = getVideoId(uuid, catid, e.getValue().intValue(), excludeCollectids, isABTest);
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
    public List<String> getVideoId(String uuid, Integer catid, int count, List<Integer> excludeCollectids, boolean isABTest) {
        long timeOut = System.currentTimeMillis() + 100;
        return getVideoId(uuid, catid, count, null, excludeCollectids, timeOut, isABTest);
    }

    /**
     * 指定分类集合获取
     * @param uuid
     * @param count
     * @param includeCatids
     * @param excludeCollectids
     * @return
     */
    public List<String> getVideoId(String uuid, int count, List<Integer> includeCatids, List<Integer> excludeCollectids, long timeout, boolean isABTest) {
        return getVideoId(uuid, null, count, includeCatids, excludeCollectids, timeout, isABTest);
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
    private List<String> getVideoId(String uuid, Integer catid, int count, List<Integer> includeCatids, List<Integer> excludeCollectids, long timeout, boolean isABTest) {
        int size = 1000;
        int currentSize = 0;
        return findAndFilterVideo(null, uuid, count, catid, size, currentSize, includeCatids, excludeCollectids, timeout, isABTest);
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
    public List<VideoHotspotVo> getVideoInfo(String uuid, Integer catid, int count, List<Integer> includeCatids, List<Integer> excludeCollectids, boolean isABTest) {
        int size = 1000;
        int currentSize = 0;
        long timeout = System.currentTimeMillis() + 100;
        return findAndFilterVideoInfo(null, uuid, count, catid, size, currentSize, includeCatids, excludeCollectids, timeout, isABTest);
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
    private List<String> findAndFilterVideo(List<String> hasQuery, String uuid, int getSize, Integer catid, int size, int currentSize, List<Integer> includeCatids, List<Integer> excludeCollectids, long timeout, boolean isABTest) {
        List<VideoHotspotVo> listVo = findAndFilterVideoInfo(null, uuid, getSize, catid, size, currentSize, includeCatids, excludeCollectids, timeout, isABTest);
        List<String> vidList = new ArrayList<>();
        if(isEmpty(listVo)){
            return vidList;
        }
        for(VideoHotspotVo vo:listVo){
            vidList.add(vo.getVideo_id());
        }
        return vidList;
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
    private List<VideoHotspotVo> findAndFilterVideoInfo(List<String> hasQuery, String uuid, int getSize, Integer catid, int size, int currentSize, List<Integer> includeCatids, List<Integer> excludeCollectids, long timeout, boolean isABTest) {
        Query query = new Query();
        if (catid != null) {
            query.addCriteria(Criteria.where("catid").is(catid.intValue()));
        }
        if (catid == null && !isEmpty(includeCatids)) {
            query.addCriteria(Criteria.where("catid").in(includeCatids));
        }
        //按运营要求，推荐里不出现合集视频
        query.addCriteria(Criteria.where("collection_id").is(0));
        query.addCriteria(Criteria.where("album_id").is(0));
        query.addCriteria(Criteria.where("state").is(1));
        query.with(Sort.by(Sort.Order.desc("weights")));
        query.limit(size);
        query.skip(currentSize);
        query.fields().include("video_id").include("video_time").exclude("_id");
        //从mongo中获取视频
        List<VideoHotspotVo> list = mongoTemplate.find(query, VideoHotspotVo.class, "video_hotspot");
        if (isEmpty(list)) {
            return null;
        }
        if (isEmpty(hasQuery)) {
            hasQuery = null;
        }
        int total = list.size();
        //通过布隆过滤器过滤，限制条数得到想要的条数结束遍历
        int index = 1;
        List<String> tmpVidlist = Lists.newArrayList();

        List<VideoHotspotVo> ouputList = Lists.newArrayList();
        List<String> outputVidList = Lists.newArrayList();
        List<VideoHotspotVo> filterTmplist = Lists.newArrayList();

        int h = list.size() - 1;
        for (int i = 0; i <= h; i++) {
            VideoHotspotVo vo = list.get(i);
            String vid = vo.getVideo_id();
            if (hasQuery != null && hasQuery.contains(vid)) {
                continue;
            }
            filterTmplist.add(vo);
            tmpVidlist.add(vid);
            if ((index % 200 == 0) || i == h) {
                long pt = System.currentTimeMillis();
                List<String> getTmp = bloomFilterService.containMuil(getSize - ouputList.size(), uuid, tmpVidlist);
                log.info("{} 推荐0.1bloom耗时长{}：{}", uuid, index, System.currentTimeMillis() - pt);
                tmpVidlist.clear();
                if (isEmpty(getTmp)) {
                    continue;
                }

                for(VideoHotspotVo tmpVo:filterTmplist){
                    if(getTmp.contains(tmpVo.getVideo_id())){
                        ouputList.add(tmpVo);
                        outputVidList.add(tmpVo.getVideo_id());
                        if(ouputList.size() >= getSize){
                            break;
                        }
                    }
                }

                filterTmplist.clear();
                if(ouputList.size() >= getSize){
                    break;
                }
            }
            index++;
        }
        list.clear();
        //过滤后获取到的视频数
        int currentGetNum = 0;
        if (!isEmpty(ouputList)) {
            currentGetNum = ouputList.size();
        }
        boolean isContinu = currentGetNum < getSize;
        List<VideoHotspotVo> tmp = null;
        //如果没有找到或找到数量不足，则循环查找热度库，且判断热度库是不是还有数据可查(是不是最后一页)
        if (isContinu && total == size) {
            if (currentGetNum != 0) {
                hasQuery = hasQuery == null ? Lists.newArrayList() : hasQuery;
                hasQuery.addAll(outputVidList);
            }
            if (timeout != 0 && System.currentTimeMillis() > timeout) {
                log.warn("{} 推荐0.1此次获取推荐超时，不再循环获取", uuid);
            } else {
                tmp = findAndFilterVideoInfo(hasQuery, uuid, getSize - currentGetNum, catid, size, currentSize + size, includeCatids, excludeCollectids, timeout, isABTest);
            }
        }
        if (tmp != null) {
            ouputList = ouputList == null ? Lists.newArrayList() : ouputList;
            ouputList.addAll(tmp);
        }
        return ouputList;
    }


    public void recordHistory(String uuid, List<String> searchVideo) {
        bloomFilterService.putAll(uuid, searchVideo);
    }
}