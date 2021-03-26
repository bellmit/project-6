package com.miguan.recommend.service.mongo;

import java.util.List;

public interface ScenairoVideoService {

    public Integer findScenarioNumIdFromMongoOrCache(Integer videoId);

    public List<String> findVideoFromMongoOrCache(Integer sceneNum);
}
