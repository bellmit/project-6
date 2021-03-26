package com.miguan.ballvideo.dao;

import com.miguan.ballvideo.entity.dsp.IdeaAdvertUserVo;

import java.util.List;
import java.util.Map;

/**
 * @program: dspPutIn-java
 * @description: 广告管理DAO
 * @author: suhj
 * @create: 2020-09-22 20:06
 **/

public interface DspAdvDesignDao {

    List<Map<String, Object>> getLst(String advertDesignId, String adUserId);

    void updateById(IdeaAdvertUserVo ideaAdvertUserVo);

    String save(IdeaAdvertUserVo ideaAdvertUserVo);

    List<Map<String, Object>> findPositionLst(String adUserId);
}
