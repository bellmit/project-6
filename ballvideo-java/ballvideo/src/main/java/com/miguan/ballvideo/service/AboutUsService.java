package com.miguan.ballvideo.service;


import com.miguan.ballvideo.vo.AboutUsVo;

import java.util.List;
import java.util.Map;

/**
 * 关于我们Service
 * @author xy.chen
 * @date 2019-08-23
 **/

public interface AboutUsService {


	/**
	 * 
	 * 通过条件查询关于我们列表
	 * 
	 **/
	List<AboutUsVo>  findAboutUsList(Map<String, Object> params);

}