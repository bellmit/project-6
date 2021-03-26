package com.miguan.ballvideo.service.recommendlower;

import com.google.common.collect.Lists;
import com.miguan.ballvideo.service.FindVideosService;
import com.miguan.ballvideo.vo.mongodb.VideoHotspotVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * 视频推荐
 */
@Service
@Slf4j
public class FindVideosServiceImpl implements FindVideosService {

    @Resource(name = "logMongoTemplate")
    private MongoTemplate mongoTemplate;

    public List<String> getVideoId(String showedVideoIds, Map<Integer, Long> catidDivide, List<Integer> excludeCollectids) {
        List<String> videos = Lists.newArrayList();
        catidDivide.entrySet().forEach(e -> {
            int catid = e.getKey().intValue();
            List<String> get = getVideoId(showedVideoIds, catid, e.getValue().intValue(), excludeCollectids);
            if (!isEmpty(get)) {
                videos.addAll(get);
            }
        });
        return videos;
    }

    /**
     * 加单个分类获取
     *
     * @param showedVideoIds
     * @param catid
     * @param count
     * @param excludeCollectids
     * @return
     */
    @Override
    public List<String> getVideoId(String showedVideoIds, Integer catid, int count, List<Integer> excludeCollectids) {
        return getVideoId(showedVideoIds, catid, count, null, excludeCollectids, 0);
    }

    /**
     * 指定分类集合获取
     *
     * @param showedVideoIds
     * @param count
     * @param includeCatids
     * @param excludeCollectids
     * @return
     */
    public List<String> getVideoId(String showedVideoIds, int count, List<Integer> includeCatids, List<Integer> excludeCollectids, long timeout) {
        return getVideoId(showedVideoIds, null, count, includeCatids, excludeCollectids, timeout);
    }

    /**
     * 根据条件获取
     *
     * @param showedVideoIds
     * @param catid
     * @param count
     * @param includeCatids
     * @param excludeCollectids
     * @return
     */
    private List<String> getVideoId(String showedVideoIds, Integer catid, int count, List<Integer> includeCatids, List<Integer> excludeCollectids, long timeout) {
        int size = 1000;
        int currentSize = 0;
        ArrayList<String> showedVideoList = StringUtils.isNotBlank(showedVideoIds) ? Lists.newArrayList(showedVideoIds.split(",")) : null;
        return findAndFilterVideo(null, showedVideoList, count, catid, size, currentSize, includeCatids, excludeCollectids, timeout);
    }

    /**
     * @param hasQuery          过滤已经获取到的视频
     * @param showedVideoIds    设备号
     * @param getSize           此次要获取的目标视频数量
     * @param catid             此次查询的目标分类
     * @param size              分页限查询数量
     * @param currentSize       分页跳过数量
     * @param includeCatids
     * @param excludeCollectids
     * @return
     */
    private List<String> findAndFilterVideo(List<String> hasQuery, List<String> showedVideoIds, int getSize, Integer catid, int size, int currentSize, List<Integer> includeCatids, List<Integer> excludeCollectids, long timeout) {
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
        query.fields().include("video_id").exclude("_id");
        //从mongo中获取视频
        List<VideoHotspotVo> list = mongoTemplate.find(query, VideoHotspotVo.class, "video_hotspot");
        if (isEmpty(list)) {
            return null;
        }
        if (!isEmpty(hasQuery)) {
            list = list.stream().filter(e -> !hasQuery.contains(e)).collect(Collectors.toList());
        }
        //int total = list.size();
        //，限制条数得到想要的条数结束遍历
        List<String> get = Lists.newArrayList();
        if (isEmpty(showedVideoIds)) {
            get.addAll(list.subList(0, getSize).stream().map(VideoHotspotVo::getVideo_id).collect(Collectors.toList()));
            return get;
        }
        //long pt = System.currentTimeMillis();
        for (int i = 0, h = list.size(); i < h; i++) {
            String vid = list.get(i).getVideo_id();
            boolean exist = showedVideoIds.contains(vid);
            if (!exist) {
                get.add(vid);
                if (get.size() >= getSize) {
                    break;
                }
            }
        }
        return get;
    }
}