package com.miguan.ballvideo.mapper;

import com.miguan.ballvideo.vo.response.VideoCatRes;

import java.util.List;

/**
 * 视频类型
*/
public interface VideoCatMapper {

    /**
     * 查询所有视频类型
     * @return
     */
    List<VideoCatRes> findAll();
}
