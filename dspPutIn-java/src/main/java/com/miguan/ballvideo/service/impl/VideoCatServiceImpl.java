package com.miguan.ballvideo.service.impl;

import com.miguan.ballvideo.mapper.VideoCatMapper;
import com.miguan.ballvideo.service.VideoCatService;
import com.miguan.ballvideo.vo.response.VideoCatRes;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 视频类型
 */
@Service
public class VideoCatServiceImpl implements VideoCatService {

    @Resource
    private VideoCatMapper videoCatMapper;

	@Override
	public List<VideoCatRes> findAll() {
		return videoCatMapper.findAll();
	}

}