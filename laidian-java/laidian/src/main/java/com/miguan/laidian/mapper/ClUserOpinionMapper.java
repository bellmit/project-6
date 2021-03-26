package com.miguan.laidian.mapper;


import com.miguan.laidian.vo.ClUserOpinionVo;

import java.util.List;
import java.util.Map;

/**
 * 用户意见反馈表Mapper
 * @author xy.chen
 * @date 2019-08-09
 **/

public interface ClUserOpinionMapper{


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
	List<ClUserOpinionVo> findClUserOpinionList(Map<String, Object> params);

	/**
	 * 
	 * 新增用户意见反馈信息
	 * 
	 **/
	int saveClUserOpinion(ClUserOpinionVo clUserOpinionVo);

	/**
	 *
	 * 用户已读（修改用户已读状态）
	 *
	 **/
	int updateUserOpinionState(String id);

	/**
	 *
	 * 查看用户已被回复的数量
	 *
	 **/
	int findClUserOpinionNumber(Map<String, Object> map);
}