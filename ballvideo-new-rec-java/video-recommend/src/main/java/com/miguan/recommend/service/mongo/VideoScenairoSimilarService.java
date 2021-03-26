package com.miguan.recommend.service.mongo;

import java.util.List;

public interface VideoScenairoSimilarService {

    public List<String> findFromMongoOrCache(Integer videoId);

    public List<String> findAndFilter(String uuid, Integer videoId, int getNum);

}
