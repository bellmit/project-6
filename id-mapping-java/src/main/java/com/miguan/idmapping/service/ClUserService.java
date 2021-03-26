package com.miguan.idmapping.service;


import com.miguan.idmapping.vo.ClUserVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 用户表Service
 * @author xy.chen
 * @date 2019-08-09
 **/

public interface ClUserService {

	/**
	 * 用户登录
	 *
	 * @param publicInfo
	 * @param commonAttr
	 * @param request
	 * @param clUserVo 用户实体
	 * @param vcode 短信验证码
	 * @return
	 */
	Map<String, Object>  login(String publicInfo, String commonAttr, HttpServletRequest request, ClUserVo clUserVo, String vcode);

	/**
	 * 微信登录
	 *
	 * @param clUserVo
	 * @param nickName
	 * @param headPic
	 * @return
	 */
	Map<String, Object> wecharLogin(HttpServletRequest request,String publicInfo, String commonAttr,ClUserVo clUserVo, String nickName, String headPic);

	/**
	 * 苹果登录
	 *
	 * @param request
	 * @param publicInfo
	 * @param commonAttr
	 * @param clUserVo
	 * @return
	 */
	Map<String, Object> appleLogin(HttpServletRequest request,String publicInfo,String commonAttr,ClUserVo clUserVo);

	void deleteByUserId(Long userId);

}