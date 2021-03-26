package com.miguan.recommend.service.xy;

import com.miguan.recommend.entity.mongo.VideoHotspotVo;

import java.util.List;

public interface FirstVideoService {

    public List<VideoHotspotVo> getByIds(List<String> videoIds);
}
