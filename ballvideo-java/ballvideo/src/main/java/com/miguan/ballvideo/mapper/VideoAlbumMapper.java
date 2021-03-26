package com.miguan.ballvideo.mapper;

import com.miguan.ballvideo.redis.util.CacheConstant;
import com.miguan.ballvideo.vo.VideoAlbumVo;
import com.miguan.ballvideo.vo.video.PackagingAlbumVo;
import com.miguan.ballvideo.vo.video.VideoAlbumDetailVo;
import com.miguan.ballvideo.vo.video.Videos161Vo;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

/**
 * 视频专辑表Mapper
 * @author xy.chen
 * @date 2020-06-02
 **/

public interface VideoAlbumMapper{

	/**
	 * 
	 * 查询视频专辑列表
	 * 
	 **/
	List<VideoAlbumVo>  findVideoAlbumList();

	/**
	 *
	 * 根据专辑Ids查询视频专辑列表
	 *
	 **/
	List<VideoAlbumVo>  findVideoAlbumListById(@Param("albumIds") String albumIds);

	/**
	 * 查询专辑详情列表(无用户ID)
	 *
	 * @param videoAlbumId
	 * @return
	 */
	List<Videos161Vo> findVideoAlbumDetailList(Long videoAlbumId);

	/**
	 * 查询专辑详情列表(有用户ID)
	 *
	 * @param params
	 * @return
	 */
	List<Videos161Vo> findVideoAlbumDetailListByUserId(Map<String, Object> params);

	/**
	 * 根据专辑ID和视频ID查询视频专辑标题
	 *
	 * @param params
	 * @return
	 */
	PackagingAlbumVo findVideoAlbumDetail(Map<String, Object> params);

	/**
	 * 查询全部专辑ID和视频ID及权重
	 * @return
	 */
	List<VideoAlbumDetailVo> findVideoAlbumDetailAll(Map<String, Object> param);

	/**
	 * 分页查询视频专辑列表
	 * @param params
	 * @return
	 */
	List<VideoAlbumVo> findVideoAlbumAll(Map<String, Object> params);

	/**
	 * 根据视频Id查询所属专辑
	 * @param videoId
	 * @return
	 */
	Long findVideoAlbumByVideoId(Long videoId);

	VideoAlbumVo findById(Long albumId);

	/**
	 * 查询全部专辑Id和专辑标题
	 * @return
	 */
	@Cacheable(value = CacheConstant.FIND_ALBUM_TITLE, unless = "#result == null || #result.size()==0")
	List<VideoAlbumVo> findAlbumTitleByAll();
}