package com.miguan.laidian.service;


import com.miguan.laidian.entity.PushArticle;
import com.miguan.laidian.vo.ClUserVo;

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
	List<ClUserVo> findClUserList(Map<String, Object> params);

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
	 * @param appType   马甲包类型
	 * @param clUserVo  用户信息
	 * @param vcode     短信验证码
	 * @return
	 */
	Map<String, Object>  login(String appType, ClUserVo clUserVo, String vcode);

	/**
	 * 获取全部推送用户的tokens
	 */
	List<ClUserVo> findAllTokens();

}