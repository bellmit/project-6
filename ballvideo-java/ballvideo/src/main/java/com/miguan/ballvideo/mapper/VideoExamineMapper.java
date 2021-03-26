package com.miguan.ballvideo.mapper;

import com.miguan.ballvideo.vo.SmsTplVo;
import com.miguan.ballvideo.vo.VideoAlbumVo;
import com.miguan.ballvideo.vo.video.VideoExamineVo;
import com.miguan.ballvideo.vo.video.Videos161Vo;

import java.util.List;
import java.util.Map;

/**
 * 审核申请 mapper
 * @author daoyu
 * @date 2020-06-03
 **/

public interface VideoExamineMapper {

    /**
     * 根据
     * @param params
     * @return VideoExamineVo
     */
    VideoExamineVo findSelective(Map<String, Object> params);

}