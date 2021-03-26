package com.miguan.ballvideo.service.dsp;

import com.miguan.ballvideo.entity.dsp.AdvZoneValExpVo;
import com.miguan.ballvideo.entity.dsp.AdvertDspInfo;
import com.miguan.ballvideo.entity.dsp.IdeaAdvertUserVo;
import com.miguan.ballvideo.vo.AdvertCodeVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Dsp广告主管理 Service
 * @author suhongju
 * @date 2020-09-22
 */
public interface DspAdvUserService {

    /**
     * 查询广告主列表
     * @param name
     * @param type
     * @param adUserId
     * @return
     */
    List<Map<String,Object>> getList(String name, String type, String adUserId);

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
