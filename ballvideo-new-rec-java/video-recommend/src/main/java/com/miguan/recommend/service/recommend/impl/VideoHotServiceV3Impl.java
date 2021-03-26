package com.miguan.recommend.service.recommend.impl;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.bo.VideoQueryDto;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.service.BloomFilterService;
import com.miguan.recommend.service.mongo.VideoHotspotService;
import com.miguan.recommend.service.recommend.EmbeddingService;
import com.miguan.recommend.service.recommend.VideoHotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service("videoHotServiceV3")
public class VideoHotServiceV3Impl implements VideoHotService<VideoHotspotVo> {
    @Resource
    private VideoHotspotService videoHotspotService;
    @Resource
    private BloomFilterService bloomFilterService;
    @Resource
    private EmbeddingService embeddingService;

    @Override
    public List<VideoHotspotVo> findAndFilterOfTheLastOnline(VideoQueryDto videoQueryDto, List<VideoHotspotVo> hasQuery) {
        long timeout = System.currentTimeMillis() + 200;
        int index = 0;
        int size = 200;
        List<VideoHotspotVo> videoHotspotVos = this.findLastOnlineFromMongoAndBloomFilter(videoQueryDto, hasQuery, index, size);
        if(isEmpty(videoHotspotVos)){
            return null;
        }
        int recursiveNumber = 0;
        while (videoHotspotVos.size() < videoQueryDto.getGetNum()){
            if (System.currentTimeMillis() > timeout || recursiveNumber > 3) {
                log.error("{} 获取最新上线视频超时, 不再循环获取", videoQueryDto.getUuid());
                break;
            }
            log.info("{} 获取最新上线视频个数不满足，遍历mongo", videoQueryDto.getUuid());
            index += size;
            // 需要补充的视频个数
            int needAddNum = videoQueryDto.getGetNum() - videoHotspotVos.size();

            List<VideoHotspotVo> queryAgainList = this.findLastOnlineFromMongoAndBloomFilter(videoQueryDto, hasQuery, index, size);
            if (isEmpty(queryAgainList)) {
                continue;
            }

            if (queryAgainList.size() > needAddNum) {
                videoHotspotVos.addAll(queryAgainList.subList(0, needAddNum - 1));
            } else {
                videoHotspotVos.addAll(queryAgainList);
            }
        }
        return videoHotspotVos;
    }

    @Override
    public List<VideoHotspotVo> findAndFilter(VideoQueryDto videoQueryDto, List<VideoHotspotVo> hasQuery) {
        long timeout = System.currentTimeMillis() + 200;
        int index = 0;
        int size = 1000;
        List<VideoHotspotVo> catVideos = findFromMongoAndBloomFilter(videoQueryDto, hasQuery, index, size);
        if (isEmpty(catVideos)) {
            catVideos = new ArrayList<VideoHotspotVo>();
        }

        int recursiveNumber = 0;
        // 如果已拿到的视频个数不满足需要返回的数量
        while (catVideos.size() < videoQueryDto.getGetNum()) {
            if (System.currentTimeMillis() > timeout || recursiveNumber > 5) {
                log.error("{} 获取分类[{}]视频超时, 不再循环获取", videoQueryDto.getUuid(), videoQueryDto.getCatid());
                break;
            }
            log.info("{} 获取分类[{}]视频个数不满足，遍历mongo", videoQueryDto.getUuid(), videoQueryDto.getCatid());
            int needAddNum = videoQueryDto.getGetNum() - catVideos.size();
            index += size;
            recursiveNumber++;
            List<VideoHotspotVo> queryAgainList = this.findFromMongoAndBloomFilter(videoQueryDto, hasQuery == null ? catVideos : hasQuery, index, size);
            if (isEmpty(queryAgainList)) {
                continue;
            }

            if (queryAgainList.size() > needAddNum) {
                catVideos.addAll(queryAgainList.subList(0, needAddNum - 1));
            } else {
                catVideos.addAll(queryAgainList);
            }
        }
        return catVideos;
    }

    @Override
    public List<VideoHotspotVo> findAndFilter(String uuid, List<Integer> includeCatid, Integer catid, Integer sensitive, int getNum, List<VideoHotspotVo> hasQuery) {
        long timeout = System.currentTimeMillis() + 200;
        int index = 0;
        int size = 1000;
        List<VideoHotspotVo> catVideos = findFromMongoAndBloomFilter(uuid, includeCatid, catid, sensitive, size, index, getNum, hasQuery);
        if (isEmpty(catVideos)) {
            catVideos = new ArrayList<VideoHotspotVo>();
        }

        int recursiveNumber = 0;
        // 如果已拿到的视频个数不满足需要返回的数量
        while (catVideos.size() < getNum) {
            if (System.currentTimeMillis() > timeout || recursiveNumber > 5) {
                log.error("{} 获取分类[{}]视频超时, 不再循环获取", uuid, catid);
                break;
            }
            log.info("{} 获取分类[{}]视频个数不满足，遍历mongo", uuid, catid);
            int needAddNum = getNum - catVideos.size();
            index += size;
            recursiveNumber++;
            List<VideoHotspotVo> queryAgainList = this.findFromMongoAndBloomFilter(uuid, includeCatid, catid, sensitive, size, index, needAddNum, hasQuery == null ? catVideos : hasQuery);
            if (isEmpty(queryAgainList)) {
                continue;
            }

            if (queryAgainList.size() > needAddNum) {
                catVideos.addAll(queryAgainList.subList(0, needAddNum - 1));
            } else {
                catVideos.addAll(queryAgainList);
            }
        }
        return catVideos;
    }

    private List<VideoHotspotVo> findLastOnlineFromMongoAndBloomFilter(VideoQueryDto queryDto, List<VideoHotspotVo> hasQuery, int skipNum, int size){
        List<VideoHotspotVo> videoHotspotVos = videoHotspotService.findFromMongoByOnlineDateDesc(skipNum, size);
        if(isEmpty(videoHotspotVos)){
            return null;
        }

        if(!isEmpty(hasQuery)){
            videoHotspotVos.removeAll(hasQuery);
        }
        return bloomFilterService.containMuilEntitySplit(queryDto.getUuid(), videoHotspotVos, queryDto.getGetNum());
    }

    private List<VideoHotspotVo> findFromMongoAndBloomFilter(VideoQueryDto queryDto, List<VideoHotspotVo> hasQuery, int skipNum, int size) {
        List<VideoHotspotVo> list = videoHotspotService.findFromMongoOrCache(queryDto.getCatid(), queryDto.getSensitive(), queryDto.getExcludedSource(), skipNum, size);
        log.debug("热度视频查询条件>>{}, 结果条数>>{}", JSONObject.toJSONString(queryDto), isEmpty(list) ? 0 : list.size());
        if (isEmpty(list)) {
            return null;
        }
        if (!isEmpty(hasQuery)) {
            list.removeAll(hasQuery);
        }
        return bloomFilterService.containMuilEntitySplit(queryDto.getUuid(), list, queryDto.getGetNum());
    }

    private List<VideoHotspotVo> findFromMongoAndBloomFilter(String uuid, List<Integer> includeCatid, Integer catid, Integer sensitive, int size, int skipNum, int getNum, List<VideoHotspotVo> hasQuery) {
        List<VideoHotspotVo> list = videoHotspotService.findFromMongoOrCache(includeCatid, catid, sensitive, size, skipNum);
        log.debug("热度视频查询条件includeCatid：{}，catid：{}，sensitive：{}， size：{}， skipNum：{}, 结果条数>>{}", includeCatid, catid, sensitive, size, skipNum, isEmpty(list) ? 0 : list.size());
        if (isEmpty(list)) {
            return null;
        }
        if (!isEmpty(hasQuery)) {
            list.removeAll(hasQuery);
        }
        return bloomFilterService.containMuilEntitySplit(uuid, list, getNum);
    }
}
