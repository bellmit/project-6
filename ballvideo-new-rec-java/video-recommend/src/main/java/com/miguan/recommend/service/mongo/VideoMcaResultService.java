package com.miguan.recommend.service.mongo;

public interface VideoMcaResultService {

    public String findSceneFromMongoOrCache(Integer videoId);
}
