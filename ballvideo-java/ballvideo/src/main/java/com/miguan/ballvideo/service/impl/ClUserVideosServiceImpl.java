package com.miguan.ballvideo.service.impl;

import com.google.common.collect.Maps;
import com.miguan.ballvideo.mapper.ClUserVideosMapper;
import com.miguan.ballvideo.service.ClUserVideosService;
import com.miguan.ballvideo.vo.ClUserVideoInfoVo;
import com.miguan.ballvideo.vo.ClUserVideosVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 用户视频关联表ServiceImpl
 * @author xy.chen
 * @date 2019-08-09
 **/

@Service("clUserVideosService")
public class ClUserVideosServiceImpl implements ClUserVideosService {

    @Resource
    private ClUserVideosMapper clUserVideosMapper;

    @Override
    public List<ClUserVideosVo> findClUserVideosList(Map<String, Object> params) {
        return clUserVideosMapper.findClUserVideosList(params);
    }

    @Override
    public int saveClUserVideos(ClUserVideosVo clUserVideosVo) {
        return clUserVideosMapper.saveClUserVideos(clUserVideosVo);
    }

    @Override
    public int updateClUserVideos(ClUserVideosVo clUserVideosVo) {
        return clUserVideosMapper.updateClUserVideos(clUserVideosVo);
    }

    @Override
    public List<ClUserVideoInfoVo> findUserVideo(String userId, List<Long> list) {
        Map<String, Object> map = Maps.newHashMapWithExpectedSize(2);
        map.put("userId", userId);
        map.put("videoIds", list);
        return clUserVideosMapper.queryUserVideoInfo(map);
    }
}