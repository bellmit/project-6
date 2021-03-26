package com.miguan.ballvideo.mapper;

import com.miguan.ballvideo.vo.video.HotWordVideoVo;

import java.util.List;
import java.util.Map;

public interface HotWordVideoMapper {

    List<HotWordVideoVo> queryHotWordVideoList(Map<String, Object> map);
}
