package com.miguan.ballvideo.service;

import com.miguan.ballvideo.entity.PushArticle;
import com.miguan.ballvideo.vo.ClUserVo;
import com.miguan.ballvideo.vo.FirstVideos;
import com.miguan.ballvideo.vo.video.HotListVo;
import com.miguan.ballvideo.vo.video.Videos161Vo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 用户表Service
 * @author xy.chen
 * @date 2019-08-09
 **/

public interface ClUserService {

	/**
	 * 
	 * 通过条件查询用户列表
	 * 
	 **/
	List<ClUserVo>  findClUserList(Map<String, Object> params);

	/**
	 * 
	 * 新增用户信息
	 * 
	 **/
	int saveClUser(ClUserVo clUserVo);

	/**
	 * 
	 * 修改用户信息
	 * 
	 **/
	int updateClUser(ClUserVo clUserVo);

	/**
	 * 用户登录
	 * @param request
	 * @param clUserVo 用户实体
	 * @param vcode 短信验证码
	 * @return
	 */
	Map<String, Object>  login(HttpServletRequest request, ClUserVo clUserVo, String vcode);

	/**
	 * 获取全部华为推送的tokens
	 * @param pushArticle
	 */
	List<String> findAllHuaweiTotken(PushArticle pushArticle);

	/**
	 * 获取全部小米推送的tokens,接口方法，传入的是 pushArticle 对象
	 * @param pushArticle
	 * @return
	 */
	List<String> findAllXiaoMiTotken(PushArticle pushArticle);

    void deleteByUserId(Long userId, String vcode);

	/**
	 * 获取全部Oppo推送的tokens,接口方法，传入的是 pushArticle 对象
	 * @param pushArticle
	 * @return
	 */
	public List<String> findAllOppoToken(PushArticle pushArticle);

	/**
	 * 获取全部推送用户的tokens，传入的是 HashMap
	 */
	List<ClUserVo> findAllTokens(Map<String, Object> params);

	/**
	 * 组装视频作者头像和名称
	 *
	 * @param firstVideos
	 * @return
	 */
	void packagingUserAndVideos(List<Videos161Vo> firstVideos);

	/**
	 * 热榜组装视频作者头像和名称
	 *
	 * @param hotList
	 */
	void packagingUserAndVideos2(List<HotListVo> hotList);

	/**
	 * 组装视频作者头像和名称
	 *
	 * @param firstVideosList
	 */
	void packagingUserAndfirstVideos(List<FirstVideos> firstVideosList);
}