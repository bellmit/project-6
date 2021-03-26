package com.miguan.recommend.service.xy.impl;

import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.mapper.FirstVideosMapper;
import com.miguan.recommend.service.xy.FirstVideoService;
import com.miguan.recommend.vo.RecVideosVo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FirstVideoServiceImpl implements FirstVideoService {

    @Resource
    private FirstVideosMapper firstVideosMapper;

    @Override
    @Cacheable(cacheNames = "select_videos", cacheManager = "getCacheManager")
    public List<VideoHotspotVo> getByIds(List<String> videoIds) {

        List<RecVideosVo> recVideosVos = firstVideosMapper.findByIds(videoIds.stream().map(Integer::valueOf).collect(Collectors.toList()));
        List<VideoHotspotVo> videoHotspotVos = new ArrayList<>();
        recVideosVos.forEach(r -> {
            VideoHotspotVo vo = new VideoHotspotVo(r, 0, 0.9D, 0.9D, null);
            videoHotspotVos.add(vo);
        });
        return videoHotspotVos;
    }
}
