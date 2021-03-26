package com.miguan.recommend.service.mongo;

import com.miguan.recommend.bo.VideoQueryDto;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;

import java.util.List;

public interface VideoHotspotService {

    /**
     * 从Mongo或本地缓存里查询
     */
    public List<VideoHotspotVo> findFromMongoOrCache(Integer catId, Integer sensitive, List<String> excludeSource, int skipNum, int size);

    /**
     * 从Mongo或本地缓存里查询
     * @param includeCatid 允许的分类ID列表
     * @param catid 指定的分类ID
     * @param size 获取个数
     * @param skipNum 跳过个数
     * @return
     */
    public List<VideoHotspotVo> findFromMongoOrCache(List<Integer> includeCatid, Integer catid, Integer sensitive, int size, int skipNum);

    /**
     * 通过视频ID，从Mongo获取
     * @param videoIds
     * @return
     */
    public List<VideoHotspotVo> findFromMongoById(List<String> videoIds, Integer sensitive);

    public List<VideoHotspotVo> findFromMongoById(List<String> videoIds);

    public List<VideoHotspotVo> findFromMongoByOnlineDateDesc(int skipNum, int size);


}
