package com.miguan.recommend.service.es.impl;

import com.miguan.recommend.common.config.EsConfig;
import com.miguan.recommend.common.es.EsDao;
import com.miguan.recommend.entity.es.HotspotVideos;
import com.miguan.recommend.service.es.EsSearchService;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EsSearchServiceImpl implements EsSearchService {

    @Resource
    private EsConfig esConfig;
    @Resource
    private EsDao esDao;

    /**
     * 标题相关视频
     * @param videoTitle
     * @return
     */
    @Override
    @Cacheable(cacheNames = "relevant_video_of_title", cacheManager = "getCacheManager")
    public List<String> relevantVideoOfTitle(String videoTitle) {
        SearchSourceBuilder titleBuilder = new SearchSourceBuilder();
        titleBuilder.query(new MatchQueryBuilder("title", videoTitle));
        titleBuilder.size(100);
        List<HotspotVideos> queryResultList = esDao.search(esConfig.getVideo_title(), titleBuilder, HotspotVideos.class);
        if(CollectionUtils.isEmpty(queryResultList)){
            return null;
        }
        return queryResultList.stream().map(HotspotVideos::getId).collect(Collectors.toList());
    }
}
