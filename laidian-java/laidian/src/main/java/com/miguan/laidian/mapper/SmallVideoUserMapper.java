package com.miguan.laidian.mapper;

import com.miguan.laidian.vo.SmallVideoUserVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户视频关联表Mapper
 * @author laiyd
 * @date 2019-08-22
 **/

public interface SmallVideoUserMapper {

	/**
	 *
	 * 通过条件查询用户视频关联列表
	 *
	 **/
	List<SmallVideoUserVo>  findClUserVideosList(Map<String, Object> params);

	/**
	 * 
	 * 新增用户视频关联信息
	 * 
	 **/
	int saveClUserVideos(SmallVideoUserVo clUserVideosVo);

	/**
	 * 
	 * 修改用户视频关联信息
	 * 
	 **/
	int updateClUserVideos(SmallVideoUserVo clUserVideosVo);

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
	List<SmallVideoUserVo>  findCollectionsList(@Param("collectionIds") String[] collectionIds);

	/**
	 * 批量更新小视频收藏数
	 *
	 * @param smallIds
	 * @return
	 */
	int batchUpdateSmallvideos(@Param("smallIds") Long[] smallIds);

	/**
	 * 通过userId查询所有被清空的收藏
	 * @param userId
	 * @return
	 */
	List<SmallVideoUserVo> findCollectionsListByUserId(@Param("userId") String userId);

	/**
	 * 清空所有的收藏 通过userId
	 * @param userId
	 * @return
	 */
	int emptyMyCollectionByUserId(@Param("userId")String userId);
}