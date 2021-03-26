package com.miguan.ballvideo.service;

import com.miguan.ballvideo.entity.VideoGather;
import com.miguan.ballvideo.vo.VideosCatVo;

import java.util.List;
import java.util.Map;

/**
 * 视频分类表Service
 *
 * @author xy.chen
 * @date 2019-08-09
 **/

public interface VideosCatService {

    /**
     * 通过条件查询首页视频分类列表
     **/
    List<VideosCatVo> findFirstVideosCatList(String channelId, String appVersion, String appPackage);

    /**
     * 通过条件查询首页视频分类列表1.8.0
     **/
    Map<String, Object> findFirstVideosCatList18(String channelId, String appVersion,String appPackage, String deviceId, String teenagerModle);

  /**
   * 根据state，type查询分类名称
   * @param state 1-开启，2-停用
   * @param type 类型 10首页视频 20 小视频
   * @return
   */
  List<String> getCatIdsByStateAndType(int state, String type);

    /**
     * 查询合集信息
     *
     * @param excludeIds
     * @param currentPage
     * @param pageSize
     * @return
     */
  List<VideoGather> findGatherIdsNotIn(String[] excludeIds, int currentPage, int pageSize);


    boolean existByCatIdAndApp(String catId, String appPackage);
}