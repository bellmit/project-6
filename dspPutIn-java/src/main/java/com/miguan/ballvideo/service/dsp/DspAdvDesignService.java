package com.miguan.ballvideo.service.dsp;

import com.miguan.ballvideo.entity.dsp.IdeaAdvertUserVo;

import java.util.List;
import java.util.Map;

/**
 * Dsp广告创意 Service
 * @author suhongju
 * @date 2020-09-22
 */
public interface DspAdvDesignService {

    /**
     * 查询广告创意列表
     * @param advertDesignId
     * @param adUserId
     * @return
     */
    List<Map<String,Object>> getList(String advertDesignId, String adUserId);

    /**
     * 获取广告主详情
     * @param adUserId
     * @return
     */
    Map<String,Object> getInfo(String adUserId);

    /**
     * 新增,编辑广告主
     * @param ideaAdvertUserVo
     * @return
     */
    String advAddEdit(IdeaAdvertUserVo ideaAdvertUserVo);

}
