package com.miguan.recommend.service.mongo;

import com.miguan.recommend.entity.mongo.IncentiveVideoHotspot;

import java.util.List;

public interface IncentiveVideoHotspotService {

    /**
     * 根据分类查询激励视频
     * @param catid
     * @return
     */
    public List<IncentiveVideoHotspot> findFromMongoOrCache(Integer catid, Integer sensitive, List<String> excludeSource);

    /**
     * 根据屏蔽分类查询激励视频
     * @param excludeCatids
     * @return
     */
    public List<IncentiveVideoHotspot> findFromMongoOrCache(List<Integer> excludeCatids, Integer sensitive);
}
