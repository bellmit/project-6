package com.miguan.recommend.service.es;

import java.util.List;

public interface EsSearchService {

    /**
     * 标题相关视频
     * @param videoTitle
     * @return
     */
    public List<String> relevantVideoOfTitle(String videoTitle);
}
