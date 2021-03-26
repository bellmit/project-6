package com.miguan.recommend.service.recommend.impl;

import com.miguan.recommend.bo.VideoQueryDto;
import com.miguan.recommend.common.constants.MongoConstants;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.service.BloomFilterService;
import com.miguan.recommend.service.recommend.VideoHotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service("videoHotServiceV2")
public class VideoHotServiceV2Impl implements VideoHotService<String> {

    @Resource(name = "logMongoTemplate")
    private MongoTemplate mongoTemplate;
    @Resource
    private BloomFilterService bloomFilterService;

    @Override
    public List<String> findAndFilterOfTheLastOnline(VideoQueryDto videoQueryDto, List<String> hasQuery) {
        return null;
    }

    @Override
    public List<String> findAndFilter(VideoQueryDto videoQueryDto, List<String> hasQuery) {
        return null;
    }

    @Override
    public List<String> findAndFilter(String uuid, List<Integer> includeCatid, Integer catid, Integer sensitive, int getNum, List<String> hasQuery) {
        long timeout = 200 + System.currentTimeMillis();
        int index = 0;
        int size = 1000;
        List<String> catVideos = findFromMongoAndBloomFilter(uuid, includeCatid, catid, size, index, getNum, hasQuery);
        if (isEmpty(catVideos)) {
            return null;
        }

        // 如果已拿到的视频个数不满足需要返回的数量
        while (catVideos.size() < getNum) {
            if (System.currentTimeMillis() > timeout) {
                log.error("{} 获取用户兴趣分类[{}]视频超时, 不再循环获取", uuid, catid);
            }
            log.info("{} 获取用户兴趣分类[{}]视频个数不满足，遍历mongo", uuid, catid);
            int needAddNum = getNum - catVideos.size();
            index += size;
            List<String> queryAgainList = this.findFromMongoAndBloomFilter(uuid, includeCatid, catid, size, index, needAddNum, hasQuery == null ? catVideos : hasQuery);
            if (isEmpty(queryAgainList)) {
                break;
            }

            if (queryAgainList.size() > needAddNum) {
                catVideos.addAll(queryAgainList.subList(0, needAddNum - 1));
            } else {
                catVideos.addAll(queryAgainList);
            }
        }
        return catVideos;
    }


    private List<String> findFromMongoAndBloomFilter(String uuid, List<Integer> includeCatid, Integer catid, int size, int skipNum, int getNum, List<String> hasQuery) {
        Query query = new Query();
        if (catid != null) {
            query.addCriteria(Criteria.where("catid").is(catid.intValue()));
        }
        if (catid == null && !isEmpty(includeCatid)) {
            query.addCriteria(Criteria.where("catid").in(includeCatid));
        }
        //按运营要求，推荐里不出现合集视频
        query.addCriteria(Criteria.where("collection_id").is(0));
        query.addCriteria(Criteria.where("state").is(1));
        query.with(Sort.by(Sort.Order.desc("weights")));
        query.limit(size);
        if (skipNum > 0) {
            query.skip(skipNum);
        }
        //query.fields().include("video_id").exclude("_id");
        log.debug("mongo查询语句>>{}", query.toString());
        //从mongo中获取视频
        long mongoStart = System.currentTimeMillis();
        List<VideoHotspotVo> list = mongoTemplate.find(query, VideoHotspotVo.class, MongoConstants.video_hotspot);
        log.info("mongo查询热度视频耗时: {}", System.currentTimeMillis() - mongoStart);
        if (isEmpty(list)) {
            return null;
        }
        List<String> queryList = list.stream().map(VideoHotspotVo::getVideo_id).collect(Collectors.toList());
        if (!isEmpty(hasQuery)) {
            queryList.removeAll(hasQuery);
        }
        if (isEmpty(queryList)) {
            return null;
        }
        // 过滤已曝光过的视频
        return bloomFilterService.containMuil(getNum, uuid, queryList);
    }

}
