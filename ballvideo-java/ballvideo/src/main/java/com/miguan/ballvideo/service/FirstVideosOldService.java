package com.miguan.ballvideo.service;

import com.github.pagehelper.Page;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.vo.FirstVideos;
import com.miguan.ballvideo.vo.video.FirstVideos16Vo;
import com.miguan.ballvideo.vo.video.FirstVideosNewVo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 首页视频源列表Service
 * @author xy.chen
 * @date 2019-08-09
 **/

public interface FirstVideosOldService {

	/**
	 * 通过条件查询首页视频源列列表(分页)
	 *
	 * @param params
	 * @return
	 **/
	Page<FirstVideos> findFirstVideosPage(Map<String, Object> params, int currentPage, int pageSize);

	/**
	 * 更新视频收藏数、点赞数、评论数、观看数、兴趣
	 *
	 * @param params
	 * @return
	 */
	@Transactional
	boolean updateVideosCount(Map<String, Object> params);

	/**
	 * 更新视频观看数到redis
	 *
	 * @param params
	 */
	boolean addRedisVideosWatchInfo(Map<String, Object> params);

	/**
	 * 通过条件查询首页视频源列列表
	 *
	 * @param params
	 * @return
	 **/
	List<FirstVideos> findFirstVideosList(Map<String, Object> params);

	/**
	 * 我的收藏视频展示
	 *
	 * @param
	 * @return
	 **/
	Page<FirstVideos> findMyCollection(String userId, String mobileType, String appVersion, int currentPage, int pageSize);

	/**
	 * 批量删除用户收藏
	 *
	 * @param collectionIds
	 * @return
	 */
	@Transactional
	int batchDelCollections(String[] collectionIds);


	Map<String, Object> getRandomVideosAndAdvert(AbTestAdvParamsVo queueVo, Map<String, Object> params);

	/**
	 * 通过条件查询首页视频源列列表
	 *
	 * @param params
	 * @param currentPage
	 * @return
	 */
	public FirstVideosNewVo firstVideosList13(Map<String, Object> params, int currentPage, int flag);

	/**
	 * 通过条件查询首页视频源列列表(分页)
	 *
	 * @param params
	 * @return
	 **/
	public FirstVideosNewVo findFirstVideosPage13(Map<String, Object> params, int currentPage, int flag);

	public FirstVideos16Vo firstVideosList16(Map<String, Object> params);

	/**
	 * 查询首页推荐列表
	 *
	 * @param params
	 * @return
	 */
	public FirstVideos16Vo firstRecommendVideosList16(Map<String, Object> params);

	public FirstVideos16Vo findFirstVideosPage16(Map<String, Object> params, int currentPage, int flag);

	boolean updateVideosCountSendMQ(Map<String, Object> params);

	/**
	 * 把视频Id放入Redis,计算真实权重
	 * @param videoId
	 */
	void setRealWeightRedis(Long videoId);

	void updateVideoRealWeightByRedis();

	@Transactional
	void updateVideosRealWeight(Map<Long, Long> params);

	FirstVideos findFirstVideosDetail(Map<String, Object> params);

	List<FirstVideos> findFirstVideosByUserId(Long userId);

    /**
     * 根据用户ID，更新视频作者和头像地址
     * @param author
     * @param bsyHeadUrl
     * @param userId
     * @return
     */
	int updatevideoAuthorAndbsyHeadUrlByUserId(String author, String bsyHeadUrl, Long userId);

}