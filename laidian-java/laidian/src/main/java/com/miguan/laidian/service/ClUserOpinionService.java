package com.miguan.laidian.service;


import com.github.pagehelper.Page;
import com.miguan.laidian.vo.ClUserOpinionVo;

import java.util.List;
import java.util.Map;

/**
 * 用户意见反馈表Service
 * @author xy.chen
 * @date 2019-08-09
 **/

public interface ClUserOpinionService {

	/**
	 * 
	 * 通过ID查询用户意见反馈信息
	 * 
	 **/
	ClUserOpinionVo getClUserOpinionById(String id);

	/**
	 * 
	 * 通过条件查询用户意见反馈列表
	 * 
	 **/
	Page<ClUserOpinionVo> findClUserOpinionList(ClUserOpinionVo clUserOpinionVo, int currentPage, int pageSize);

	/**
	 * 
	 * 新增用户意见反馈信息
	 * 
	 **/
	int saveClUserOpinion(ClUserOpinionVo clUserOpinionVo);
}