package com.miguan.ballvideo.mapper;

import com.miguan.ballvideo.vo.SmallVideosVo;
import java.util.List;
import java.util.Map;

/**
 * 小视频列表Mapper
 * @author xy.chen
 * @date 2019-08-09
 **/

public interface SmallVideosMapper{

	/**
	 * 
	 * 通过条件查询小视频列列表(无用户ID)
	 * 
	 **/
	List<SmallVideosVo>  findSmallVideosList(Map<String, Object> params);

	/**
	 *
	 * 通过条件查询小视频列列表(有用户ID)
	 *
	 **/
	List<SmallVideosVo>  findSmallVideosListByUserId(Map<String, Object> params);

	/**
	 * 更新小视频收藏数、点赞数、评论数、观看数
	 *
	 * @param params
	 * @return
	 */
	int updateSmallVideosCount(Map<String, Object> params);
}