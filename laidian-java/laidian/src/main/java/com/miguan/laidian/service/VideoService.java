package com.miguan.laidian.service;

import com.github.pagehelper.Page;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.entity.Video;
import com.miguan.laidian.entity.VideosCat;
import com.miguan.laidian.vo.VideoExposureVo;
import com.miguan.laidian.vo.VideoLabelVo;
import com.miguan.laidian.vo.VideoVo;

import java.util.List;
import java.util.Map;

/**
 * 视频源列表Service
 *
 * @author xy.chen
 * @date 2019-07-08
 **/

public interface VideoService {

    /**
     * 查询视频分类
     *
     * @return
     */
    List<VideosCat> findAll();

    /**
     * 通过条件查询视频源列列表
     **/
    Page<Video> findVideosList(Map<String, Object> params, int currentPage, int pageSize);

    /**
     * 修改视频收藏、分享数量消息队列
     **/
    boolean updateCountSendMQ(Map<String, Object> params);

    /**
     * 修改视频收藏、分享数量
     **/
    boolean updateCount(Map<String, Object> params);

    /**
     * 设置收藏类型
     *
     * @param params
     * @return
     */
    int updateType(Map<String, Object> params);

    /**
     * 通过条件查询收藏信息列表
     **/
    Page<Map<String, Object>> findCollectionList(Map<String, Object> params, int currentPage, int pageSize);

    /**
     * 获得分享视频列表(分享视频，以及热门前8的视频)
     *
     * @param videoId 分享视频的id
     * @return
     */
    List<Map<String, Object>> gainShareVideos(String videoId);

    int batchDelCollections(String[] split);

    /**
     * 保存上传视频信息
     *
     * @param videosVo
     * @return
     */
    int saveUploadVideos(Video videosVo);

    /**
     * 批量删除上传视频信息
     *
     * @param uploadVideosIds
     * @return
     */
    int batchDelUploadVideos(String[] uploadVideosIds);

    Video findOne(Map<String, Object> params);

    /**
     * 快捷设置来电秀素材（6个）
     *
     * @param params
     * @param num
     * @return
     */
    List<VideoVo> quickSetupCallShowVideos(Map<String, Object> params, int num);

    /**
     * 设置广告相关配置信息
     * @param appType
     * @param videosList
     */
    void setAdvConfig(String appType, List<Video> videosList);

    /**
     * 清空我的收藏
     * @param userId
     * @return
     */
    int emptyMyCollection(String userId);

    /**
     * 查询视频信息
     *
     * @param params
     * @return
     */
    Video findVideo(Map<String, Object> params);

    /**
     * 查询审核通过最新视频的创建时间
     * @return
     */
    Long queryNewVideosTime();

    /**
     * 待统计视频曝光数放入MQ
     * @param videoList
     */
    void videoExposureSendToMQ(List<Video> videoList);

    /**
     * 统计视频曝光数量:存放redis
     * @param videoList
     */
    void addVideoExposure(List<Video> videoList);

    Map<String, List<VideoExposureVo>> getVideoExposureCountInfo(String dates);

    List<VideoLabelVo> topSearchLabel();

    /**
     * 查询标签视频列表
     *
     * @param likeLabelName
     * @param commomParams
     * @return
     */
    Page<Video> findLabelVideosList(String likeLabelName,CommonParamsVo commomParams);

    /**
     * 感兴趣视频ID列表保存redis
     * @param ids
     * @param deviceId
     */
    void saveInterestVideoIds(String ids, String deviceId, String interestCatIds);
}