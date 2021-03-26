package com.miguan.ballvideo.mapper;

import com.miguan.ballvideo.vo.ClUserVideoInfoVo;
import com.miguan.ballvideo.vo.ClUserVideosVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户视频关联表Mapper
 * @author xy.chen
 * @date 2019-08-09
 **/

public interface ClUserVideosMapper{

	/**
	 * 
	 * 通过条件查询用户视频关联列表
	 * 
	 **/
	List<ClUserVideosVo>  findClUserVideosList(Map<String, Object> params);

	/**
	 *
	 * 通过条件查询用户收藏数
	 *
	 **/
	int  findVideoCollectionSum(Map<String, Object> params);

	/**
	 * 
	 * 新增用户视频关联信息
	 * 
	 **/
	int saveClUserVideos(ClUserVideosVo clUserVideosVo);

	/**
	 * 
	 * 修改用户视频关联信息
	 * 
	 **/
	int updateClUserVideos(ClUserVideosVo clUserVideosVo);

	/**
	 * 批量删除用户收藏
	 *
	 * @param collectionIds
	 * @return
	 */
	int batchDelCollections(@Param("collectionIds") String[] collectionIds);


	/**
	 * 查询批量删除收藏的相关信息
	 *
	 * @param collectionIds
	 * @return
	 */
	List<ClUserVideosVo>  findCollectionsList(@Param("collectionIds") String[] collectionIds);

	/**
	 * 批量更新首页视频收藏数
	 *
	 * @param firstIds
	 * @return
	 */
	int batchUpdateFirstvideos(@Param("firstIds") String[] firstIds);

	/**
	 * 批量更新小视频收藏数
	 *
	 * @param smallIds
	 * @return
	 */
	int batchUpdateSmallvideos(@Param("smallIds") String[] smallIds);

	/**
	 * 查询视频点赞数
	 *
	 * @param videoType
	 * @return
	 */
	List<ClUserVideosVo> countLoveNum(@Param("videoType") Integer videoType,@Param("videoIds") List<Long> videoIds);

	/**
	 * 查询用户是否收藏
	 *
	 * @param userId
	 * @param videoIds
	 * @return
	 */
	List<Long> queryCollection(@Param("userId")String userId,@Param("videoIds") List<Long> videoIds);

	List<ClUserVideoInfoVo> queryUserVideoInfo(Map<String, Object> params);
}