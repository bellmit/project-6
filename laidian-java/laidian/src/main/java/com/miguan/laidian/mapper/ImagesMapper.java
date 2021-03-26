package com.miguan.laidian.mapper;

import com.miguan.laidian.vo.ImagesVo;

import java.util.List;
import java.util.Map;

/**
 * 图片列表Mapper
 * @author xy.chen
 * @date 2019-07-08
 **/

public interface ImagesMapper {

	/**
	 *
	 * 通过条件查询视频源列列表
	 *
	 **/
	List<ImagesVo>  findImagesList(Map<String, Object> params);

	/**
	 * 修改视频收藏、分享数量
	 **/
	int updateCount(Map<String, Object> params);

}