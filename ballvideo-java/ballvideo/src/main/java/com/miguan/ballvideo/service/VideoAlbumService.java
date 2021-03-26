package com.miguan.ballvideo.service;

import com.github.pagehelper.Page;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.common.interceptor.argument.params.CommonParamsVo;
import com.miguan.ballvideo.dto.VideoGatherParamsDto;
import com.miguan.ballvideo.vo.FirstVideos;
import com.miguan.ballvideo.vo.VideoAlbumVo;
import com.miguan.ballvideo.vo.video.VideoAlbumListResultVo;
import com.miguan.ballvideo.vo.video.VideoAlbumResultVo;
import com.miguan.ballvideo.vo.video.VideoGatherVo;
import com.miguan.ballvideo.vo.video.Videos161Vo;

import java.util.List;

/**
 * 视频专辑Service
 */
public interface VideoAlbumService {

    /**
     * 查询专辑列表
     *
     * @param params
     * @return
     */
	List<VideoAlbumVo> getVideoAlbumList(VideoGatherParamsDto params);

    /**
     * 查询专辑列表
     *
     * @param params
     * @return
     */
    VideoAlbumListResultVo getVideoAlbumList26(AbTestAdvParamsVo queueVo, VideoGatherParamsDto params);

    /**
     * 查询专辑详情列表
     *
     * @param albumId
     * @param params
     * @return
     */
    Page<Videos161Vo> getVideoAlbumDetailList(Long albumId, CommonParamsVo params);

    /**
     * 查询专辑详情列表
     *
     * @param albumId
     * @param params
     * @param positionType
     * @param permission
     * @return
     */
    VideoAlbumResultVo getVideoAlbumDetailList(AbTestAdvParamsVo queueVo, Long albumId, CommonParamsVo params, String positionType, String permission);

    /**
     * 查询视频详情
     *
     * @param userId
     * @param id
     * @param albumId
     * @param mobileType
     * @param appVersion
     * @return
     */
    FirstVideos firstVideosDetail(String userId, String id, Long albumId,String mobileType,String appVersion);

    /**
     * 搜索页面“猜你喜欢”专辑列表
     * @param queueVo
     * @param userId
     * @param params
     * @return
     */
    Object getDefaultVideos(AbTestAdvParamsVo queueVo, String userId, VideoGatherParamsDto params);

    /**
     * 视频详情页面专辑列表
     * @param albumId
     * @param videoId
     * @param step
     * @return
     */
    VideoGatherVo getVideoAlbums(Long albumId,Long videoId,String step, String appPackage);

    /**
     * 当前视频所在专辑列表信息
     * @param vo
     * @return
     */
    VideoGatherVo getCurrentVideoAlbums(Videos161Vo vo, String appPackage);

    /**
     * 刷新全部视频Id所属专辑缓存
     */
    void changeVideoIdAlbum();

    VideoGatherVo getVideoAlbumVoByAlbumId(Long gatherId,boolean includeSearchData);

    void updateVideoIdAlbumChange(String albumId, String videoIds);

    /**
     * 下一个专辑视频查询
     * @param albumId
     * @param videoId
     * @return
     */
    List<Videos161Vo> getNextAlbumVideo(Long albumId,Long videoId,String publicInfo, String appPackage,String channelId,String appVersion,String abExp,String abIsol);
}
