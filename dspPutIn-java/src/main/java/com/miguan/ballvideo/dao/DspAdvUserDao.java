package com.miguan.ballvideo.dao;

import com.miguan.ballvideo.entity.dsp.IdeaAdvertUserVo;

import java.util.List;
import java.util.Map;

/**
 * @program: dspPutIn-java
 * @description: 广告主列表DAO
 * @author: suhj
 * @create: 2020-09-22 20:06
 **/

public interface DspAdvUserDao {

    List<Map<String, Object>> getList(String name, String type, String adUserId);

    void updateById(IdeaAdvertUserVo ideaAdvertUserVo);

    String save(IdeaAdvertUserVo ideaAdvertUserVo);

    List<Map<String, Object>> findPlanLst(String adUserId);
}
