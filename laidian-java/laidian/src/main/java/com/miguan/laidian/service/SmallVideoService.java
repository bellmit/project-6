package com.miguan.laidian.service;

import com.github.pagehelper.Page;
import com.miguan.laidian.vo.SmallVideoVo;

import java.util.List;
import java.util.Map;

/**
 * iOS视频源列表Service
 *
 * @author xy.chen
 * @date 2019-07-08
 **/

public interface SmallVideoService {

    /**
     * 通过条件查询视频源列列表
     **/
    Page<SmallVideoVo> findIOSVideosList(Map<String, Object> params, int currentPage, int pageSize);

    /**
     * 更新举报次数
     **/
    int updateReportCount(Map<String, Object> params);


    /**
     * 查看我的收藏视频
     * @param userId
     * @param currentPage
     * @param pageSize
     * @return
     */
    Page<SmallVideoVo> findMyCollection(String userId, int currentPage, int pageSize, String appType);

    /**
     * 更新用户收藏数、点赞数、观看数、是否感兴趣
     * @param params
     * @return
     */
    int updateVideosCount(Map<String, Object> params);

    /**
     *
     * 通过条件查询小视频列列表
     *
     **/
    List<SmallVideoVo> findVideosDetail(Map<String, Object> params);

    /**
     *
     * 通过条件查询小视频列列表
     *
     **/
    SmallVideoVo findVideosDetailByOne(Map<String, Object> params);
}