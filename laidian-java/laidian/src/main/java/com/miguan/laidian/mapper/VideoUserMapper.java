package com.miguan.laidian.mapper;

import com.miguan.laidian.vo.VideoUserVo;

import java.util.Map;

/**
 * 用户视频关联表Mapper
 * @author shixh
 * @date 2019-09-29
 **/

public interface VideoUserMapper {

	VideoUserVo findOne(Map<String, Object> params);

	void save(VideoUserVo videoUserVo);

	void updateCount(Map<String, Object> params);
}