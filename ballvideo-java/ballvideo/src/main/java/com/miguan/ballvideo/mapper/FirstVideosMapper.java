package com.miguan.ballvideo.mapper;

import com.miguan.ballvideo.dto.FirstVideosDto;
import com.miguan.ballvideo.redis.util.CacheConstant;
import com.miguan.ballvideo.vo.FirstVideos;
import com.miguan.ballvideo.vo.UserCenterVo;
import com.miguan.ballvideo.vo.video.FirstVideosPublicationVo;
import com.miguan.ballvideo.vo.video.RealWeightCalculateVo;
import com.miguan.ballvideo.vo.video.Videos161Vo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

/**
 * 首页视频源列表Mapper
 * @author xy.chen
 * @date 2019-08-09
 **/

public interface FirstVideosMapper{

	/**
	 * 通过条件查询首页视频源列列表（无用户ID）
	 *
	 * @param params
	 * @return
	 **/
	List<FirstVideos> findFirstVideosList(Map<String, Object> params);

	/**
	 * 通过条件查询首页视频源列列表（有用户ID）
	 *
	 * @param params
	 * @return
	 **/
	List<FirstVideos> findFirstVideosListByUserId(Map<String, Object> params);

	/**
	 * 通过条件查询首页视频源列列表（无用户ID）
	 * @param params
	 * @return
	 */
	List<FirstVideos> findFirstVideosDetailList(Map<String, Object> params);

	/**
	 * 通过条件查询首页视频详情（有用户ID）
	 *
	 * @param params
	 * @return
	 **/
	List<FirstVideos> findFirstVideosDetailByUserId(Map<String, Object> params);

	/**
	 * 更新首页视频收藏数、点赞数、评论数、观看数
	 *
	 * @param params
	 * @return
	 */
	int updateFirstVideosCount(Map<String, Object> params);

	/**
	 * 更新首页视频评论数
	 *
	 * @param params
	 * @return
	 */
	int updateCommentCount(Map<String, Object> params);

	/**
	 * 批量更新首页视频观看数
	 *
	 * @param params
	 * @return
	 */
	int updateFirstVideosCountBatch(Map<String, Object> params);

	/**
	 * 根据
	 * @param id
	 * @return
	 */
	FirstVideos getFirstVideosById(Long id);

	/**
	 * 根据
	 * @param id
	 * @return
	 */
	FirstVideos getFirstVideosTitleById(Long id);

	/**
	 * 查询
	 * @param
	 * @return
	 */
	List<FirstVideos> findFirstVideoListByMyCollection(Map<String, Object> map);

	/**
	 * 查询全部首页视频列表
	 *
	 * @return
	 **/
	List<FirstVideos> findAllFirstVideosList();

	/**
	 * 通过条件查询首页视频源列列表（无用户ID）v1.6.1
	 *
	 * @param params
	 * @return
	 **/
	List<Videos161Vo> findFirstVideosList161(Map<String, Object> params);

	/**
	 * 通过条件查询首页视频源列列表（有用户ID）v1.6.1
	 *
	 * @param params
	 * @return
	 **/
	List<Videos161Vo> findFirstVideosListByUserId161(Map<String, Object> params);

	List<Videos161Vo> findFirstVideosList18(Map<String, Object> params);

	List<Videos161Vo> findFirstVideosListByUserId18(Map<String, Object> params);

	@Cacheable(value = CacheConstant.CATID_BY_VIDEOID, unless = "#result == null")
	@Select("SELECT cat_id FROM first_videos where id = #{videoId}")
    String findCatIdByVideoId(@Param("videoId") Long videoId);

	/**
	 * 更新视频合集Id为0
	 * @param gatherId
	 * @return
	 */
	int updateFirstVideosGatherId(Long gatherId);

	/**
	 * 根据Ids查询真实权重
	 * @param
	 * @return
	 */
	List<FirstVideos> getFirstVideosByIds(@Param("ids")String ids);

	List<RealWeightCalculateVo> calculateByIds(@Param("ids")String ids);

	void updateFirstVideosRealWeight(@Param("sql") String sql);

	/**
	 * 查询需要预热的数据
	 *
	 * @param param
	 * @return
	 */
	List<Videos161Vo> findBsyUrlList(Map<String, Object> param);

	int saveVideo(FirstVideosDto firstVideosDto);

	/**
	 * 根据视频Id,用户ID,包名，删除发布的视频
	 * @param id
	 * @param userId
	 * @return
	 */
	int deletePublicationVideoByIdAndUserIdAndAndAppPackage(@Param("id") Long id, @Param("userId") String userId);

	/**
	 * 根据用户ID查询发布的视频列表
	 * @param param
	 * @return
	 */
	List<FirstVideosPublicationVo> selectPublicationVideoByUserId(Map<String, Object> param);

	/**
	 * 用户中心统计
	 * @param param
	 * @return
	 */
	UserCenterVo countUserCenter(Map<String, Object> param);

	int updatevideoAuthorAndbsyHeadUrlByUserId(@Param("videoAuthor") String videoAuthor, @Param("bsyHeadUrl") String bsyHeadUrl, @Param("userId") Long userId);

    /**
     * 更新首页视频点击数、展示数和ctr值
     *
     * @param params
     * @return
     */
    int updateFirstVideosCtr(Map<String, Object> params);

	/**
	 * 查询出用户上传的短视频id列表
	 * @param userId
	 * @return
	 */
	List<String> findIdListByUserId(@Param("userId") Long userId);

	/**
	 * 查询视频少于20秒的数据
	 *
	 * @return
	 */
	List<String> findFirstVideoIdsOne();

	/**
	 * 查询视频标题少于5个字的数据
	 *
	 * @return
	 */
	List<String> findFirstVideoIdsTwo();

    FirstVideos findFirstVideosById(Long id);

	@Cacheable(value = CacheConstant.IMMERSE_VIDEO_LIST, unless = "#result == null")
	List<Videos161Vo> findFirstVideosListById(Map<String, Object> params);

	List<Videos161Vo> findSpecialVideosListById(@Param("videoIds")String videoIds);
}