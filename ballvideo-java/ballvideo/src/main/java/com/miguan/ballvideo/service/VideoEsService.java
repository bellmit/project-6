package com.miguan.ballvideo.service;

import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.dto.VideoGatherParamsDto;
import com.miguan.ballvideo.entity.es.FirstVideoEsVo;
import com.miguan.ballvideo.vo.SearchHistoryLogVo;
import com.miguan.ballvideo.vo.video.Videos161Vo;

import java.util.List;
import java.util.Map;

/**
 * Created by shixh on 2020/1/7.
 */
public interface VideoEsService {

    String getVideoSql();

    ResultMap deleteIndex(String index);

    Object init();

    void init(String sqlBuffer);

    Object search(String title, String userId, VideoGatherParamsDto params, AbTestAdvParamsVo queueVo);

    Object update(String videoIds, String options);

    Object updateByGatherId(Long gatherId,String videoIds);

    void deleteOrCloseGather(long gatherId, int state);

    void deleteDueVideos();

    FirstVideoEsVo getById(Long id);

    Object getByCatId(Long id,Long incentiveVideo);

    void save(FirstVideoEsVo vo);



    Object updateByGatgherId(Long gatherId);

    Object getMyGatherVidesoById(Long videoId);

    //首页视频推荐接口/视频详情
    List<Videos161Vo> query(Map<String, Object> params);

    //首页视频非推荐接口
    List<Videos161Vo> query(Map<String, Object> params, int num);

    List<Videos161Vo> queryByIds(List<String> videos);

    void saveToEs(List<Videos161Vo> videos);

    void videoDataDeleteMQ(String type);

    List<Videos161Vo> getVideoByIdList(String ids);

    void saveSearchInfo(SearchHistoryLogVo logVo);
}
